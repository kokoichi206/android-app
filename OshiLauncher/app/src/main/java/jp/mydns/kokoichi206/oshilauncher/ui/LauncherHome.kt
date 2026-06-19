package jp.mydns.kokoichi206.oshilauncher.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jp.mydns.kokoichi206.oshilauncher.data.AppEntry
import jp.mydns.kokoichi206.oshilauncher.data.AppRepository
import jp.mydns.kokoichi206.oshilauncher.data.CustomizationStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// 任意の壁紙の上でも読めるよう、白文字＋影で描く（全面スクリムで壁紙を暗くしない）。
private val onWallpaperShadow = Shadow(Color.Black.copy(alpha = 0.65f), Offset(0f, 1.5f), 4f)

@Composable
fun OshiLauncherApp(repo: AppRepository, store: CustomizationStore) {
    var apps by remember { mutableStateOf(emptyList<AppEntry>()) }
    var showSettings by remember { mutableStateOf(false) }
    var revision by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        apps = withContext(Dispatchers.IO) { repo.loadApps() }
    }

    fun closeSettings() {
        showSettings = false
        revision++
    }

    CompositionLocalProvider(
        LocalCustomizationStore provides store,
        LocalCustomizeRevision provides revision,
    ) {
        // ランチャーなので戻るはホームでは無反応、設定が開いていれば閉じるだけ。
        BackHandler(enabled = showSettings) { closeSettings() }

        val oshiMode = remember(revision) { store.isOshiMode() }

        Box(
            Modifier
                .fillMaxSize()
                // 2本指タップで 推し/通常 を切替。Initial パスで監視し consume しないので
                // 単指タップ・スクロール・上スワイプとは衝突しない。
                .pointerInput(revision) {
                    awaitPointerEventScope {
                        while (true) {
                            val ev = awaitPointerEvent(PointerEventPass.Initial)
                            if (ev.changes.count { it.pressed } >= 2) {
                                store.setOshiMode(!store.isOshiMode())
                                revision++
                                do {
                                    val e2 = awaitPointerEvent(PointerEventPass.Initial)
                                } while (e2.changes.any { it.pressed })
                            }
                        }
                    }
                },
        ) {
            // 推しモードのみ背景スライドショーを重ねる。通常モードは透過のままシステム壁紙を見せる。
            if (oshiMode) {
                WallpaperLayer(store, revision, Modifier.fillMaxSize())
            }
            HomeContent(
                apps = apps,
                repo = repo,
                onLaunch = { repo.launch(it.packageName) },
                onOpenSettings = { showSettings = true },
            )
        }

        if (showSettings) {
            CustomizeScreen(
                apps = apps,
                repo = repo,
                store = store,
                onClose = { closeSettings() },
            )
        }
    }
}

@Composable
private fun HomeContent(
    apps: List<AppEntry>,
    repo: AppRepository,
    onLaunch: (AppEntry) -> Unit,
    onOpenSettings: () -> Unit,
) {
    val timeFormat = remember { SimpleDateFormat("H:mm", Locale.JAPAN) }
    val dateFormat = remember { SimpleDateFormat("M月d日 (E)", Locale.JAPAN) }
    var now by remember { mutableStateOf(Date()) }
    LaunchedEffect(Unit) {
        while (true) {
            now = Date()
            delay(10_000)
        }
    }

    Column(Modifier.fillMaxSize().padding(horizontal = 14.dp)) {
        Row(Modifier.fillMaxWidth().padding(top = 36.dp, end = 8.dp), horizontalArrangement = Arrangement.End) {
            Text(
                "⚙",
                fontSize = 18.sp,
                color = Color.White,
                style = TextStyle(shadow = onWallpaperShadow),
                modifier = Modifier.clickable(onClick = onOpenSettings).padding(8.dp),
            )
        }
        Text(
            timeFormat.format(now),
            fontSize = 60.sp,
            fontWeight = FontWeight.Light,
            color = Color.White,
            style = TextStyle(shadow = onWallpaperShadow),
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
        Text(
            dateFormat.format(now),
            fontSize = 14.sp,
            color = Color.White.copy(alpha = 0.9f),
            style = TextStyle(shadow = onWallpaperShadow),
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )

        Spacer(Modifier.height(22.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier.fillMaxSize(),
        ) {
            items(apps, key = { it.packageName }) { app ->
                AppCell(app, repo) { onLaunch(app) }
            }
        }
    }
}

@Composable
private fun AppCell(app: AppEntry, repo: AppRepository, onClick: () -> Unit) {
    Column(
        Modifier
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AppIconImage(app.packageName, repo, 52.dp)
        Spacer(Modifier.height(6.dp))
        Text(
            app.label,
            fontSize = 11.sp,
            color = Color.White,
            style = TextStyle(shadow = onWallpaperShadow),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
        )
    }
}
