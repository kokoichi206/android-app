package jp.mydns.kokoichi206.quietlauncher.model

enum class TimeBucket(val label: String) {
    MORNING("朝"),
    DAY("昼"),
    NIGHT("夜"),
}

enum class DayType(val label: String) {
    WEEKDAY("平日"),
    HOLIDAY("休日"),
}

/** PoC ではカレンダー実連携の代わりにダミー予定を使う（spec 5.3）。 */
data class CalEvent(
    val id: String,
    val label: String,
    val category: String?,
)

data class LauncherContext(
    val bucket: TimeBucket,
    val dayType: DayType,
    val event: CalEvent?,
) {
    val cellKey: String get() = "${bucket.name}_${dayType.name}"
}

data class AppEntry(
    val packageName: String,
    val label: String,
)

/**
 * 読み出し時点で減衰適用済みのカウンタ値。
 * cells のキーは "packageName|BUCKET_DAYTYPE"。
 */
data class AppStats(
    val frecency: Map<String, Double>,
    val cells: Map<String, Double>,
)

data class ScoreBreakdown(
    val packageName: String,
    val label: String,
    val freq: Double,
    val time: Double,
    val cal: Double,
) {
    val total: Double get() = freq + time + cal
}

data class RecomputeResult(
    val slots: List<String>,
    val swappedIn: Set<String>,
    val suggestion: String?,
    val messages: List<String>,
    val scores: List<ScoreBreakdown>,
    val coldStart: Boolean,
)
