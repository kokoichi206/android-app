package jp.mydns.kokoichi206.oshilauncher.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import jp.mydns.kokoichi206.oshilauncher.data.CustomizationStore
import jp.mydns.kokoichi206.oshilauncher.data.WallpaperTransition
import kotlinx.coroutines.delay
import java.io.File

private const val WALLPAPER_MAX_PX = 1440

/**
 * ホーム背面の推しスライドショー。切替アニメは [CustomizationStore.transition] に従い、
 * ライブ壁紙サービスと同じ画像セット・同じ方式を共有する。未設定なら下地の [ColorBg] を見せる。
 * revision は設定画面から戻った際に +1 され、画像・秒数・方式の再読込トリガになる。
 */
@Composable
fun WallpaperLayer(store: CustomizationStore, revision: Int, modifier: Modifier = Modifier) {
    val files = remember(revision) { store.wallpaperFiles() }
    val intervalMs = remember(revision) { store.intervalSeconds() * 1000L }
    val transition = remember(revision) { store.transition() }

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
        val dur = transition.durationMs

        // 次画像をあらかじめデコードしてキャッシュに載せ、切替時の空フレーム/ポップを防ぐ。
        if (files.size > 1) {
            LaunchedEffect(revision, index) {
                ImageFiles.load(files[(index + 1) % files.size], WALLPAPER_MAX_PX)
            }
        }

        AnimatedContent(
            targetState = current,
            transitionSpec = {
                when (transition) {
                    WallpaperTransition.PUSH ->
                        slideInHorizontally(tween(dur)) { w -> w } togetherWith
                            slideOutHorizontally(tween(dur)) { w -> -w }

                    WallpaperTransition.ZOOM ->
                        (fadeIn(tween(dur)) + scaleIn(tween(dur), initialScale = 1.18f)) togetherWith
                            fadeOut(tween(dur))

                    // CROSSFADE / KEN_BURNS は溶け込み（KEN_BURNS は保持中の常時微動を別途付与）
                    else -> fadeIn(tween(dur)) togetherWith fadeOut(tween(dur))
                }
            },
            label = "wallpaper",
            modifier = Modifier.fillMaxSize(),
        ) { file ->
            WallpaperImage(file, kenBurns = transition == WallpaperTransition.KEN_BURNS, Modifier.fillMaxSize())
        }
        // ホームの淡色テキストの可読性を保つためのスクリム。
        Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.30f)))
    }
}

@Composable
private fun WallpaperImage(file: File, kenBurns: Boolean, modifier: Modifier) {
    var bitmap by remember(file.path) { mutableStateOf(ImageFiles.peek(file, WALLPAPER_MAX_PX)) }
    if (bitmap == null) {
        LaunchedEffect(file.path) { bitmap = ImageFiles.load(file, WALLPAPER_MAX_PX) }
    }

    val mod = if (kenBurns) {
        val anim = rememberInfiniteTransition(label = "kb")
        val scale by anim.animateFloat(
            initialValue = 1.05f,
            targetValue = 1.18f,
            animationSpec = infiniteRepeatable(tween(14_000, easing = LinearEasing), RepeatMode.Reverse),
            label = "kbScale",
        )
        modifier.graphicsLayer { scaleX = scale; scaleY = scale }
    } else {
        modifier
    }

    bitmap?.let {
        Image(it, contentDescription = null, modifier = mod, contentScale = ContentScale.Crop)
    }
}
