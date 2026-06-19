package jp.mydns.kokoichi206.oshilauncher.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import jp.mydns.kokoichi206.oshilauncher.data.AppEntry
import jp.mydns.kokoichi206.oshilauncher.data.AppRepository
import jp.mydns.kokoichi206.oshilauncher.data.CustomizationStore
import jp.mydns.kokoichi206.oshilauncher.data.Grid
import jp.mydns.kokoichi206.oshilauncher.data.HomeLayout
import jp.mydns.kokoichi206.oshilauncher.data.ItemType
import jp.mydns.kokoichi206.oshilauncher.data.LayoutItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

private val onWallpaperShadow = Shadow(Color.Black.copy(alpha = 0.65f), Offset(0f, 1.5f), 4f)
private val labelStyle = TextStyle(shadow = onWallpaperShadow)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OshiLauncherApp(repo: AppRepository, store: CustomizationStore) {
    var apps by remember { mutableStateOf(emptyList<AppEntry>()) }
    var showSettings by remember { mutableStateOf(false) }
    var showDrawer by remember { mutableStateOf(false) }
    var revision by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) { apps = withContext(Dispatchers.IO) { repo.loadApps() } }

    val oshiMode = remember(revision) { store.isOshiMode() }
    var layout by remember(revision) { mutableStateOf(store.homeLayout(oshiMode)) }

    fun updateLayout(next: HomeLayout) {
        layout = next
        store.setHomeLayout(oshiMode, next)
    }

    // 初回（配置が空）はアプリを並べて初期配置する。
    LaunchedEffect(apps, revision) {
        if (apps.isNotEmpty() && layout.items.isEmpty()) {
            val seeded = HomeLayout(
                apps.take(Grid.COLUMNS * 3).mapIndexed { i, app ->
                    LayoutItem(ItemType.APP, app.packageName, i % Grid.COLUMNS, i / Grid.COLUMNS)
                },
            )
            updateLayout(seeded)
        }
    }

    fun closeSettings() {
        showSettings = false
        revision++
    }

    CompositionLocalProvider(
        LocalCustomizationStore provides store,
        LocalCustomizeRevision provides revision,
    ) {
        BackHandler(enabled = showSettings || showDrawer) {
            showDrawer = false
            if (showSettings) closeSettings()
        }

        Box(
            Modifier
                .fillMaxSize()
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
            if (oshiMode) WallpaperLayer(store, revision, Modifier.fillMaxSize())

            HomeContent(
                apps = apps,
                repo = repo,
                layout = layout,
                onLaunch = { repo.launch(it) },
                onChange = ::updateLayout,
                onOpenDrawer = { showDrawer = true },
                onOpenSettings = { showSettings = true },
            )
        }

        if (showSettings) {
            CustomizeScreen(apps, repo, store, onClose = { closeSettings() })
        }
        if (showDrawer) {
            val placed = remember(layout) { layout.items.mapTo(HashSet()) { it.key } }
            val addable = remember(apps, placed) { apps.filter { it.packageName !in placed } }
            ModalBottomSheet(
                onDismissRequest = { showDrawer = false },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                containerColor = ColorSheet,
            ) {
                AppDrawer(addable, repo) { app ->
                    val cell = layout.firstEmptyCell()
                    if (cell != null) {
                        updateLayout(
                            layout.copy(
                                items = layout.items +
                                    LayoutItem(ItemType.APP, app.packageName, cell.first, cell.second),
                            ),
                        )
                    }
                    showDrawer = false
                }
            }
        }
    }
}

@Composable
private fun HomeContent(
    apps: List<AppEntry>,
    repo: AppRepository,
    layout: HomeLayout,
    onLaunch: (String) -> Unit,
    onChange: (HomeLayout) -> Unit,
    onOpenDrawer: () -> Unit,
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

    Column(Modifier.fillMaxSize().padding(horizontal = 10.dp)) {
        Row(Modifier.fillMaxWidth().padding(top = 36.dp, end = 8.dp), horizontalArrangement = Arrangement.End) {
            Text("⚙", fontSize = 18.sp, color = Color.White, style = labelStyle,
                modifier = Modifier.clickable(onClick = onOpenSettings).padding(8.dp))
        }
        Text(timeFormat.format(now), fontSize = 52.sp, fontWeight = FontWeight.Light, color = Color.White,
            style = labelStyle, modifier = Modifier.align(Alignment.CenterHorizontally))
        Text(dateFormat.format(now), fontSize = 13.sp, color = Color.White.copy(alpha = 0.9f),
            style = labelStyle, modifier = Modifier.align(Alignment.CenterHorizontally))

        Spacer(Modifier.height(12.dp))

        HomeGrid(
            layout = layout,
            apps = apps,
            repo = repo,
            onLaunch = onLaunch,
            onChange = onChange,
            modifier = Modifier.weight(1f).fillMaxWidth(),
        )

        Text("︿ アプリ一覧", fontSize = 12.sp, color = Color.White, style = labelStyle,
            modifier = Modifier.align(Alignment.CenterHorizontally).clickable(onClick = onOpenDrawer).padding(vertical = 14.dp))
    }
}

