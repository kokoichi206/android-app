package jp.mydns.kokoichi206.oshilauncher.ui

import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jp.mydns.kokoichi206.oshilauncher.data.AppEntry
import jp.mydns.kokoichi206.oshilauncher.data.AppRepository
import jp.mydns.kokoichi206.oshilauncher.data.CustomizationStore
import jp.mydns.kokoichi206.oshilauncher.data.WallpaperTransition
import jp.mydns.kokoichi206.oshilauncher.wallpaper.SlideshowWallpaperService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

/**
 * 推しランチャーの設定（壁紙スライドショー / アイコン差し替え / ライブ壁紙に設定）。
 * 画面内の変更は localRevision で即時反映し、onClose 側で全体 revision を進めてホームへ伝える。
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CustomizeScreen(
    apps: List<AppEntry>,
    repo: AppRepository,
    store: CustomizationStore,
    onClose: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var localRevision by remember { mutableIntStateOf(0) }
    fun bumpLocal() {
        localRevision++
    }

    val wallpapers = remember(localRevision) { store.wallpaperFiles() }

    val addWallpapers = rememberLauncherForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(maxItems = 10),
    ) { uris ->
        if (uris.isNotEmpty()) {
            scope.launch {
                withContext(Dispatchers.IO) {
                    uris.forEach { uri -> runCatching { store.addWallpaper(uri) } }
                }
                bumpLocal()
            }
        }
    }

    var pendingIconPkg by remember { mutableStateOf<String?>(null) }
    val pickIcon = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        val pkg = pendingIconPkg
        pendingIconPkg = null
        if (uri != null && pkg != null) {
            scope.launch {
                withContext(Dispatchers.IO) { runCatching { store.setIcon(pkg, uri) } }
                bumpLocal()
            }
        }
    }

    val setHome = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) { bumpLocal() }

    fun openLiveWallpaperPicker() {
        val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER).apply {
            putExtra(
                WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                ComponentName(context, SlideshowWallpaperService::class.java),
            )
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        runCatching { context.startActivity(intent) }
    }

    CompositionLocalProvider(LocalCustomizeRevision provides localRevision) {
        Column(
            Modifier
                .fillMaxSize()
                .background(ColorBg)
                .padding(horizontal = 22.dp),
        ) {
            Row(
                Modifier.fillMaxWidth().padding(top = 40.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("カスタマイズ", fontSize = 20.sp, fontWeight = FontWeight.Medium, color = ColorText)
                Spacer(Modifier.weight(1f))
                Text(
                    "閉じる",
                    fontSize = 14.sp,
                    color = ColorAccent,
                    modifier = Modifier.clickable(onClick = onClose).padding(8.dp),
                )
            }

            LazyColumn(Modifier.weight(1f)) {
                item {
                    val oshiMode = remember(localRevision) { store.isOshiMode() }
                    SectionTitle("表示モード")
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TransitionChip("推しモード", "背景・アイコン", selected = oshiMode) {
                            store.setOshiMode(true); bumpLocal()
                        }
                        TransitionChip("通常モード", "標準の見た目", selected = !oshiMode) {
                            store.setOshiMode(false); bumpLocal()
                        }
                    }
                    Text("※ ホームを2本指でタップしても切り替わります。", fontSize = 11.sp, color = ColorDim,
                        modifier = Modifier.padding(top = 8.dp))
                    Spacer(Modifier.height(26.dp))
                }
                item {
                    val isHome = remember(localRevision) { DefaultLauncher.isDefault(context) }
                    SectionTitle("ホームアプリ")
                    if (isHome) {
                        Text("✓ このアプリがホームに設定されています", fontSize = 12.sp, color = ColorSub,
                            modifier = Modifier.padding(bottom = 6.dp))
                    } else {
                        Text("ホームボタンで OshiLauncher が開くようにします。", fontSize = 11.sp, color = ColorDim,
                            modifier = Modifier.padding(bottom = 8.dp))
                        Chip("このアプリをホームに設定") { setHome.launch(DefaultLauncher.requestIntent(context)) }
                    }
                    Spacer(Modifier.height(26.dp))
                }
                item {
                    SectionTitle("推し背景スライドショー")
                    IntervalRow(store)
                    Spacer(Modifier.height(14.dp))
                    Text("切替アニメ", fontSize = 12.sp, color = ColorDim, modifier = Modifier.padding(bottom = 8.dp))
                    TransitionRow(store, localRevision) { bumpLocal() }
                    Spacer(Modifier.height(14.dp))
                    WallpaperSection(
                        wallpapers = wallpapers,
                        onAdd = {
                            addWallpapers.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                            )
                        },
                        onRemove = { file ->
                            scope.launch {
                                withContext(Dispatchers.IO) { store.removeWallpaper(file.name) }
                                bumpLocal()
                            }
                        },
                    )
                    Spacer(Modifier.height(12.dp))
                    Chip("ロック画面/ホームのライブ壁紙に設定") { openLiveWallpaperPicker() }

                    Spacer(Modifier.height(26.dp))
                    SectionTitle("アイコンを画像に差し替え")
                    Text(
                        "OshiLauncher 内の表示だけが変わります（他のホームアプリには影響しません）。",
                        fontSize = 11.sp,
                        color = ColorDim,
                        modifier = Modifier.padding(bottom = 8.dp),
                    )
                }

                items(apps, key = { it.packageName }) { app ->
                    val overridden = remember(app.packageName, localRevision) {
                        store.hasIconOverride(app.packageName)
                    }
                    IconRow(
                        packageName = app.packageName,
                        label = app.label,
                        repo = repo,
                        overridden = overridden,
                        onChange = {
                            pendingIconPkg = app.packageName
                            pickIcon.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                            )
                        },
                        onReset = {
                            scope.launch {
                                withContext(Dispatchers.IO) { store.clearIcon(app.packageName) }
                                bumpLocal()
                            }
                        },
                    )
                }

                item { Spacer(Modifier.height(40.dp)) }
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text,
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium,
        color = ColorSub,
        modifier = Modifier.padding(top = 6.dp, bottom = 10.dp),
    )
}

@Composable
private fun IntervalRow(store: CustomizationStore) {
    var seconds by remember { mutableIntStateOf(store.intervalSeconds()) }
    fun update(next: Int) {
        seconds = next.coerceIn(
            CustomizationStore.MIN_INTERVAL_SECONDS,
            CustomizationStore.MAX_INTERVAL_SECONDS,
        )
        store.setIntervalSeconds(seconds)
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("切替間隔", fontSize = 13.sp, color = ColorText)
        Spacer(Modifier.weight(1f))
        Chip("−") { update(seconds - 1) }
        Text(
            "$seconds 秒",
            fontSize = 14.sp,
            color = ColorText,
            modifier = Modifier.padding(horizontal = 14.dp),
        )
        Chip("＋") { update(seconds + 1) }
    }
}

@Composable
private fun WallpaperSection(
    wallpapers: List<File>,
    onAdd: () -> Unit,
    onRemove: (File) -> Unit,
) {
    if (wallpapers.isEmpty()) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(150.dp)
                .border(1.dp, ColorDim, RoundedCornerShape(14.dp))
                .clickable(onClick = onAdd),
            contentAlignment = Alignment.Center,
        ) {
            Text("＋ 画像を追加（スクショ等）", fontSize = 13.sp, color = ColorSub)
        }
        return
    }

    val pagerState = rememberPagerState(pageCount = { wallpapers.size })
    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxWidth().height(200.dp),
        pageSpacing = 10.dp,
    ) { page ->
        val file = wallpapers[page]
        Box(Modifier.fillMaxSize().clip(RoundedCornerShape(14.dp)).background(ColorTile)) {
            FileImage(file, Modifier.fillMaxSize())
            Text(
                "削除",
                fontSize = 12.sp,
                color = ColorText,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    .clickable { onRemove(file) }
                    .padding(horizontal = 10.dp, vertical = 5.dp),
            )
        }
    }
    Spacer(Modifier.height(10.dp))
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("${pagerState.currentPage + 1} / ${wallpapers.size}", fontSize = 11.sp, color = ColorDim)
        Spacer(Modifier.weight(1f))
        Chip("＋ 画像を追加", onClick = onAdd)
    }
}

@Composable
private fun IconRow(
    packageName: String,
    label: String,
    repo: AppRepository,
    overridden: Boolean,
    onChange: () -> Unit,
    onReset: () -> Unit,
) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AppIconImage(packageName, repo, 44.dp)
        Spacer(Modifier.width(14.dp))
        Text(label, fontSize = 15.sp, color = ColorText, modifier = Modifier.weight(1f))
        if (overridden) {
            Chip("戻す", onClick = onReset)
            Spacer(Modifier.width(8.dp))
        }
        Chip("変更", onClick = onChange)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TransitionRow(store: CustomizationStore, revision: Int, onPick: () -> Unit) {
    val current = remember(revision) { store.transition() }
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        WallpaperTransition.entries.forEach { t ->
            TransitionChip(t.label, t.sub, selected = t == current) {
                store.setTransition(t)
                onPick()
            }
        }
    }
}

@Composable
private fun TransitionChip(label: String, sub: String, selected: Boolean, onClick: () -> Unit) {
    Column(
        Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) ColorAccent.copy(alpha = 0.22f) else ColorTile)
            .border(1.dp, if (selected) ColorAccent else Color.Transparent, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 9.dp),
    ) {
        Text(label, fontSize = 13.sp, color = ColorText)
        Text(sub, fontSize = 10.sp, color = if (selected) ColorAccent else ColorDim)
    }
}

@Composable
private fun Chip(text: String, onClick: () -> Unit) {
    Text(
        text,
        fontSize = 12.sp,
        color = ColorText,
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(ColorTile)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 7.dp),
    )
}

@Composable
private fun FileImage(file: File, modifier: Modifier) {
    var bitmap by remember(file.path) { mutableStateOf(ImageFiles.peek(file, 1024)) }
    if (bitmap == null) {
        LaunchedEffect(file.path) { bitmap = ImageFiles.load(file, 1024) }
    }
    bitmap?.let {
        Image(it, contentDescription = null, modifier = modifier, contentScale = ContentScale.Crop)
    }
}
