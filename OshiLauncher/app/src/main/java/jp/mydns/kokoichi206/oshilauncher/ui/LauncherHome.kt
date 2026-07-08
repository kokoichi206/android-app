package jp.mydns.kokoichi206.oshilauncher.ui

import android.app.Activity
import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
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
import androidx.core.graphics.drawable.toBitmap
import jp.mydns.kokoichi206.oshilauncher.data.AppEntry
import jp.mydns.kokoichi206.oshilauncher.data.AppRepository
import jp.mydns.kokoichi206.oshilauncher.data.CustomizationStore
import jp.mydns.kokoichi206.oshilauncher.data.Grid
import jp.mydns.kokoichi206.oshilauncher.data.HomeLayout
import jp.mydns.kokoichi206.oshilauncher.data.ItemType
import jp.mydns.kokoichi206.oshilauncher.data.LayoutItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
    val scope = rememberCoroutineScope()

    var apps by remember { mutableStateOf(emptyList<AppEntry>()) }
    var showSettings by remember { mutableStateOf(false) }
    var showDrawer by remember { mutableStateOf(false) }
    var showWidgetPicker by remember { mutableStateOf(false) }
    var revision by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) { apps = withContext(Dispatchers.IO) { repo.loadApps() } }

    val oshiMode = remember(revision) { store.isOshiMode() }
    var layout by remember(revision) { mutableStateOf(store.homeLayout(oshiMode)) }
    val pagerState = rememberPagerState { maxOf(1, layout.pageCount()) }

    fun updateLayout(next: HomeLayout) {
        layout = next
        store.setHomeLayout(oshiMode, next)
    }

    LaunchedEffect(apps, revision) {
        if (apps.isNotEmpty() && layout.items.isEmpty()) {
            // 推し活モードの初期配置は上段にフォトカードを敷き、WidgetClub 風の見た目から始める。
            updateLayout(
                if (oshiMode) {
                    HomeLayout(
                        listOf(LayoutItem(ItemType.PHOTO, "photo_seed", 0, 0, Grid.COLUMNS, 2)) +
                            apps.take(Grid.COLUMNS * 2).mapIndexed { i, app ->
                                LayoutItem(ItemType.APP, app.packageName, i % Grid.COLUMNS, 2 + i / Grid.COLUMNS)
                            },
                    )
                } else {
                    HomeLayout(
                        apps.take(Grid.COLUMNS * 3).mapIndexed { i, app ->
                            LayoutItem(ItemType.APP, app.packageName, i % Grid.COLUMNS, i / Grid.COLUMNS)
                        },
                    )
                },
            )
        }
    }

    fun launch(pkg: String) {
        store.recordLaunch(pkg)
        repo.launch(pkg)
    }

    fun addToHome(pkg: String) {
        if (layout.items.any { it.key == pkg }) return
        val (p, x, y) = layout.firstFreeSlot(pagerState.currentPage)
        updateLayout(layout.copy(items = layout.items + LayoutItem(ItemType.APP, pkg, x, y, page = p)))
    }

    var pendingPage by remember { mutableIntStateOf(-1) }

    fun moveToPage(item: LayoutItem, newPage: Int) {
        val np = newPage.coerceAtLeast(0)
        val slot = layout.firstFreeSlot(np)
        updateLayout(
            layout.copy(
                items = layout.items.map {
                    if (it.key == item.key) it.copy(page = np, cellX = slot.second, cellY = slot.third) else it
                },
            ),
        )
        pendingPage = np // ページ数更新後に LaunchedEffect でスクロール
    }

    // 新ページ作成後、pageCount が反映されてから追従スクロールする。
    LaunchedEffect(pendingPage, layout) {
        if (pendingPage >= 0) {
            runCatching { pagerState.animateScrollToPage(pendingPage.coerceIn(0, maxOf(0, layout.pageCount() - 1))) }
            pendingPage = -1
        }
    }

    // --- ウィジェット追加フロー ---
    var pendingWidgetId by rememberSaveable { mutableStateOf(-1) }

    fun placeWidget(id: Int, info: AppWidgetProviderInfo) {
        val density = context.resources.displayMetrics.density
        val sx = ceil(info.minWidth / density / 90f).toInt().coerceIn(1, Grid.COLUMNS)
        val sy = ceil(info.minHeight / density / 110f).toInt().coerceIn(1, Grid.ROWS)
        val (p, x, y) = layout.firstFreeSlot(pagerState.currentPage)
        updateLayout(
            layout.copy(items = layout.items + LayoutItem(ItemType.WIDGET, id.toString(), x, y, sx, sy, page = p)),
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
        if (item.type == ItemType.WIDGET) item.key.toIntOrNull()?.let { widgetHost.deleteAppWidgetId(it) }
        updateLayout(layout.copy(items = layout.items.filterNot { it.key == item.key }))
    }

    fun addPhotoCard() {
        val (p, x, y) = layout.firstFreeSlotFor(2, 2, pagerState.currentPage)
        updateLayout(
            layout.copy(
                items = layout.items +
                    LayoutItem(ItemType.PHOTO, "photo_${System.currentTimeMillis()}", x, y, 2, 2, page = p),
            ),
        )
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
                    // ステルススイッチ: 2本指の「タップ」（動かさず・すぐ離す）だけに反応する。
                    // 移動量と押下時間で弾かないと、ピンチや長押しのたびにモードが切り替わってしまう。
                    val slop = viewConfiguration.touchSlop * 3
                    awaitPointerEventScope {
                        while (true) {
                            var ev = awaitPointerEvent(PointerEventPass.Initial)
                            if (ev.changes.count { it.pressed } < 2) continue
                            val start = ev.changes.filter { it.pressed }.associate { it.id to it.position }
                            val startTime = ev.changes.maxOf { it.uptimeMillis }
                            var isTap = true
                            while (ev.changes.any { it.pressed }) {
                                ev = awaitPointerEvent(PointerEventPass.Initial)
                                val moved = ev.changes.any { c ->
                                    val s = start[c.id] ?: return@any false
                                    (c.position - s).getDistance() > slop
                                }
                                val held = ev.changes.maxOf { it.uptimeMillis } - startTime > 350
                                if (moved || held) isTap = false
                            }
                            if (isTap) {
                                store.setOshiMode(!store.isOshiMode())
                                revision++
                            }
                        }
                    }
                },
        ) {
            // 人に見せる瞬間の切替が自然に見えるよう、背景はフェードで出し入れする。
            AnimatedVisibility(
                visible = oshiMode,
                enter = fadeIn(tween(400)),
                exit = fadeOut(tween(400)),
            ) {
                WallpaperLayer(store, revision, Modifier.fillMaxSize())
            }

            HomeContent(
                apps = apps,
                repo = repo,
                layout = layout,
                pagerState = pagerState,
                widgetHost = widgetHost,
                awm = awm,
                oshiMode = oshiMode,
                onLaunch = ::launch,
                onChange = ::updateLayout,
                onRemove = ::removeItem,
                onMoveToPage = ::moveToPage,
                onOpenDrawer = { showDrawer = true },
                onOpenWidgets = { showWidgetPicker = true },
                onAddPhoto = ::addPhotoCard,
                onOpenSettings = { showSettings = true },
            )
        }

        if (showSettings) CustomizeScreen(apps, repo, store, onClose = { closeSettings() })

        if (showDrawer) {
            val ordered = remember(apps) {
                val times = store.launchTimes()
                apps.sortedWith(
                    compareByDescending<AppEntry> { times[it.packageName] ?: 0L }.thenBy { it.label.lowercase() },
                )
            }
            val placed = remember(layout) { layout.items.mapTo(HashSet()) { it.key } }
            ModalBottomSheet(
                onDismissRequest = { showDrawer = false },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                containerColor = ColorSheet,
            ) {
                AppDrawer(
                    apps = ordered,
                    placedKeys = placed,
                    repo = repo,
                    onLaunch = { launch(it); showDrawer = false },
                    onAdd = { addToHome(it); showDrawer = false },
                )
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
    pagerState: PagerState,
    widgetHost: AppWidgetHost,
    awm: AppWidgetManager,
    oshiMode: Boolean,
    onLaunch: (String) -> Unit,
    onChange: (HomeLayout) -> Unit,
    onRemove: (LayoutItem) -> Unit,
    onMoveToPage: (LayoutItem, Int) -> Unit,
    onOpenDrawer: () -> Unit,
    onOpenWidgets: () -> Unit,
    onAddPhoto: () -> Unit,
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

    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp)
            .pointerInput(Unit) {
                var acc = 0f
                detectVerticalDragGestures(
                    onDragStart = { acc = 0f },
                    onVerticalDrag = { _, dy ->
                        acc += dy
                        if (acc < -120f) { onOpenDrawer(); acc = Float.MAX_VALUE }
                    },
                )
            },
    ) {
        Row(Modifier.fillMaxWidth().padding(top = 36.dp, end = 8.dp), horizontalArrangement = Arrangement.End) {
            Text("⚙", fontSize = 18.sp, color = Color.White, style = labelStyle,
                modifier = Modifier.clickable(onClick = onOpenSettings).padding(8.dp))
        }
        Text(timeFormat.format(now), fontSize = 44.sp, fontWeight = FontWeight.Light, color = Color.White,
            style = labelStyle, modifier = Modifier.align(Alignment.CenterHorizontally))
        Text(dateFormat.format(now), fontSize = 13.sp, color = Color.White.copy(alpha = 0.9f),
            style = labelStyle, modifier = Modifier.align(Alignment.CenterHorizontally))

        Spacer(Modifier.height(8.dp))

        Box(Modifier.weight(1f).fillMaxWidth()) {
            HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
                HomeGrid(
                    page = page,
                    layout = layout,
                    apps = apps,
                    repo = repo,
                    widgetHost = widgetHost,
                    awm = awm,
                    oshiMode = oshiMode,
                    onLaunch = onLaunch,
                    onChange = onChange,
                    onRemove = onRemove,
                    onMoveToPage = onMoveToPage,
                    onOpenWidgets = onOpenWidgets,
                    onAddPhoto = onAddPhoto,
                    onOpenSettings = onOpenSettings,
                    modifier = Modifier.fillMaxSize(),
                )
            }
            // ページインジケータ
            if (pagerState.pageCount > 1) {
                Row(
                    Modifier.align(Alignment.BottomCenter).padding(bottom = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    repeat(pagerState.pageCount) { i ->
                        val active = i == pagerState.currentPage
                        Box(
                            Modifier.size(if (active) 8.dp else 6.dp).clip(CircleShape)
                                .background(Color.White.copy(alpha = if (active) 0.95f else 0.4f)),
                        )
                    }
                }
            }
        }

        Row(
            Modifier.fillMaxWidth().padding(vertical = 10.dp),
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
    page: Int,
    layout: HomeLayout,
    apps: List<AppEntry>,
    repo: AppRepository,
    widgetHost: AppWidgetHost,
    awm: AppWidgetManager,
    oshiMode: Boolean,
    onLaunch: (String) -> Unit,
    onChange: (HomeLayout) -> Unit,
    onRemove: (LayoutItem) -> Unit,
    onMoveToPage: (LayoutItem, Int) -> Unit,
    onOpenWidgets: () -> Unit,
    onAddPhoto: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val byPkg = remember(apps) { apps.associateBy { it.packageName } }
    var draggingKey by remember { mutableStateOf<String?>(null) }
    var dragDelta by remember { mutableStateOf(Offset.Zero) }
    var grabOffset by remember { mutableStateOf(Offset.Zero) } // 長押し開始点（アイテム内ローカル座標）
    var menuOpen by remember { mutableStateOf(false) }
    var menuPos by remember { mutableStateOf(Offset.Zero) }

    BoxWithConstraints(modifier) {
        val density = LocalDensity.current
        val cellW = maxWidth / Grid.COLUMNS
        val cellH = maxHeight / Grid.ROWS
        val cellWpx = with(density) { cellW.toPx() }
        val cellHpx = with(density) { cellH.toPx() }

        fun commitDrop(item: LayoutItem) {
            val rawX = (item.cellX * cellWpx + dragDelta.x) / cellWpx
            val rawY = (item.cellY * cellHpx + dragDelta.y) / cellHpx
            // 指のグリッド列位置（アイテムは画面内でも、指は端まで動かせる）。
            val fingerCol = (item.cellX * cellWpx + grabOffset.x + dragDelta.x) / cellWpx
            when {
                rawY < -0.5f -> onRemove(item)
                fingerCol >= Grid.COLUMNS - 0.15f -> onMoveToPage(item, page + 1) // 指が右端
                fingerCol <= 0.15f && page > 0 -> onMoveToPage(item, page - 1) // 指が左端
                else -> {
                    val tx = rawX.roundToInt().coerceIn(0, Grid.COLUMNS - item.spanX)
                    val ty = rawY.roundToInt().coerceIn(0, Grid.ROWS - item.spanY)
                    val occupant = layout.itemAt(page, tx, ty)
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
            }
        }

        // 背景長押し → 標準ランチャー風メニュー
        Box(
            Modifier.fillMaxSize().pointerInput(Unit) {
                detectTapGestures(onLongPress = { pos -> menuPos = pos; menuOpen = true })
            },
        )
        Box(Modifier.offset { IntOffset(menuPos.x.roundToInt(), menuPos.y.roundToInt()) }) {
            DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                DropdownMenuItem(text = { Text("推しフォトカード") }, onClick = { menuOpen = false; onAddPhoto() })
                DropdownMenuItem(text = { Text("ウィジェット") }, onClick = { menuOpen = false; onOpenWidgets() })
                DropdownMenuItem(
                    text = { Text("壁紙") },
                    onClick = { menuOpen = false; runCatching { context.startActivity(Intent(Intent.ACTION_SET_WALLPAPER)) } },
                )
                DropdownMenuItem(text = { Text("設定") }, onClick = { menuOpen = false; onOpenSettings() })
            }
        }

        if (draggingKey != null) {
            Box(Modifier.fillMaxWidth().height(cellH).zIndex(2f), contentAlignment = Alignment.Center) {
                Text("🗑 上にドロップで削除", fontSize = 11.sp, color = Color.White, style = labelStyle)
            }
            // 左右端：中心を端まで運ぶと隣ページへ
            Box(
                Modifier.align(Alignment.CenterEnd).fillMaxHeight().width(cellW * 0.5f).zIndex(2f)
                    .background(Color.White.copy(alpha = 0.10f)),
                contentAlignment = Alignment.Center,
            ) { Text("→\n次", fontSize = 12.sp, color = Color.White, style = labelStyle, textAlign = TextAlign.Center) }
            if (page > 0) {
                Box(
                    Modifier.align(Alignment.CenterStart).fillMaxHeight().width(cellW * 0.5f).zIndex(2f)
                        .background(Color.White.copy(alpha = 0.10f)),
                    contentAlignment = Alignment.Center,
                ) { Text("←\n前", fontSize = 12.sp, color = Color.White, style = labelStyle, textAlign = TextAlign.Center) }
            }
        }

        // フォトカードは推し画像を映すので、一般モードでは（配置共有時でも）描画しない。
        layout.itemsOnPage(page).filter { oshiMode || it.type != ItemType.PHOTO }.forEach { item ->
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
                .pointerInput(item.key, layout, page) {
                    detectDragGesturesAfterLongPress(
                        onDragStart = { off -> draggingKey = item.key; dragDelta = Offset.Zero; grabOffset = off },
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

                ItemType.PHOTO -> Box(cellModifier.padding(6.dp)) {
                    OshiPhotoCard(item.key, Modifier.fillMaxSize())
                }

                ItemType.WIDGET -> Box(cellModifier.padding(4.dp)) {
                    val id = item.key.toIntOrNull()
                    val info = if (id != null) awm.getAppWidgetInfo(id) else null
                    if (id != null && info != null) {
                        AndroidView(factory = { ctx -> widgetHost.createView(ctx, id, info) }, modifier = Modifier.fillMaxSize())
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppDrawer(
    apps: List<AppEntry>,
    placedKeys: Set<String>,
    repo: AppRepository,
    onLaunch: (String) -> Unit,
    onAdd: (String) -> Unit,
) {
    var query by remember { mutableStateOf("") }
    val filtered = remember(query, apps) {
        if (query.isBlank()) {
            apps
        } else {
            val q = query.lowercase()
            val prefix = apps.filter { it.label.lowercase().startsWith(q) }
            val contains = apps.filter { it.label.lowercase().contains(q) } - prefix.toSet()
            prefix + contains
        }
    }
    Column(Modifier.fillMaxHeight(0.9f).padding(horizontal = 12.dp)) {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            placeholder = { Text("アプリを検索") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
        )
        Text("タップで起動 / 長押しでホームに追加", fontSize = 11.sp, color = ColorSub,
            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp))
        LazyVerticalGrid(columns = GridCells.Fixed(4), modifier = Modifier.fillMaxHeight()) {
            items(filtered, key = { it.packageName }) { app ->
                val onHome = app.packageName in placedKeys
                Column(
                    Modifier
                        .padding(vertical = 12.dp)
                        .pointerInput(app.packageName) {
                            detectTapGestures(
                                onTap = { onLaunch(app.packageName) },
                                onLongPress = { onAdd(app.packageName) },
                            )
                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    AppIconImage(app.packageName, repo, 48.dp)
                    Spacer(Modifier.height(6.dp))
                    Text(app.label + if (onHome) " •" else "", fontSize = 11.sp,
                        color = if (onHome) ColorDim else ColorText,
                        maxLines = 1, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center)
                }
            }
        }
    }
}

private class WidgetEntry(
    val info: AppWidgetProviderInfo,
    val appLabel: String,
    val widgetLabel: String,
    val sizeText: String,
)

@Composable
private fun WidgetPicker(
    providers: List<AppWidgetProviderInfo>,
    pm: android.content.pm.PackageManager,
    onPick: (AppWidgetProviderInfo) -> Unit,
) {
    val context = LocalContext.current
    val entries = remember(providers) {
        val density = context.resources.displayMetrics.density
        providers.map { info ->
            val appLabel = runCatching {
                pm.getApplicationLabel(pm.getApplicationInfo(info.provider.packageName, 0)).toString()
            }.getOrDefault(info.provider.packageName)
            val sx = ceil(info.minWidth / density / 90f).toInt().coerceIn(1, Grid.COLUMNS)
            val sy = ceil(info.minHeight / density / 110f).toInt().coerceIn(1, Grid.ROWS)
            WidgetEntry(info, appLabel, info.loadLabel(pm), "$sx×$sy")
        }.sortedWith(compareBy({ it.appLabel.lowercase() }, { it.widgetLabel.lowercase() }))
    }
    Column(Modifier.fillMaxHeight(0.9f).padding(horizontal = 16.dp).verticalScroll(rememberScrollState())) {
        Text("ウィジェットを追加", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = ColorText,
            modifier = Modifier.padding(vertical = 10.dp))
        var lastApp: String? = null
        entries.forEach { e ->
            if (e.appLabel != lastApp) {
                lastApp = e.appLabel
                Text(e.appLabel, fontSize = 12.sp, color = ColorAccent,
                    modifier = Modifier.padding(top = 14.dp, bottom = 4.dp))
            }
            Row(
                Modifier.fillMaxWidth().clickable { onPick(e.info) }.padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                WidgetPreview(e.info, Modifier.size(width = 84.dp, height = 56.dp).background(ColorTile))
                Spacer(Modifier.width(14.dp))
                Column {
                    Text(e.widgetLabel, fontSize = 14.sp, color = ColorText, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(e.sizeText, fontSize = 11.sp, color = ColorDim)
                }
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}

/** ウィジェットのプレビュー画像（無ければ提供アイコン）を非同期で読み込んで表示。 */
@Composable
private fun WidgetPreview(info: AppWidgetProviderInfo, modifier: Modifier) {
    val context = LocalContext.current
    var bmp by remember(info) { mutableStateOf<ImageBitmap?>(null) }
    LaunchedEffect(info) {
        bmp = withContext(Dispatchers.IO) {
            val d = runCatching { info.loadPreviewImage(context, 0) }.getOrNull()
                ?: runCatching { info.loadIcon(context, 0) }.getOrNull()
            d?.let { dr ->
                runCatching { dr.toBitmap().asImageBitmap() }.getOrNull()
                    ?: runCatching { dr.toBitmap(220, 140).asImageBitmap() }.getOrNull()
            }
        }
    }
    val b = bmp
    if (b != null) Image(b, contentDescription = null, modifier = modifier, contentScale = ContentScale.Fit)
    else Box(modifier)
}
