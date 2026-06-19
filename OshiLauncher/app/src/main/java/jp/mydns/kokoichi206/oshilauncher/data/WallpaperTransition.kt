package jp.mydns.kokoichi206.oshilauncher.data

/**
 * 壁紙スライドショーの切替アニメ。ホーム背景とライブ壁紙の両方がこの定義を共有する。
 * durationMs は方式ごとに最適値を固定（エンドユーザーには ms を触らせず、保持秒数だけ可変にする方針）。
 */
enum class WallpaperTransition(
    val id: String,
    val label: String,
    val sub: String,
    val durationMs: Int,
) {
    KEN_BURNS("kenburns", "Ken Burns", "常に動く", 1300),
    CROSSFADE("crossfade", "フェード", "静か", 1100),
    ZOOM("zoom", "ズーム", "奥行き", 1200),
    PUSH("push", "プッシュ", "流れる", 1500);

    companion object {
        val DEFAULT = KEN_BURNS
        fun fromId(id: String?): WallpaperTransition = entries.firstOrNull { it.id == id } ?: DEFAULT
    }
}
