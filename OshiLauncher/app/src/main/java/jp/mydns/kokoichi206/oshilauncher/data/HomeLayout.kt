package jp.mydns.kokoichi206.oshilauncher.data

import org.json.JSONArray
import org.json.JSONObject

/** ホームのグリッド寸法（1ページあたり）。ページは左右に複数。 */
object Grid {
    const val COLUMNS = 4
    const val ROWS = 6
}

enum class ItemType { APP, WIDGET }

/**
 * ホームに配置された1要素。
 * APP: key = packageName。WIDGET: key = appWidgetId。
 * page でページ、(cellX, cellY) でそのページ内のセル左上、(spanX, spanY) で占有セル数。
 */
data class LayoutItem(
    val type: ItemType,
    val key: String,
    val cellX: Int,
    val cellY: Int,
    val spanX: Int = 1,
    val spanY: Int = 1,
    val page: Int = 0,
) {
    fun toJson(): JSONObject = JSONObject().apply {
        put("t", type.name)
        put("k", key)
        put("x", cellX)
        put("y", cellY)
        put("sx", spanX)
        put("sy", spanY)
        put("p", page)
    }

    companion object {
        fun fromJson(o: JSONObject) = LayoutItem(
            type = runCatching { ItemType.valueOf(o.getString("t")) }.getOrDefault(ItemType.APP),
            key = o.getString("k"),
            cellX = o.getInt("x"),
            cellY = o.getInt("y"),
            spanX = o.optInt("sx", 1),
            spanY = o.optInt("sy", 1),
            page = o.optInt("p", 0),
        )
    }
}

data class HomeLayout(val items: List<LayoutItem> = emptyList()) {

    fun toJsonString(): String =
        JSONArray().apply { items.forEach { put(it.toJson()) } }.toString()

    fun pageCount(): Int = (items.maxOfOrNull { it.page } ?: 0) + 1

    fun itemsOnPage(page: Int): List<LayoutItem> = items.filter { it.page == page }

    /** 指定ページの指定セルを占有しているアイテム（無ければ null）。 */
    fun itemAt(page: Int, x: Int, y: Int): LayoutItem? = items.firstOrNull { item ->
        item.page == page &&
            x in item.cellX until (item.cellX + item.spanX) &&
            y in item.cellY until (item.cellY + item.spanY)
    }

    /** 指定ページの最初の空きセル（埋まっていれば null）。 */
    fun firstEmptyCell(page: Int): Pair<Int, Int>? {
        for (y in 0 until Grid.ROWS) {
            for (x in 0 until Grid.COLUMNS) {
                if (itemAt(page, x, y) == null) return x to y
            }
        }
        return null
    }

    /** 優先ページ→既存ページ→新規ページ の順で空きスロットを返す (page, x, y)。 */
    fun firstFreeSlot(preferredPage: Int): Triple<Int, Int, Int> {
        firstEmptyCell(preferredPage)?.let { return Triple(preferredPage, it.first, it.second) }
        for (p in 0..pageCount()) {
            firstEmptyCell(p)?.let { return Triple(p, it.first, it.second) }
        }
        return Triple(pageCount(), 0, 0)
    }

    companion object {
        fun fromJsonString(raw: String?): HomeLayout {
            if (raw.isNullOrEmpty()) return HomeLayout()
            val arr = JSONArray(raw)
            return HomeLayout((0 until arr.length()).map { LayoutItem.fromJson(arr.getJSONObject(it)) })
        }
    }
}
