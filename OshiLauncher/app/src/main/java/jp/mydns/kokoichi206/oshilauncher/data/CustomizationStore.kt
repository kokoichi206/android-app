package jp.mydns.kokoichi206.oshilauncher.data

import android.content.Context
import android.net.Uri
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import kotlin.random.Random

/** 名前付きの推し活モード 1 パターン（例: ブルーロックモード、家族モード）。 */
data class OshiProfile(val id: String, val name: String)

/**
 * 壁紙スライドショーとアイコン差し替えの永続化。SharedPreferences + JSON（PoC）。
 * launcher 本体（ホーム背景）とライブ壁紙サービスの両方から読まれ、画像セットを共有する。
 *
 * 推し活モードは名前付きプロファイルを複数持てる。壁紙・アイコン・配置はプロファイル別に保存し、
 * 読み書きはすべて「アクティブなプロファイル」に対して行う（呼び出し側はプロファイルを意識しない）。
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
        private const val KEY_PROFILES = "oshi_profiles"
        private const val KEY_ACTIVE_PROFILE = "active_oshi_profile"
        private const val LEGACY_PROFILE_ID = "default"
        const val DEFAULT_INTERVAL_SECONDS = 3
        const val MIN_INTERVAL_SECONDS = 1
        const val MAX_INTERVAL_SECONDS = 60
    }

    // --- 推し活モードのプロファイル ---

    private fun wallpapersKey(profileId: String) = "${KEY_WALLPAPERS}_$profileId"
    private fun iconsKey(profileId: String) = "${KEY_ICONS}_$profileId"
    private fun oshiLayoutKey(profileId: String) = "${KEY_LAYOUT_OSHI}_$profileId"

    /**
     * プロファイル導入前の単一推しモードのデータ（壁紙/アイコン/配置）を、
     * 最初のプロファイルとして引き継ぐ。プロファイルは常に 1 つ以上存在する。
     */
    private fun migrateToProfilesIfNeeded() {
        if (prefs.contains(KEY_PROFILES)) return
        saveProfiles(listOf(OshiProfile(LEGACY_PROFILE_ID, "推し活モード")))
        prefs.edit()
            .putString(wallpapersKey(LEGACY_PROFILE_ID), prefs.getString(KEY_WALLPAPERS, null) ?: "[]")
            .putString(iconsKey(LEGACY_PROFILE_ID), prefs.getString(KEY_ICONS, null) ?: "{}")
            .putString(oshiLayoutKey(LEGACY_PROFILE_ID), prefs.getString(KEY_LAYOUT_OSHI, null) ?: "")
            .putString(KEY_ACTIVE_PROFILE, LEGACY_PROFILE_ID)
            .remove(KEY_WALLPAPERS)
            .remove(KEY_ICONS)
            .remove(KEY_LAYOUT_OSHI)
            .apply()
    }

    fun profiles(): List<OshiProfile> {
        migrateToProfilesIfNeeded()
        val arr = JSONArray(prefs.getString(KEY_PROFILES, null) ?: "[]")
        return (0 until arr.length()).map {
            val o = arr.getJSONObject(it)
            OshiProfile(o.getString("id"), o.getString("name"))
        }
    }

    private fun saveProfiles(list: List<OshiProfile>) {
        val arr = JSONArray()
        list.forEach { arr.put(JSONObject().put("id", it.id).put("name", it.name)) }
        prefs.edit().putString(KEY_PROFILES, arr.toString()).apply()
    }

    fun activeProfile(): OshiProfile {
        val list = profiles()
        val id = prefs.getString(KEY_ACTIVE_PROFILE, null)
        return list.firstOrNull { it.id == id } ?: list.first()
    }

    private fun activeProfileId(): String = activeProfile().id

    fun setActiveProfile(id: String) {
        prefs.edit().putString(KEY_ACTIVE_PROFILE, id).apply()
    }

    fun addProfile(name: String): OshiProfile {
        val profile = OshiProfile("p${System.currentTimeMillis()}", name)
        saveProfiles(profiles() + profile)
        return profile
    }

    fun renameProfile(id: String, name: String) {
        saveProfiles(profiles().map { if (it.id == id) it.copy(name = name) else it })
    }

    /** 最後の 1 つは消せない。画像実体（壁紙・アイコン）もプロファイル専用コピーなので一緒に削除する。 */
    fun removeProfile(id: String) {
        val list = profiles()
        if (list.size <= 1 || list.none { it.id == id }) return
        wallpaperNames(id).forEach { File(wallpaperDir, it).delete() }
        val icons = iconMap(id)
        icons.keys().asSequence().toList().forEach { pkg -> File(iconDir, icons.getString(pkg)).delete() }
        prefs.edit()
            .remove(wallpapersKey(id))
            .remove(iconsKey(id))
            .remove(oshiLayoutKey(id))
            .apply()
        saveProfiles(list.filterNot { it.id == id })
        if (prefs.getString(KEY_ACTIVE_PROFILE, null) == id) {
            setActiveProfile(list.first { it.id != id }.id)
        }
    }

    // --- 壁紙スライドショー（アクティブなプロファイルの画像セット） ---

    fun wallpaperFiles(): List<File> =
        wallpaperNames(activeProfileId()).map { File(wallpaperDir, it) }.filter { it.exists() }

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
        oshi -> oshiLayoutKey(activeProfileId())
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
        val id = activeProfileId()
        val name = copyToDir(uri, wallpaperDir)
        saveWallpaperNames(id, wallpaperNames(id) + name)
        return name
    }

    fun removeWallpaper(name: String) {
        val id = activeProfileId()
        File(wallpaperDir, name).delete()
        saveWallpaperNames(id, wallpaperNames(id).filter { it != name })
    }

    private fun wallpaperNames(profileId: String): List<String> {
        val raw = prefs.getString(wallpapersKey(profileId), null) ?: return emptyList()
        val arr = JSONArray(raw)
        return (0 until arr.length()).map { arr.getString(it) }
    }

    private fun saveWallpaperNames(profileId: String, names: List<String>) {
        prefs.edit().putString(wallpapersKey(profileId), JSONArray(names).toString()).apply()
    }

    // --- アイコン差し替え（OshiLauncher の描画内のみ、アクティブなプロファイル別） ---

    fun iconFile(packageName: String): File? {
        val name = iconMap(activeProfileId()).optString(packageName, "").takeIf { it.isNotEmpty() }
            ?: return null
        return File(iconDir, name).takeIf { it.exists() }
    }

    fun hasIconOverride(packageName: String): Boolean = iconFile(packageName) != null

    fun setIcon(packageName: String, uri: Uri): File {
        val id = activeProfileId()
        iconFile(packageName)?.delete()
        val name = copyToDir(uri, iconDir)
        val json = iconMap(id).put(packageName, name)
        prefs.edit().putString(iconsKey(id), json.toString()).apply()
        return File(iconDir, name)
    }

    fun clearIcon(packageName: String) {
        val id = activeProfileId()
        iconFile(packageName)?.delete()
        val json = iconMap(id).apply { remove(packageName) }
        prefs.edit().putString(iconsKey(id), json.toString()).apply()
    }

    private fun iconMap(profileId: String): JSONObject =
        JSONObject(prefs.getString(iconsKey(profileId), null) ?: "{}")

    private fun copyToDir(uri: Uri, dir: File): String {
        val name = "${System.currentTimeMillis()}_${Random.nextInt(1_000_000)}"
        val dest = File(dir, name)
        val input = context.contentResolver.openInputStream(uri)
            ?: throw IllegalStateException("画像を読み込めませんでした: $uri")
        input.use { src -> dest.outputStream().use { src.copyTo(it) } }
        return name
    }
}
