package jp.mydns.kokoichi206.quietlauncher

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import jp.mydns.kokoichi206.quietlauncher.data.AppRepository
import jp.mydns.kokoichi206.quietlauncher.data.DemoSeeder
import jp.mydns.kokoichi206.quietlauncher.data.StatsStore
import jp.mydns.kokoichi206.quietlauncher.logic.CalendarDictionary
import jp.mydns.kokoichi206.quietlauncher.logic.ScoreEngine
import jp.mydns.kokoichi206.quietlauncher.model.AppEntry
import jp.mydns.kokoichi206.quietlauncher.model.DayType
import jp.mydns.kokoichi206.quietlauncher.model.LauncherContext
import jp.mydns.kokoichi206.quietlauncher.model.ScoreBreakdown
import jp.mydns.kokoichi206.quietlauncher.model.TimeBucket
import java.util.Calendar

class LauncherState(
    private val repo: AppRepository,
    private val store: StatsStore,
) {
    var apps by mutableStateOf(emptyList<AppEntry>())
        private set
    var pinned by mutableStateOf(emptyList<AppEntry>())
        private set
    var slots by mutableStateOf(emptyList<AppEntry>())
        private set
    var swappedIn by mutableStateOf(emptySet<String>())
        private set
    var suggestion by mutableStateOf<AppEntry?>(null)
        private set
    var messages by mutableStateOf(emptyList<String>())
        private set
    var scores by mutableStateOf(emptyList<ScoreBreakdown>())
        private set
    var coldStart by mutableStateOf(false)
        private set

    // デバッグパネルからのオーバーライド (spec 5.3)
    var bucketOverride by mutableStateOf<TimeBucket?>(null)
        private set
    var dayOverride by mutableStateOf<DayType?>(null)
        private set
    var event by mutableStateOf(CalendarDictionary.events.first())
        private set

    fun effectiveContext(): LauncherContext {
        val now = Calendar.getInstance()
        val bucket = bucketOverride ?: when (now.get(Calendar.HOUR_OF_DAY)) {
            in 5..9 -> TimeBucket.MORNING
            in 10..16 -> TimeBucket.DAY
            else -> TimeBucket.NIGHT
        }
        val dayOfWeek = now.get(Calendar.DAY_OF_WEEK)
        val dayType = dayOverride
            ?: if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
                DayType.HOLIDAY
            } else {
                DayType.WEEKDAY
            }
        return LauncherContext(bucket, dayType, event.takeIf { it.category != null })
    }

    fun refresh(trigger: String) {
        apps = repo.loadApps()
        pinned = resolvePinned(apps)
        recompute(trigger)
    }

    fun recompute(trigger: String) {
        val ctx = effectiveContext()
        val pinnedPackages = pinned.mapTo(mutableSetOf()) { it.packageName }
        val candidates = apps.filter { it.packageName !in pinnedPackages }
        val result = ScoreEngine.recompute(candidates, store.load(), ctx, store.loadSlots())
        store.saveSlots(result.slots)

        val byPkg = apps.associateBy { it.packageName }
        slots = result.slots.mapNotNull { byPkg[it] }
        swappedIn = result.swappedIn
        suggestion = result.suggestion?.let { byPkg[it] }
        scores = result.scores
        coldStart = result.coldStart
        messages = listOf(
            "[$trigger] ${ctx.bucket.label}・${ctx.dayType.label}・${event.label}",
        ) + result.messages
    }

    /** 起動の記録のみ行い、再計算は復帰時に回す。目の前で入れ替えないため (spec 3.5)。 */
    fun onAppClick(app: AppEntry) {
        store.recordLaunch(app.packageName, effectiveContext())
        repo.launch(app.packageName)
    }

    fun setBucket(bucket: TimeBucket?) {
        bucketOverride = bucket
        recompute("バケット変更: ${bucket?.label ?: "自動"}")
    }

    fun setDayType(dayType: DayType?) {
        dayOverride = dayType
        recompute("曜日タイプ変更: ${dayType?.label ?: "自動"}")
    }

    fun selectEvent(newEvent: jp.mydns.kokoichi206.quietlauncher.model.CalEvent) {
        event = newEvent
        recompute("予定変更: ${newEvent.label}")
    }

    fun seedDemo() {
        val applied = DemoSeeder.seed(store, apps.mapTo(mutableSetOf()) { it.packageName })
        recompute("デモデータ投入 (${applied.size} 件)")
    }

    fun resetStats() {
        store.reset()
        recompute("統計リセット")
    }

    private fun resolvePinned(all: List<AppEntry>): List<AppEntry> {
        fun find(packages: List<String>, labelKeys: List<String>): AppEntry? =
            packages.firstNotNullOfOrNull { pkg -> all.find { it.packageName == pkg } }
                ?: all.find { app -> labelKeys.any { app.label.lowercase().contains(it) } }

        val phone = find(
            listOf("com.google.android.dialer", "com.android.dialer"),
            listOf("phone", "電話"),
        )
        val camera = find(
            listOf("com.android.camera2", "com.google.android.GoogleCamera", "com.android.camera"),
            listOf("camera", "カメラ"),
        )
        // 既定 2 つが見つからない端末ではラベル辞書順の先頭で代替 (spec 5.1)
        return listOfNotNull(phone, camera).distinctBy { it.packageName }
            .ifEmpty { all.take(2) }
    }
}
