package jp.mydns.kokoichi206.oshilauncher.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import jp.mydns.kokoichi206.oshilauncher.data.AppRepository
import jp.mydns.kokoichi206.oshilauncher.data.CustomizationStore

// 推しの画像を主役にするため、UI 自体は暗めの控えめな配色。
val ColorBg = Color(0xFF0B0D12)
val ColorSheet = Color(0xFF14121A)
val ColorText = Color(0xFFF2EEF6)
val ColorSub = Color(0xFFB7A8C6)
val ColorDim = Color(0xFF6E6480)
val ColorAccent = Color(0xFFE48FB1) // 推し色アクセント（ピンク寄り）
val ColorTile = Color(0xFF1B1822)

/** カスタマイズ状態を AppIconImage など多数の呼び出し元へ引数なしで配るためのローカル。 */
val LocalCustomizationStore = compositionLocalOf<CustomizationStore?> { null }

/** 設定変更後に +1 され、アイコン・壁紙の再読込トリガになる世代番号。 */
val LocalCustomizeRevision = compositionLocalOf { 0 }

@Composable
fun AppIconImage(packageName: String, repo: AppRepository, size: Dp) {
    val store = LocalCustomizationStore.current
    val revision = LocalCustomizeRevision.current
    val customFile = remember(packageName, revision) { store?.iconFile(packageName) }

    var bitmap by remember(packageName, revision) {
        mutableStateOf(if (customFile == null) IconCache.peek(packageName) else null)
    }
    if (bitmap == null) {
        LaunchedEffect(packageName, revision) {
            bitmap = if (customFile != null) {
                ImageFiles.load(customFile, reqMaxPx = 144)
            } else {
                IconCache.load(packageName, repo)
            }
        }
    }

    val loaded = bitmap
    when {
        loaded != null && customFile != null ->
            Image(
                loaded,
                contentDescription = null,
                modifier = Modifier.size(size).clip(RoundedCornerShape(percent = 22)),
                contentScale = ContentScale.Crop,
            )

        loaded != null ->
            Image(loaded, contentDescription = null, modifier = Modifier.size(size))

        else ->
            Box(Modifier.size(size).background(Color(0xFF231F2C), RoundedCornerShape(10.dp)))
    }
}
