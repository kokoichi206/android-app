package jp.mydns.kokoichi206.oshilauncher.data

import org.json.JSONArray
import org.json.JSONObject

/** ホームのグリッド寸法（単一ページ。複数ページは将来拡張）。 */
object Grid {
    const val COLUMNS = 4
    const val ROWS = 6
}

enum class ItemType { APP, WIDGET }

/**
 * ホームに配置された1要素。
 * APP: key = packageName。WIDGET: key = appWidgetId（Phase 2 で使用）。
 * セル左上を (cellX, cellY)、占有セル数を (spanX, spanY) で表す。
 */
data class LayoutItem(
    val type: ItemType,
    val key: String,
    val cellX: Int,
    val cellY: Int,
    val spanX: Int = 1,
    val spanY: Int = 1,
) {
    fun toJson(): JSONObject = JSONObject().apply {
        put("t", type.name)
        put("k", key)
        put("x", cellX)
        put("y", cellY)
        put("sx", spanX)
        put("sy", spanY)
    }

    companion object {
        fun fromJson(o: JSONObject) = LayoutItem(
            type = runCatching { ItemType.valueOf(o.getString("t")) }.getOrDefault(ItemType.APP),
            key = o.getString("k"),
            cellX = o.getInt("x"),
            cellY = o.getInt("y"),
            spanX = o.optInt("sx", 1),
            spanY = o.optInt("sy", 1),
        )
    }
}

/** 配置全体。セル占有判定や空きセル探索のヘルパを持つ。 */
data class HomeLayout(val items: List<LayoutItem> = emptyList()) {

    fun toJsonString(): String =
        JSONArray().apply { items.forEach { put(it.toJson()) } }.toString()

    /** 指定セルを占有しているアイテム（無ければ null）。 */
    fun itemAt(x: Int, y: Int): LayoutItem? = items.firstOrNull { item ->
        x in item.cellX until (item.cellX + item.spanX) &&
            y in item.cellY until (item.cellY + item.spanY)
    }

    /** 左上から走査した最初の空きセル（埋まっていれば null）。 */
    fun firstEmptyCell(): Pair<Int, Int>? {
        for (y in 0 until Grid.ROWS) {
            for (x in 0 until Grid.COLUMNS) {
                if (itemAt(x, y) == null) return x to y
            }
        }
        return null
    }

    companion object {
        fun fromJsonString(raw: String?): HomeLayout {
            if (raw.isNullOrEmpty()) return HomeLayout()
            val arr = JSONArray(raw)
            return HomeLayout((0 until arr.length()).map { LayoutItem.fromJson(arr.getJSONObject(it)) })
        }
    }
}
