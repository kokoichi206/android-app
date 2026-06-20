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
        private const val KEY_TRANSITION = "transition_style"
        private const val KEY_OSHI_MODE = "oshi_mode"
        private const val KEY_SHARED_LAYOUT = "shared_layout"
        private const val KEY_LAYOUT_SHARED = "layout_shared"
        private const val KEY_LAYOUT_NORMAL = "layout_normal"
        private const val KEY_LAYOUT_OSHI = "layout_oshi"
        private const val KEY_LAUNCH_TIMES = "launch_times"
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

    fun transition(): WallpaperTransition =
        WallpaperTransition.fromId(prefs.getString(KEY_TRANSITION, null))

    fun setTransition(t: WallpaperTransition) {
        prefs.edit().putString(KEY_TRANSITION, t.id).apply()
    }

    /**
     * 推しモード（true）= 背景スライドショー＋カスタムアイコンを有効化。
     * 通常モード（false, 既定）= システム壁紙＋デフォルトアイコンの普通のホーム。
     */
    fun isOshiMode(): Boolean = prefs.getBoolean(KEY_OSHI_MODE, false)

    fun setOshiMode(on: Boolean) {
        prefs.edit().putBoolean(KEY_OSHI_MODE, on).apply()
    }

    // --- ホーム配置（モード別レイアウト / 共有） ---

    /** true なら両モードで同じ配置を共有、false（既定）ならモードごとに別配置。 */
    fun isSharedLayout(): Boolean = prefs.getBoolean(KEY_SHARED_LAYOUT, false)

    fun setSharedLayout(shared: Boolean, currentOshi: Boolean) {
        if (shared && prefs.getString(KEY_LAYOUT_SHARED, null).isNullOrEmpty()) {
            // 共有に切り替える瞬間、現在モードの配置を共有レイアウトの初期値にする。
            prefs.edit().putString(KEY_LAYOUT_SHARED, rawLayout(layoutKey(false, currentOshi))).apply()
        }
        prefs.edit().putBoolean(KEY_SHARED_LAYOUT, shared).apply()
    }

    fun homeLayout(oshi: Boolean): HomeLayout =
        HomeLayout.fromJsonString(rawLayout(layoutKey(isSharedLayout(), oshi)))

    fun setHomeLayout(oshi: Boolean, layout: HomeLayout) {
        prefs.edit().putString(layoutKey(isSharedLayout(), oshi), layout.toJsonString()).apply()
    }

    private fun layoutKey(shared: Boolean, oshi: Boolean): String = when {
        shared -> KEY_LAYOUT_SHARED
        oshi -> KEY_LAYOUT_OSHI
        else -> KEY_LAYOUT_NORMAL
    }

    private fun rawLayout(key: String): String = prefs.getString(key, "") ?: ""

    // --- アプリ起動履歴（ドロワーの最近起動順ソート用） ---

    fun recordLaunch(packageName: String) {
        val json = JSONObject(prefs.getString(KEY_LAUNCH_TIMES, null) ?: "{}")
        json.put(packageName, System.currentTimeMillis())
        prefs.edit().putString(KEY_LAUNCH_TIMES, json.toString()).apply()
    }

    fun launchTimes(): Map<String, Long> {
        val json = JSONObject(prefs.getString(KEY_LAUNCH_TIMES, null) ?: "{}")
        return json.keys().asSequence().associateWith { json.getLong(it) }
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
