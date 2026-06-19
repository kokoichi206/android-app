package jp.mydns.kokoichi206.oshilauncher.ui

import android.app.Activity
import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
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
import kotlin.math.ceil
import kotlin.math.roundToInt

private val onWallpaperShadow = Shadow(Color.Black.copy(alpha = 0.65f), Offset(0f, 1.5f), 4f)
private val labelStyle = TextStyle(shadow = onWallpaperShadow)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OshiLauncherApp(repo: AppRepository, store: CustomizationStore, widgetHost: AppWidgetHost) {
    val context = LocalContext.current
    val awm = remember { AppWidgetManager.getInstance(context) }

    var apps by remember { mutableStateOf(emptyList<AppEntry>()) }
    var showSettings by remember { mutableStateOf(false) }
    var showDrawer by remember { mutableStateOf(false) }
    var showWidgetPicker by remember { mutableStateOf(false) }
    var revision by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) { apps = withContext(Dispatchers.IO) { repo.loadApps() } }

    val oshiMode = remember(revision) { store.isOshiMode() }
    var layout by remember(revision) { mutableStateOf(store.homeLayout(oshiMode)) }

    fun updateLayout(next: HomeLayout) {
        layout = next
        store.setHomeLayout(oshiMode, next)
    }

    LaunchedEffect(apps, revision) {
        if (apps.isNotEmpty() && layout.items.isEmpty()) {
            updateLayout(
                HomeLayout(
                    apps.take(Grid.COLUMNS * 3).mapIndexed { i, app ->
                        LayoutItem(ItemType.APP, app.packageName, i % Grid.COLUMNS, i / Grid.COLUMNS)
                    },
                ),
            )
        }
    }

    // --- ウィジェット追加フロー（割り当て→バインド→設定→配置） ---
    // 外部 Activity（バインド確認・configure）往復で Activity が再生成されても失わないよう、
    // 保留 ID は rememberSaveable に置き、結果 Intent の EXTRA_APPWIDGET_ID から復元する。
    // provider は id から awm.getAppWidgetInfo(id) で再取得（state に持たない）。
    var pendingWidgetId by rememberSaveable { mutableStateOf(-1) }

    fun placeWidget(id: Int, info: AppWidgetProviderInfo) {
        val density = context.resources.displayMetrics.density
        val sx = ceil(info.minWidth / density / 90f).toInt().coerceIn(1, Grid.COLUMNS)
        val sy = ceil(info.minHeight / density / 110f).toInt().coerceIn(1, Grid.ROWS)
        val cell = layout.firstEmptyCell() ?: (0 to 0)
        updateLayout(
            layout.copy(
                items = layout.items + LayoutItem(ItemType.WIDGET, id.toString(), cell.first, cell.second, sx, sy),
            ),
        )
    }

    fun widgetIdFrom(data: Intent?): Int =
        data?.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, pendingWidgetId) ?: pendingWidgetId

    val configureLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) { res ->
        val id = widgetIdFrom(res.data)
        val info = if (id != -1) awm.getAppWidgetInfo(id) else null
        if (res.resultCode == Activity.RESULT_OK && info != null) placeWidget(id, info)
        else if (id != -1) widgetHost.deleteAppWidgetId(id)
    }

    fun afterBind(id: Int, info: AppWidgetProviderInfo) {
        if (info.configure != null) {
            pendingWidgetId = id
            val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE).apply {
                component = info.configure
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id)
            }
            // configure Activity は起動できないことがある（非公開等）→その場合はそのまま配置。
            runCatching { configureLauncher.launch(intent) }.onFailure { placeWidget(id, info) }
        } else {
            placeWidget(id, info)
        }
    }

    val bindLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) { res ->
        val id = widgetIdFrom(res.data)
        val info = if (id != -1) awm.getAppWidgetInfo(id) else null
        if (res.resultCode == Activity.RESULT_OK && info != null) afterBind(id, info)
        else if (id != -1) widgetHost.deleteAppWidgetId(id)
    }

    fun startAddWidget(info: AppWidgetProviderInfo) {
        showWidgetPicker = false
        val id = widgetHost.allocateAppWidgetId()
        pendingWidgetId = id
        if (awm.bindAppWidgetIdIfAllowed(id, info.provider)) {
            afterBind(id, info)
        } else {
            val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_BIND).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id)
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_PROVIDER, info.provider)
            }
            bindLauncher.launch(intent)
        }
    }

    fun removeItem(item: LayoutItem) {
        if (item.type == ItemType.WIDGET) {
            item.key.toIntOrNull()?.let { widgetHost.deleteAppWidgetId(it) }
        }
        updateLayout(layout.copy(items = layout.items.filterNot { it.key == item.key }))
    }

    fun closeSettings() {
        showSettings = false
        revision++
    }

    CompositionLocalProvider(
        LocalCustomizationStore provides store,
        LocalCustomizeRevision provides revision,
    ) {
        BackHandler(enabled = showSettings || showDrawer || showWidgetPicker) {
            showDrawer = false
            showWidgetPicker = false
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
                widgetHost = widgetHost,
                awm = awm,
                onLaunch = { repo.launch(it) },
                onChange = ::updateLayout,
                onRemove = ::removeItem,
                onOpenDrawer = { showDrawer = true },
                onOpenWidgets = { showWidgetPicker = true },
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
                    layout.firstEmptyCell()?.let { cell ->
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
        if (showWidgetPicker) {
            val providers = remember { awm.installedProviders.sortedBy { it.loadLabel(context.packageManager) } }
            ModalBottomSheet(
                onDismissRequest = { showWidgetPicker = false },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                containerColor = ColorSheet,
            ) {
                WidgetPicker(providers, context.packageManager) { startAddWidget(it) }
            }
        }
    }
}

@Composable
private fun HomeContent(
    apps: List<AppEntry>,
    repo: AppRepository,
    layout: HomeLayout,
    widgetHost: AppWidgetHost,
    awm: AppWidgetManager,
    onLaunch: (String) -> Unit,
    onChange: (HomeLayout) -> Unit,
    onRemove: (LayoutItem) -> Unit,
    onOpenDrawer: () -> Unit,
    onOpenWidgets: () -> Unit,
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
        Text(timeFormat.format(now), fontSize = 48.sp, fontWeight = FontWeight.Light, color = Color.White,
            style = labelStyle, modifier = Modifier.align(Alignment.CenterHorizontally))
        Text(dateFormat.format(now), fontSize = 13.sp, color = Color.White.copy(alpha = 0.9f),
            style = labelStyle, modifier = Modifier.align(Alignment.CenterHorizontally))

        Spacer(Modifier.height(10.dp))

        HomeGrid(
            layout = layout,
            apps = apps,
            repo = repo,
            widgetHost = widgetHost,
            awm = awm,
            onLaunch = onLaunch,
            onChange = onChange,
            onRemove = onRemove,
            onOpenWidgets = onOpenWidgets,
            onOpenSettings = onOpenSettings,
            modifier = Modifier.weight(1f).fillMaxWidth(),
        )

        Row(
            Modifier.fillMaxWidth().padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally),
        ) {
            Text("︿ アプリ", fontSize = 13.sp, color = Color.White, style = labelStyle,
                modifier = Modifier.clickable(onClick = onOpenDrawer).padding(6.dp))
            Text("＋ ウィジェット", fontSize = 13.sp, color = Color.White, style = labelStyle,
                modifier = Modifier.clickable(onClick = onOpenWidgets).padding(6.dp))
        }
    }
}

