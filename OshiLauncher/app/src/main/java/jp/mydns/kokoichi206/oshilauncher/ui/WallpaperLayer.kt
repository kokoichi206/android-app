package jp.mydns.kokoichi206.oshilauncher.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import jp.mydns.kokoichi206.oshilauncher.data.CustomizationStore
import kotlinx.coroutines.delay
import java.io.File

private const val WALLPAPER_MAX_PX = 1440

/**
 * ホーム背面の推しスライドショー。設定済み画像を一定秒ごとに「スライドイン＋クロスフェード」で切替。
 * ライブ壁紙（[jp.mydns.kokoichi206.oshilauncher.wallpaper.SlideshowWallpaperService]）と同じ画像セットを
 * 共有するが、こちらは launcher 自身が描くホーム背景。未設定なら下地の [ColorBg] を見せる。
 */
@Composable
fun WallpaperLayer(store: CustomizationStore, revision: Int, modifier: Modifier = Modifier) {
    val files = remember(revision) { store.wallpaperFiles() }
    val intervalMs = remember(revision) { store.intervalSeconds() * 1000L }

    Box(modifier.background(ColorBg)) {
        if (files.isEmpty()) return@Box

        var index by remember(revision) { mutableIntStateOf(0) }
        if (files.size > 1) {
            LaunchedEffect(revision, intervalMs, files.size) {
                while (true) {
                    delay(intervalMs)
                    index = (index + 1) % files.size
                }
            }
        }
        val current = files[index.coerceIn(0, files.lastIndex)]

        AnimatedContent(
            targetState = current,
            transitionSpec = {
                // 次の画像は右からスライドインしつつフェードイン、旧画像は軽く左へ抜けてフェードアウト。
                (slideInHorizontally(tween(700)) { w -> w } + fadeIn(tween(700))) togetherWith
                    (slideOutHorizontally(tween(700)) { w -> -w / 6 } + fadeOut(tween(700)))
            },
            label = "wallpaper",
            modifier = Modifier.fillMaxSize(),
        ) { file ->
            WallpaperImage(file, Modifier.fillMaxSize())
        }
        // 推し画像の上でも時計・文字が読めるようにするスクリム。
        Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.30f)))
    }
}

@Composable
private fun WallpaperImage(file: File, modifier: Modifier) {
    var bitmap by remember(file.path) { mutableStateOf(ImageFiles.peek(file, WALLPAPER_MAX_PX)) }
    if (bitmap == null) {
        LaunchedEffect(file.path) { bitmap = ImageFiles.load(file, WALLPAPER_MAX_PX) }
    }
    bitmap?.let {
        Image(it, contentDescription = null, modifier = modifier, contentScale = ContentScale.Crop)
    }
}
