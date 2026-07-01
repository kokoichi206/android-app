package jp.mydns.kokoichi206.quietlauncher.logic

import jp.mydns.kokoichi206.quietlauncher.model.AppEntry
import jp.mydns.kokoichi206.quietlauncher.model.CalEvent

/**
 * A-1 のキーワード辞書（spec 7）。
 * A-3 ではこの対応表を LLM 生成辞書や embedding に置き換える。
 */
object CalendarDictionary {

    private val appKeywords: Map<String, List<String>> = mapOf(
        "golf" to listOf("golf", "ゴルフ", "スコア"),
        "trip" to listOf("map", "maps", "マップ", "地図", "乗換", "transit"),
        "photo" to listOf("photo", "フォト", "camera", "カメラ", "gallery"),
    )

    val events: List<CalEvent> = listOf(
        CalEvent("none", "予定なし", null),
        CalEvent("golf", "⛳ ゴルフ 15:30", "golf"),
        CalEvent("trip", "🚄 出張 大阪", "trip"),
        CalEvent("photo", "📸 発表会 13:00", "photo"),
    )

    fun matches(event: CalEvent?, app: AppEntry): Boolean {
        val keywords = event?.category?.let { appKeywords[it] } ?: return false
        val haystack = "${app.label} ${app.packageName}".lowercase()
        return keywords.any { haystack.contains(it.lowercase()) }
    }
}
