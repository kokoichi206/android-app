package jp.mydns.kokoichi206.quietlauncher.data

import android.content.Context
import jp.mydns.kokoichi206.quietlauncher.model.AppStats
import jp.mydns.kokoichi206.quietlauncher.model.LauncherContext
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.pow

/**
 * 減衰カウンタの永続化（spec 4）。
 * PoC のため SharedPreferences + JSON。本実装で Room に置き換える前提の再生成可能な実装層。
 */
class StatsStore(
    context: Context,
    private val clock: () -> Long = System::currentTimeMillis,
) {
    private val prefs = context.getSharedPreferences("quiet_launcher_stats", Context.MODE_PRIVATE)

    companion object {
        const val HALF_LIFE_DAYS = 7.0
        private const val KEY_FRECENCY = "frecency"
        private const val KEY_CELLS = "cells"
        private const val KEY_SLOTS = "slots"
        private const val DAY_MILLIS = 86_400_000.0
    }

    fun recordLaunch(packageName: String, ctx: LauncherContext) {
        addTo(KEY_FRECENCY, packageName, 1.0)
        addTo(KEY_CELLS, "$packageName|${ctx.cellKey}", 1.0)
    }

    /** デモデータ投入用。既存値に積み増しせず上書きする。 */
    fun put(key: String, frecency: Double, cells: Map<String, Double>) {
        setValue(KEY_FRECENCY, key, frecency)
        cells.forEach { (cellKey, v) -> setValue(KEY_CELLS, "$key|$cellKey", v) }
    }

    fun load(): AppStats {
        val now = clock()
        return AppStats(
            frecency = readDecayed(KEY_FRECENCY, now),
            cells = readDecayed(KEY_CELLS, now),
        )
    }

    fun loadSlots(): List<String> {
        val raw = prefs.getString(KEY_SLOTS, null) ?: return emptyList()
        val arr = JSONArray(raw)
        return (0 until arr.length()).map { arr.getString(it) }
    }

    fun saveSlots(slots: List<String>) {
        prefs.edit().putString(KEY_SLOTS, JSONArray(slots).toString()).apply()
    }

    fun reset() {
        prefs.edit().clear().apply()
    }

    private fun decay(value: Double, ts: Long, now: Long): Double {
        val days = (now - ts).coerceAtLeast(0) / DAY_MILLIS
        return value * 2.0.pow(-days / HALF_LIFE_DAYS)
    }

    private fun readDecayed(prefKey: String, now: Long): Map<String, Double> {
        val json = JSONObject(prefs.getString(prefKey, null) ?: "{}")
        val result = mutableMapOf<String, Double>()
        json.keys().forEach { key ->
            val o = json.getJSONObject(key)
            result[key] = decay(o.getDouble("v"), o.getLong("ts"), now)
        }
        return result
    }

    private fun addTo(prefKey: String, key: String, delta: Double) {
        val now = clock()
        val json = JSONObject(prefs.getString(prefKey, null) ?: "{}")
        val current = if (json.has(key)) {
            val o = json.getJSONObject(key)
            decay(o.getDouble("v"), o.getLong("ts"), now)
        } else {
            0.0
        }
        json.put(key, JSONObject().put("v", current + delta).put("ts", now))
        prefs.edit().putString(prefKey, json.toString()).apply()
    }

    private fun setValue(prefKey: String, key: String, value: Double) {
        val json = JSONObject(prefs.getString(prefKey, null) ?: "{}")
        json.put(key, JSONObject().put("v", value).put("ts", clock()))
        prefs.edit().putString(prefKey, json.toString()).apply()
    }
}
