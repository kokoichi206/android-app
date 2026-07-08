package jp.mydns.kokoichi206.oshilauncher.ui

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.io.File
import kotlin.math.abs

private const val PHOTO_CARD_MAX_PX = 800

/**
 * ホームに配置できる推し写真カード（WidgetClub のフォトウィジェット相当）。
 * 壁紙スライドショーと同じ画像セットをクロスフェードで巡回する。タップで次の画像へ。
 * 開始位置は itemKey でずらし、複数カードが同じ画像に揃わないようにする。
 */
@Composable
fun OshiPhotoCard(itemKey: String, modifier: Modifier = Modifier) {
    val store = LocalCustomizationStore.current ?: return
    val revision = LocalCustomizeRevision.current
    val files = remember(revision) { store.wallpaperFiles() }
    val shape = RoundedCornerShape(20.dp)

    if (files.isEmpty()) {
        Box(modifier.clip(shape).background(ColorTile), contentAlignment = Alignment.Center) {
            Text(
                "＋ 設定から\n推し画像を追加",
                fontSize = 11.sp,
                color = ColorSub,
                textAlign = TextAlign.Center,
            )
        }
        return
    }

    val intervalMs = remember(revision) { store.intervalSeconds() * 1000L }
    var index by remember(revision, files.size) {
        mutableIntStateOf(abs(itemKey.hashCode()) % files.size)
    }

    if (files.size > 1) {
        LaunchedEffect(revision, intervalMs, files.size) {
            while (true) {
                delay(intervalMs)
                index = (index + 1) % files.size
            }
        }
        // 次画像の先読みで切替時のポップを防ぐ（WallpaperLayer と同じ狙い）。
        LaunchedEffect(revision, index) {
            ImageFiles.load(files[(index + 1) % files.size], PHOTO_CARD_MAX_PX)
        }
    }

    Box(
        modifier
            .clip(shape)
            .background(ColorTile)
            .pointerInput(files.size) {
                detectTapGestures(onTap = { index = (index + 1) % files.size })
            },
    ) {
        Crossfade(
            targetState = files[index.coerceIn(0, files.lastIndex)],
            animationSpec = tween(500),
            label = "photoCard",
        ) { file ->
            PhotoImage(file, Modifier.fillMaxSize())
        }
    }
}

@Composable
private fun PhotoImage(file: File, modifier: Modifier) {
    var bitmap by remember(file.path) { mutableStateOf(ImageFiles.peek(file, PHOTO_CARD_MAX_PX)) }
    if (bitmap == null) {
        LaunchedEffect(file.path) { bitmap = ImageFiles.load(file, PHOTO_CARD_MAX_PX) }
    }
    bitmap?.let {
        Image(it, contentDescription = null, modifier = modifier, contentScale = ContentScale.Crop)
    }
}