@Composable
private fun HomeGrid(
    layout: HomeLayout,
    apps: List<AppEntry>,
    repo: AppRepository,
    onLaunch: (String) -> Unit,
    onChange: (HomeLayout) -> Unit,
    modifier: Modifier = Modifier,
) {
    val byPkg = remember(apps) { apps.associateBy { it.packageName } }
    var draggingKey by remember { mutableStateOf<String?>(null) }
    var dragDelta by remember { mutableStateOf(Offset.Zero) }

    BoxWithConstraints(modifier) {
        val density = LocalDensity.current
        val cellW = maxWidth / Grid.COLUMNS
        val cellH = maxHeight / Grid.ROWS
        val cellWpx = with(density) { cellW.toPx() }
        val cellHpx = with(density) { cellH.toPx() }

        fun commitDrop(item: LayoutItem) {
            val rawX = (item.cellX * cellWpx + dragDelta.x) / cellWpx
            val rawY = (item.cellY * cellHpx + dragDelta.y) / cellHpx
            if (rawY < -0.5f) { // グリッド上端より上にドロップ＝削除
                onChange(layout.copy(items = layout.items.filterNot { it.key == item.key }))
                return
            }
            val tx = rawX.roundToInt().coerceIn(0, Grid.COLUMNS - 1)
            val ty = rawY.roundToInt().coerceIn(0, Grid.ROWS - 1)
            val occupant = layout.itemAt(tx, ty)
            val newItems = when {
                occupant == null || occupant.key == item.key ->
                    layout.items.map { if (it.key == item.key) it.copy(cellX = tx, cellY = ty) else it }
                else -> layout.items.map {
                    when (it.key) {
                        item.key -> it.copy(cellX = tx, cellY = ty)
                        occupant.key -> it.copy(cellX = item.cellX, cellY = item.cellY)
                        else -> it
                    }
                }
            }
            onChange(layout.copy(items = newItems))
        }

        // ドラッグ中の削除ゾーン表示
        if (draggingKey != null) {
            Box(Modifier.fillMaxWidth().height(cellH).zIndex(2f), contentAlignment = Alignment.Center) {
                Text("🗑 ここにドロップで削除", fontSize = 12.sp, color = Color.White, style = labelStyle)
            }
        }

        layout.items.forEach { item ->
            if (item.type != ItemType.APP) return@forEach
            val isDragging = item.key == draggingKey
            val baseX = item.cellX * cellWpx
            val baseY = item.cellY * cellHpx
            Box(
                Modifier
                    .offset {
                        IntOffset(
                            (baseX + if (isDragging) dragDelta.x else 0f).roundToInt(),
                            (baseY + if (isDragging) dragDelta.y else 0f).roundToInt(),
                        )
                    }
                    .size(width = cellW * item.spanX, height = cellH * item.spanY)
                    .zIndex(if (isDragging) 3f else 0f)
                    .pointerInput(item.key) {
                        detectTapGestures(onTap = { onLaunch(item.key) })
                    }
                    .pointerInput(item.key, layout) {
                        detectDragGesturesAfterLongPress(
                            onDragStart = { draggingKey = item.key; dragDelta = Offset.Zero },
                            onDrag = { change, delta -> change.consume(); dragDelta += delta },
                            onDragEnd = { commitDrop(item); draggingKey = null; dragDelta = Offset.Zero },
                            onDragCancel = { draggingKey = null; dragDelta = Offset.Zero },
                        )
                    },
                contentAlignment = Alignment.Center,
            ) {
                IconCell(item.key, byPkg[item.key]?.label ?: item.key, repo)
            }
        }
    }
}

@Composable
private fun IconCell(packageName: String, label: String, repo: AppRepository) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        AppIconImage(packageName, repo, 50.dp)
        Spacer(Modifier.height(4.dp))
        Text(label, fontSize = 11.sp, color = Color.White, style = labelStyle,
            maxLines = 1, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center)
    }
}

@Composable
private fun AppDrawer(apps: List<AppEntry>, repo: AppRepository, onPick: (AppEntry) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = Modifier.fillMaxHeight(0.88f).padding(horizontal = 12.dp),
    ) {
        items(apps, key = { it.packageName }) { app ->
            Column(
                Modifier.clickable { onPick(app) }.padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                AppIconImage(app.packageName, repo, 48.dp)
                Spacer(Modifier.height(6.dp))
                Text(app.label, fontSize = 11.sp, color = ColorText,
                    maxLines = 1, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center)
            }
        }
    }
}
