package jp.mydns.kokoichi206.quietlauncher.logic

import jp.mydns.kokoichi206.quietlauncher.model.AppEntry
import jp.mydns.kokoichi206.quietlauncher.model.AppStats
import jp.mydns.kokoichi206.quietlauncher.model.LauncherContext
import jp.mydns.kokoichi206.quietlauncher.model.RecomputeResult
import jp.mydns.kokoichi206.quietlauncher.model.ScoreBreakdown
import java.util.Locale

/**
 * A-1 集計ベースのスコアリングとヒステリシス（spec 3）。
 * Android 非依存の純粋ロジック。
 */
object ScoreEngine {

    const val THETA = 8.0
    const val W_FREQ = 0.35
    const val W_TIME = 0.45
    const val W_CAL = 0.20
    const val SLOT_COUNT = 4

    /**
     * @param apps ピン留めを除いたランキング対象アプリ
     * @param currentSlots 現在のスロット（枠位置順のパッケージ名）
     */
    fun recompute(
        apps: List<AppEntry>,
        stats: AppStats,
        ctx: LauncherContext,
        currentSlots: List<String>,
        slotCount: Int = SLOT_COUNT,
    ): RecomputeResult {
        val messages = mutableListOf<String>()

        val maxFreq = apps.maxOfOrNull { stats.frecency[it.packageName] ?: 0.0 } ?: 0.0
        val maxTime = apps.maxOfOrNull { stats.cells[cellKeyOf(it, ctx)] ?: 0.0 } ?: 0.0

        val scores = apps.map { app ->
            val f = normalize(stats.frecency[app.packageName] ?: 0.0, maxFreq)
            val t = normalize(stats.cells[cellKeyOf(app, ctx)] ?: 0.0, maxTime)
            val c = if (CalendarDictionary.matches(ctx.event, app)) 100.0 else 0.0
            ScoreBreakdown(app.packageName, app.label, W_FREQ * f, W_TIME * t, W_CAL * c)
        }.sortedWith(compareByDescending<ScoreBreakdown> { it.total }.thenBy { it.label.lowercase() })

        val byPkg = scores.associateBy { it.packageName }
        val coldStart = (scores.firstOrNull()?.total ?: 0.0) <= 0.0
        if (coldStart) {
            messages += "データ不足: 全スコア 0 のためラベル辞書順で配置（設計されたバックオフ）"
        }

        val topN = scores.take(slotCount).map { it.packageName }
        val installed = apps.mapTo(mutableSetOf()) { it.packageName }
        val slots = currentSlots.filter { it in installed }.toMutableList()
        val swappedIn = mutableSetOf<String>()

        if (slots.isEmpty()) {
            slots += topN
            messages += "初期配置: スコア上位 ${slots.size} 件を配置"
        } else {
            if (slots.size < slotCount) {
                val fills = scores.map { it.packageName }
                    .filter { it !in slots }
                    .take(slotCount - slots.size)
                slots += fills
                swappedIn += fills
                if (fills.isNotEmpty()) {
                    messages += "空き枠を補充: ${fills.joinToString { labelOf(byPkg, it) }}"
                }
            }
            val candidates = topN.filter { it !in slots }
                .sortedByDescending { byPkg.getValue(it).total }
            for (candidate in candidates) {
                val out = slots.filter { it !in topN }
                    .minByOrNull { byPkg[it]?.total ?: 0.0 }
                    ?: break
                val diff = byPkg.getValue(candidate).total - (byPkg[out]?.total ?: 0.0)
                if (diff > THETA) {
                    slots[slots.indexOf(out)] = candidate
                    swappedIn += candidate
                    messages += "入替: ${labelOf(byPkg, out)} → ${labelOf(byPkg, candidate)}" +
                        "（差 ${fmt(diff)} > θ=${fmt(THETA)}）"
                } else {
                    messages += "保留: ${labelOf(byPkg, candidate)}（${fmt(byPkg.getValue(candidate).total)}）は " +
                        "${labelOf(byPkg, out)}（${fmt(byPkg[out]?.total ?: 0.0)}）を上回るが " +
                        "差 ${fmt(diff)} ≤ θ=${fmt(THETA)} のため入替しない（ガタつき防止）"
                    // 候補はスコア降順なので以降も成立しない
                    break
                }
            }
            if (swappedIn.isEmpty() && messages.none { it.startsWith("保留") }) {
                messages += "入替なし（現状維持）"
            }
        }

        var suggestion: String? = null
        if (ctx.event?.category != null) {
            val matched = scores.filter { it.cal > 0.0 }
            suggestion = matched.firstOrNull { it.packageName !in slots }?.packageName
            messages += when {
                matched.isEmpty() ->
                    "提案枠: 『${ctx.event.label}』にマッチするアプリなし → 出さない"
                suggestion == null ->
                    "提案枠: マッチするアプリは全てスロット内 → 出さない"
                else ->
                    "提案枠: ${labelOf(byPkg, suggestion)}（予定『${ctx.event.label}』から）"
            }
        }

        return RecomputeResult(
            slots = slots,
            swappedIn = swappedIn,
            suggestion = suggestion,
            messages = messages,
            scores = scores,
            coldStart = coldStart,
        )
    }

    private fun cellKeyOf(app: AppEntry, ctx: LauncherContext): String =
        "${app.packageName}|${ctx.cellKey}"

    private fun normalize(value: Double, max: Double): Double =
        if (max > 0.0) value / max * 100.0 else 0.0

    private fun labelOf(byPkg: Map<String, ScoreBreakdown>, pkg: String): String =
        byPkg[pkg]?.label ?: pkg

    private fun fmt(v: Double): String = String.format(Locale.US, "%.1f", v)
}