@Composable
private fun HomeGrid(
    layout: HomeLayout,
    apps: List<AppEntry>,
    repo: AppRepository,
    widgetHost: AppWidgetHost,
    awm: AppWidgetManager,
    onLaunch: (String) -> Unit,
    onChange: (HomeLayout) -> Unit,
    onRemove: (LayoutItem) -> Unit,
    onOpenWidgets: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val byPkg = remember(apps) { apps.associateBy { it.packageName } }
    var draggingKey by remember { mutableStateOf<String?>(null) }
    var dragDelta by remember { mutableStateOf(Offset.Zero) }
    var menuOpen by remember { mutableStateOf(false) }
    var menuPos by remember { mutableStateOf(Offset.Zero) }

    BoxWithConstraints(modifier) {
        val density = LocalDensity.current
        val cellW = maxWidth / Grid.COLUMNS
        val cellH = maxHeight / Grid.ROWS
        val cellWpx = with(density) { cellW.toPx() }
        val cellHpx = with(density) { cellH.toPx() }

        // 背景（空きセル）長押しで、標準ランチャー風メニューを長押し位置に出す。
        Box(
            Modifier.fillMaxSize().pointerInput(Unit) {
                detectTapGestures(onLongPress = { pos -> menuPos = pos; menuOpen = true })
            },
        )
        Box(Modifier.offset { IntOffset(menuPos.x.roundToInt(), menuPos.y.roundToInt()) }) {
            DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                DropdownMenuItem(text = { Text("ウィジェット") }, onClick = { menuOpen = false; onOpenWidgets() })
                DropdownMenuItem(
                    text = { Text("壁紙") },
                    onClick = {
                        menuOpen = false
                        runCatching { context.startActivity(Intent(Intent.ACTION_SET_WALLPAPER)) }
                    },
                )
                DropdownMenuItem(text = { Text("設定") }, onClick = { menuOpen = false; onOpenSettings() })
            }
        }

        fun commitDrop(item: LayoutItem) {
            val rawX = (item.cellX * cellWpx + dragDelta.x) / cellWpx
            val rawY = (item.cellY * cellHpx + dragDelta.y) / cellHpx
            if (rawY < -0.5f) {
                onRemove(item)
                return
            }
            val tx = rawX.roundToInt().coerceIn(0, Grid.COLUMNS - item.spanX)
            val ty = rawY.roundToInt().coerceIn(0, Grid.ROWS - item.spanY)
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

        if (draggingKey != null) {
            Box(Modifier.fillMaxWidth().height(cellH).zIndex(2f), contentAlignment = Alignment.Center) {
                Text("🗑 ここにドロップで削除", fontSize = 12.sp, color = Color.White, style = labelStyle)
            }
        }

        layout.items.forEach { item ->
            val isDragging = item.key == draggingKey
            val baseX = item.cellX * cellWpx
            val baseY = item.cellY * cellHpx
            val cellModifier = Modifier
                .offset {
                    IntOffset(
                        (baseX + if (isDragging) dragDelta.x else 0f).roundToInt(),
                        (baseY + if (isDragging) dragDelta.y else 0f).roundToInt(),
                    )
                }
                .size(width = cellW * item.spanX, height = cellH * item.spanY)
                .zIndex(if (isDragging) 3f else 0f)
                .pointerInput(item.key, layout) {
                    detectDragGesturesAfterLongPress(
                        onDragStart = { draggingKey = item.key; dragDelta = Offset.Zero },
                        onDrag = { change, delta -> change.consume(); dragDelta += delta },
                        onDragEnd = { commitDrop(item); draggingKey = null; dragDelta = Offset.Zero },
                        onDragCancel = { draggingKey = null; dragDelta = Offset.Zero },
                    )
                }

            when (item.type) {
                ItemType.APP -> Box(
                    cellModifier.pointerInput(item.key) { detectTapGestures(onTap = { onLaunch(item.key) }) },
                    contentAlignment = Alignment.Center,
                ) {
                    IconCell(item.key, byPkg[item.key]?.label ?: item.key, repo)
                }

                ItemType.WIDGET -> Box(cellModifier.padding(4.dp)) {
                    val id = item.key.toIntOrNull()
                    val info = if (id != null) awm.getAppWidgetInfo(id) else null
                    if (id != null && info != null) {
                        AndroidView(
                            factory = { ctx -> widgetHost.createView(ctx, id, info) },
                            modifier = Modifier.fillMaxSize(),
                        )
                    } else {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("ウィジェット", fontSize = 11.sp, color = Color.White, style = labelStyle)
                        }
                    }
                }
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

@Composable
private fun WidgetPicker(
    providers: List<AppWidgetProviderInfo>,
    pm: android.content.pm.PackageManager,
    onPick: (AppWidgetProviderInfo) -> Unit,
) {
    Column(
        Modifier.fillMaxHeight(0.85f).padding(horizontal = 20.dp).verticalScroll(rememberScrollState()),
    ) {
        Text("ウィジェットを追加", fontSize = 16.sp, color = ColorText, modifier = Modifier.padding(vertical = 8.dp))
        providers.forEach { info ->
            val label = remember(info) { info.loadLabel(pm) }
            Row(
                Modifier.fillMaxWidth().clickable { onPick(info) }.padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(label, fontSize = 14.sp, color = ColorText)
            }
        }
    }
}
