package jp.mydns.kokoichi206.quietlauncher.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jp.mydns.kokoichi206.quietlauncher.LauncherState
import jp.mydns.kokoichi206.quietlauncher.data.AppRepository
import jp.mydns.kokoichi206.quietlauncher.model.AppEntry
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

val ColorBg = Color(0xFF0B0D12)
val ColorSheet = Color(0xFF10131B)
val ColorText = Color(0xFFE8EAF0)
val ColorSub = Color(0xFF8B93A5)
val ColorDim = Color(0xFF565E70)
val ColorAccent = Color(0xFF7AA2F7)
val ColorGreen = Color(0xFF8FD49A)
val ColorAmber = Color(0xFFE5C07B)
val ColorTile = Color(0xFF161B27)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LauncherApp(state: LauncherState, repo: AppRepository) {
    var showDrawer by remember { mutableStateOf(false) }
    var showDebug by remember { mutableStateOf(false) }

    BackHandler(enabled = true) {
        showDrawer = false
        showDebug = false
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(ColorBg)
            .pointerInput(Unit) {
                detectVerticalDragGestures { _, dragAmount ->
                    if (dragAmount < -14f) showDrawer = true
                }
            },
    ) {
        HomeScreen(
            state = state,
            repo = repo,
            onOpenDrawer = { showDrawer = true },
            onOpenDebug = { showDebug = true },
        )
    }

    if (showDrawer) {
        val drawerSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { showDrawer = false },
            sheetState = drawerSheetState,
            containerColor = ColorSheet,
        ) {
            DrawerSheet(state, repo, drawerSheetState) { showDrawer = false }
        }
    }
    if (showDebug) {
        ModalBottomSheet(
            onDismissRequest = { showDebug = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = ColorSheet,
        ) {
            DebugPanel(state)
        }
    }
}

@Composable
private fun HomeScreen(
    state: LauncherState,
    repo: AppRepository,
    onOpenDrawer: () -> Unit,
    onOpenDebug: () -> Unit,
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

    Column(Modifier.fillMaxSize().padding(horizontal = 26.dp)) {
        Row(Modifier.fillMaxWidth().padding(top = 36.dp), horizontalArrangement = Arrangement.End) {
            Text(
                "⚙",
                fontSize = 18.sp,
                color = ColorDim,
                modifier = Modifier.clickable(onClick = onOpenDebug).padding(8.dp),
            )
        }
        Text(
            timeFormat.format(now),
            fontSize = 56.sp,
            fontWeight = FontWeight.Light,
            color = ColorText,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
        Text(
            dateFormat.format(now),
            fontSize = 14.sp,
            color = ColorSub,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
        if (state.event.category != null) {
            Text(
                "🗓 ${state.event.label}",
                fontSize = 13.sp,
                color = ColorSub,
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 6.dp),
            )
        }

        Spacer(Modifier.height(28.dp))

        state.suggestion?.let { app ->
            SuggestionRow(app, state.event.label, repo) { state.onAppClick(app) }
            Spacer(Modifier.height(14.dp))
        }

        state.slots.forEach { app ->
            SlotRow(
                app = app,
                highlighted = app.packageName in state.swappedIn,
                repo = repo,
            ) { state.onAppClick(app) }
        }
        if (state.coldStart) {
            Text(
                "データ不足: ラベル辞書順で表示中",
                fontSize = 11.sp,
                color = ColorDim,
                modifier = Modifier.padding(top = 6.dp),
            )
        }

        Spacer(Modifier.weight(1f))

        Text("固定（学習対象外）", fontSize = 10.sp, color = ColorDim)
        Spacer(Modifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            state.pinned.forEach { app ->
                PinnedTile(app, repo, Modifier.weight(1f)) { state.onAppClick(app) }
            }
        }
        Text(
            "︿ アプリ一覧",
            fontSize = 12.sp,
            color = ColorDim,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clickable(onClick = onOpenDrawer)
                .padding(vertical = 16.dp),
        )
    }
}

@Composable
private fun SuggestionRow(
    app: AppEntry,
    eventLabel: String,
    repo: AppRepository,
    onClick: () -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .border(1.dp, ColorAmber.copy(alpha = 0.55f), RoundedCornerShape(14.dp))
            .background(ColorAmber.copy(alpha = 0.07f), RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AppIconImage(app.packageName, repo, 34.dp)
        Spacer(Modifier.width(12.dp))
        Column {
            Text(app.label, fontSize = 15.sp, color = ColorText)
            Text("予定『$eventLabel』から", fontSize = 10.sp, color = ColorAmber)
        }
    }
}

@Composable
private fun SlotRow(
    app: AppEntry,
    highlighted: Boolean,
    repo: AppRepository,
    onClick: () -> Unit,
) {
    val borderColor = if (highlighted) ColorGreen.copy(alpha = 0.6f) else Color.Transparent
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .border(1.dp, borderColor, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 6.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AppIconImage(app.packageName, repo, 42.dp)
        Spacer(Modifier.width(16.dp))
        Text(app.label, fontSize = 17.sp, color = ColorText)
    }
}

@Composable
private fun PinnedTile(
    app: AppEntry,
    repo: AppRepository,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Row(
        modifier
            .background(ColorTile, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AppIconImage(app.packageName, repo, 30.dp)
        Spacer(Modifier.width(10.dp))
        Text(app.label, fontSize = 13.sp, color = ColorText, maxLines = 1)
    }
}

@Composable
fun AppIconImage(packageName: String, repo: AppRepository, size: Dp) {
    var bitmap by remember(packageName) { mutableStateOf(IconCache.peek(packageName)) }
    if (bitmap == null) {
        LaunchedEffect(packageName) { bitmap = IconCache.load(packageName, repo) }
    }
    val loaded = bitmap
    if (loaded != null) {
        Image(loaded, contentDescription = null, modifier = Modifier.size(size))
    } else {
        Box(Modifier.size(size).background(Color(0xFF1D2330), RoundedCornerShape(10.dp)))
    }
}
