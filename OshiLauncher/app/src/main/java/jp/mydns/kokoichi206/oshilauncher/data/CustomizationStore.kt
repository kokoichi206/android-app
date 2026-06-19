package jp.mydns.kokoichi206.oshilauncher.data

import android.content.Context
import android.net.Uri
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import kotlin.random.Random

/**
 * 壁紙スライドショーとアイコン差し替えの永続化。SharedPreferences + JSON（PoC）。
 * launcher 本体（ホーム背景）とライブ壁紙サービスの両方から読まれ、画像セットを共有する。
 *
 * 画像実体は filesDir 配下へコピーして持つ。Photo Picker が返す content URI は一時許可で、
 * そのまま保存しても次回読めなくなるため。
 */
class CustomizationStore(private val context: Context) {

    private val prefs =
        context.getSharedPreferences("oshi_launcher_customization", Context.MODE_PRIVATE)
    private val wallpaperDir = File(context.filesDir, "wallpapers").apply { mkdirs() }
    private val iconDir = File(context.filesDir, "icons").apply { mkdirs() }

    companion object {
        private const val KEY_WALLPAPERS = "wallpapers"
        private const val KEY_INTERVAL = "interval_seconds"
        private const val KEY_ICONS = "icons"
        const val DEFAULT_INTERVAL_SECONDS = 3
        const val MIN_INTERVAL_SECONDS = 1
        const val MAX_INTERVAL_SECONDS = 60
    }

    // --- 壁紙スライドショー ---

    fun wallpaperFiles(): List<File> =
        wallpaperNames().map { File(wallpaperDir, it) }.filter { it.exists() }

    fun intervalSeconds(): Int =
        prefs.getInt(KEY_INTERVAL, DEFAULT_INTERVAL_SECONDS)
            .coerceIn(MIN_INTERVAL_SECONDS, MAX_INTERVAL_SECONDS)

    fun setIntervalSeconds(seconds: Int) {
        prefs.edit()
            .putInt(KEY_INTERVAL, seconds.coerceIn(MIN_INTERVAL_SECONDS, MAX_INTERVAL_SECONDS))
            .apply()
    }

    fun addWallpaper(uri: Uri): String {
        val name = copyToDir(uri, wallpaperDir)
        saveWallpaperNames(wallpaperNames() + name)
        return name
    }

    fun removeWallpaper(name: String) {
        File(wallpaperDir, name).delete()
        saveWallpaperNames(wallpaperNames().filter { it != name })
    }

    private fun wallpaperNames(): List<String> {
        val raw = prefs.getString(KEY_WALLPAPERS, null) ?: return emptyList()
        val arr = JSONArray(raw)
        return (0 until arr.length()).map { arr.getString(it) }
    }

    private fun saveWallpaperNames(names: List<String>) {
        prefs.edit().putString(KEY_WALLPAPERS, JSONArray(names).toString()).apply()
    }

    // --- アイコン差し替え（OshiLauncher の描画内のみ） ---

    fun iconFile(packageName: String): File? {
        val name = iconMap().optString(packageName, "").takeIf { it.isNotEmpty() } ?: return null
        return File(iconDir, name).takeIf { it.exists() }
    }

    fun hasIconOverride(packageName: String): Boolean = iconFile(packageName) != null

    fun setIcon(packageName: String, uri: Uri): File {
        iconFile(packageName)?.delete()
        val name = copyToDir(uri, iconDir)
        val json = iconMap().put(packageName, name)
        prefs.edit().putString(KEY_ICONS, json.toString()).apply()
        return File(iconDir, name)
    }

    fun clearIcon(packageName: String) {
        iconFile(packageName)?.delete()
        val json = iconMap().apply { remove(packageName) }
        prefs.edit().putString(KEY_ICONS, json.toString()).apply()
    }

    private fun iconMap(): JSONObject =
        JSONObject(prefs.getString(KEY_ICONS, null) ?: "{}")

    private fun copyToDir(uri: Uri, dir: File): String {
        val name = "${System.currentTimeMillis()}_${Random.nextInt(1_000_000)}"
        val dest = File(dir, name)
        val input = context.contentResolver.openInputStream(uri)
            ?: throw IllegalStateException("画像を読み込めませんでした: $uri")
        input.use { src -> dest.outputStream().use { src.copyTo(it) } }
        return name
    }
}
