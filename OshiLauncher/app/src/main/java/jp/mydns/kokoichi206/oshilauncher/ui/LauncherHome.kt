package jp.mydns.kokoichi206.oshilauncher.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OshiLauncherApp(repo: AppRepository, store: CustomizationStore) {
    var apps by remember { mutableStateOf(emptyList<AppEntry>()) }
    var showDrawer by remember { mutableStateOf(false) }
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
        BackHandler(enabled = true) {
            showDrawer = false
            if (showSettings) closeSettings()
        }

        Box(Modifier.fillMaxSize().background(ColorBg)) {
            WallpaperLayer(store, revision, Modifier.fillMaxSize())
            Box(
                Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectVerticalDragGestures { _, dragAmount ->
                            if (dragAmount < -14f) showDrawer = true
                        }
                    },
            ) {
                HomeContent(
                    apps = apps,
                    repo = repo,
                    onLaunch = { repo.launch(it.packageName) },
                    onOpenDrawer = { showDrawer = true },
                    onOpenSettings = { showSettings = true },
                )
            }
        }

        if (showSettings) {
            CustomizeScreen(
                apps = apps,
                repo = repo,
                store = store,
                onClose = { closeSettings() },
            )
        }

        if (showDrawer) {
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ModalBottomSheet(
                onDismissRequest = { showDrawer = false },
                sheetState = sheetState,
                containerColor = ColorSheet,
            ) {
                AppDrawer(apps, repo) { app ->
                    repo.launch(app.packageName)
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
    onLaunch: (AppEntry) -> Unit,
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

    Column(Modifier.fillMaxSize().padding(horizontal = 26.dp)) {
        Row(Modifier.fillMaxWidth().padding(top = 36.dp), horizontalArrangement = Arrangement.End) {
            Text(
                "⚙",
                fontSize = 18.sp,
                color = ColorText,
                modifier = Modifier.clickable(onClick = onOpenSettings).padding(8.dp),
            )
        }

        Spacer(Modifier.height(40.dp))
        Text(
            timeFormat.format(now),
            fontSize = 64.sp,
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

        Spacer(Modifier.weight(1f))

        // 推し背景を隠しすぎないよう、ホームにはお気に入り数件だけ置く。
        apps.take(4).forEach { app ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .clickable { onLaunch(app) }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AppIconImage(app.packageName, repo, 40.dp)
                Spacer(Modifier.width(16.dp))
                Text(app.label, fontSize = 16.sp, color = ColorText)
            }
        }

        Text(
            "︿ アプリ一覧",
            fontSize = 12.sp,
            color = ColorSub,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clickable(onClick = onOpenDrawer)
                .padding(vertical = 16.dp),
        )
    }
}

@Composable
private fun AppDrawer(apps: List<AppEntry>, repo: AppRepository, onClick: (AppEntry) -> Unit) {
    LazyColumn(
        Modifier
            .fillMaxHeight(0.88f)
            .padding(horizontal = 20.dp),
    ) {
        items(apps, key = { it.packageName }) { app ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .clickable { onClick(app) }
                    .padding(vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AppIconImage(app.packageName, repo, 38.dp)
                Spacer(Modifier.width(14.dp))
                Text(app.label, fontSize = 15.sp, color = ColorText)
            }
        }
    }
}
