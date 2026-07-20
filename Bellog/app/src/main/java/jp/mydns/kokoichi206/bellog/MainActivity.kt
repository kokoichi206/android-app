package jp.mydns.kokoichi206.bellog

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddTask
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Switch
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import jp.mydns.kokoichi206.bellog.data.InstalledApp
import jp.mydns.kokoichi206.bellog.data.InstalledAppRepository
import jp.mydns.kokoichi206.bellog.data.NotificationGroup
import jp.mydns.kokoichi206.bellog.data.NotificationContentType
import jp.mydns.kokoichi206.bellog.data.NotificationRecord
import jp.mydns.kokoichi206.bellog.data.NotificationStore
import jp.mydns.kokoichi206.bellog.data.NotificationTask
import jp.mydns.kokoichi206.bellog.data.SourcePreferences
import jp.mydns.kokoichi206.bellog.config.GroupingConfigRepository
import jp.mydns.kokoichi206.bellog.notification.ACTION_NOTIFICATIONS_CHANGED
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val Mint = Color(0xFF3D9B62)
private val LightColors = lightColorScheme(
    primary = Mint,
    onPrimary = Color.White,
    background = Color(0xFFF7F9F8),
    onBackground = Color(0xFF142033),
    surface = Color.White,
    onSurface = Color(0xFF142033),
    surfaceVariant = Color(0xFFEAF0EC),
    onSurfaceVariant = Color(0xFF647069),
)
private val DarkColors = darkColorScheme(
    primary = Color(0xFF79D99C),
    onPrimary = Color(0xFF07391D),
    background = Color(0xFF101512),
    onBackground = Color(0xFFE2EAE4),
    surface = Color(0xFF19201C),
    onSurface = Color(0xFFE2EAE4),
    surfaceVariant = Color(0xFF252E29),
    onSurfaceVariant = Color(0xFFAFBBB3),
)

class MainActivity : ComponentActivity() {
    private val revision = mutableIntStateOf(0)
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            revision.intValue++
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val initialScreen = when (intent.action) {
            ACTION_OPEN_SOURCES -> Screen.SETTINGS
            else -> Screen.NOTIFICATIONS
        }
        setContent { BellogTheme { BellogApp(revision.intValue, initialScreen) } }
    }

    override fun onStart() {
        super.onStart()
        ContextCompat.registerReceiver(
            this,
            receiver,
            IntentFilter(ACTION_NOTIFICATIONS_CHANGED),
            ContextCompat.RECEIVER_NOT_EXPORTED,
        )
    }

    override fun onResume() {
        super.onResume()
        revision.intValue++
    }

    override fun onStop() {
        unregisterReceiver(receiver)
        super.onStop()
    }
}

@Composable
private fun BellogTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors,
        content = content,
    )
}

private enum class Screen { NOTIFICATIONS, TASKS, SETTINGS, DETAIL }

private const val ACTION_OPEN_SOURCES = "jp.mydns.kokoichi206.bellog.action.OPEN_SOURCES"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BellogApp(revision: Int, initialScreen: Screen) {
    val context = LocalContext.current
    val store = remember { NotificationStore(context.applicationContext) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var records by remember { mutableStateOf(emptyList<NotificationRecord>()) }
    var tasks by remember { mutableStateOf(emptyList<NotificationTask>()) }
    var screen by remember { mutableStateOf(initialScreen) }
    var selectedGroup by remember { mutableStateOf<NotificationGroup?>(null) }
    var notificationAccess by remember { mutableStateOf(false) }

    suspend fun showUndoSnackbar(message: String): SnackbarResult {
        snackbarHostState.currentSnackbarData?.dismiss()
        return snackbarHostState.showSnackbar(
            message = message,
            actionLabel = "元に戻す",
            duration = SnackbarDuration.Long,
        )
    }

    LaunchedEffect(Unit) {
        val reclassified = withContext(Dispatchers.IO) {
            val repository = GroupingConfigRepository(context.applicationContext)
            repository.initialize(store)
        }
        if (reclassified) records = withContext(Dispatchers.IO) { store.records() }
    }

    LaunchedEffect(revision) {
        notificationAccess = context.packageName in NotificationManagerCompat.getEnabledListenerPackages(context)
        val stored = withContext(Dispatchers.IO) { store.records() to store.tasks() }
        records = stored.first
        tasks = stored.second
    }

    fun goBack() {
        screen = Screen.NOTIFICATIONS
        selectedGroup = null
    }

    fun deleteGroup(group: NotificationGroup) {
        records = records.filterNot { it.conversationKey == group.conversationKey }
        scope.launch {
            withContext(Dispatchers.IO) { store.deleteConversation(group.conversationKey) }
            if (showUndoSnackbar("通知を削除しました") == SnackbarResult.ActionPerformed) {
                records = withContext(Dispatchers.IO) {
                    store.restoreRecords(group.records)
                    store.records()
                }
            }
        }
    }

    fun createTask(group: NotificationGroup) {
        scope.launch {
            val createdTask = withContext(Dispatchers.IO) { store.createTaskFromConversation(group) }
            if (createdTask == null) {
                snackbarHostState.currentSnackbarData?.dismiss()
                snackbarHostState.showSnackbar(
                    message = "この通知はタスク化済みです",
                    duration = SnackbarDuration.Short,
                )
                return@launch
            }
            tasks = withContext(Dispatchers.IO) { store.tasks() }
            if (showUndoSnackbar("タスクに追加しました") == SnackbarResult.ActionPerformed) {
                tasks = withContext(Dispatchers.IO) {
                    store.deleteTask(createdTask.id)
                    store.tasks()
                }
            }
        }
    }

    fun completeTask(task: NotificationTask) {
        tasks = tasks.filterNot { it.id == task.id }
        scope.launch {
            withContext(Dispatchers.IO) { store.deleteTask(task.id) }
            if (showUndoSnackbar("タスクを完了しました") == SnackbarResult.ActionPerformed) {
                tasks = withContext(Dispatchers.IO) {
                    store.restoreTask(task)
                    store.tasks()
                }
            }
        }
    }

    BackHandler(enabled = screen == Screen.DETAIL, onBack = ::goBack)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
                title = {
                    Column {
                        Text(
                            when (screen) {
                                Screen.DETAIL -> "通知プレビュー"
                                Screen.NOTIFICATIONS -> "通知"
                                Screen.TASKS -> "タスク"
                                Screen.SETTINGS -> "設定"
                            },
                            fontWeight = FontWeight.Medium,
                        )
                        if (screen == Screen.NOTIFICATIONS) {
                            Text("右でタスク化・左で削除", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                },
                navigationIcon = {
                    if (screen == Screen.DETAIL) {
                        TextButton(onClick = ::goBack) { Text("戻る") }
                    }
                },
            )
        },
        bottomBar = {
            if (screen != Screen.DETAIL) {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                    NavigationBarItem(
                        selected = screen == Screen.NOTIFICATIONS,
                        onClick = { screen = Screen.NOTIFICATIONS },
                        icon = { Icon(Icons.Outlined.Notifications, contentDescription = null) },
                        label = { Text("通知") },
                    )
                    NavigationBarItem(
                        selected = screen == Screen.TASKS,
                        onClick = { screen = Screen.TASKS },
                        icon = { Icon(Icons.Outlined.CheckCircle, contentDescription = null) },
                        label = { Text("タスク") },
                    )
                    NavigationBarItem(
                        selected = screen == Screen.SETTINGS,
                        onClick = { screen = Screen.SETTINGS },
                        icon = { Icon(Icons.Outlined.Settings, contentDescription = null) },
                        label = { Text("設定") },
                    )
                }
            }
        },
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when (screen) {
                Screen.NOTIFICATIONS -> InboxScreen(
                    records = records,
                    tasks = tasks,
                    notificationAccess = notificationAccess,
                    onEnableAccess = {
                        context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
                    },
                    onOpenSettings = { screen = Screen.SETTINGS },
                    onCreateTask = ::createTask,
                    onDeleteGroup = ::deleteGroup,
                    onOpenGroup = {
                        val readGroup = it.copy(records = it.records.map { record -> record.copy(isRead = true) })
                        selectedGroup = readGroup
                        records = records.map { record ->
                            if (record.conversationKey == it.conversationKey) record.copy(isRead = true) else record
                        }
                        screen = Screen.DETAIL
                        scope.launch(Dispatchers.IO) { store.markConversationRead(it.conversationKey) }
                    },
                )
                Screen.TASKS -> TasksScreen(tasks = tasks, onComplete = ::completeTask)
                Screen.SETTINGS -> SettingsScreen(
                    store = store,
                    onCleared = {
                        records = emptyList()
                    },
                )
                Screen.DETAIL -> selectedGroup?.let { DetailScreen(it) }
            }
        }
    }
}

@Composable
private fun InboxScreen(
    records: List<NotificationRecord>,
    tasks: List<NotificationTask>,
    notificationAccess: Boolean,
    onEnableAccess: () -> Unit,
    onOpenSettings: () -> Unit,
    onCreateTask: (NotificationGroup) -> Unit,
    onDeleteGroup: (NotificationGroup) -> Unit,
    onOpenGroup: (NotificationGroup) -> Unit,
) {
    var query by remember { mutableStateOf("") }
    val groups = remember(records, query) {
        records
            .filter {
                query.isBlank() || listOf(it.appLabel, it.conversationTitle, it.sender, it.message, it.summary.orEmpty()).any { value ->
                    value.contains(query, ignoreCase = true)
                }
            }
            .groupBy { it.conversationKey }
            .map { (key, values) ->
                val sorted = values.sortedBy { it.receivedAt }
                val latest = sorted.last()
                NotificationGroup(key, latest.packageName, latest.appLabel, latest.conversationTitle, sorted)
            }
            .sortedByDescending { it.receivedAt }
    }

    Column(Modifier.fillMaxSize()) {
        Box(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("通知を検索") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
            )
        }
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (!notificationAccess) {
                item {
                    SetupCard(
                        title = "通知へのアクセスを許可",
                        text = "選択したアプリの通知を、この端末だけに保存するために必要です。",
                        button = "設定を開く",
                        onClick = onEnableAccess,
                    )
                }
            }
            if (records.isEmpty()) {
                item {
                    SetupCard(
                        title = "まだ通知はありません",
                        text = "収集するアプリを選ぶと、次に届いた通知からここへ溜まります。",
                        button = "アプリを選ぶ",
                        onClick = onOpenSettings,
                    )
                }
            } else if (groups.isEmpty()) {
                item { Text("一致する通知はありません", color = MaterialTheme.colorScheme.onSurfaceVariant) }
            } else {
                items(groups, key = { it.conversationKey }) { group ->
                    val groupTasks = tasks.filter { it.sourceConversationKey == group.conversationKey }
                    SwipeableTimelineCard(
                        group = group,
                        taskCount = groupTasks.size,
                        canCreateTask = groupTasks.none { it.notificationReceivedAt == group.receivedAt },
                        onClick = { onOpenGroup(group) },
                        onCreateTask = { onCreateTask(group) },
                        onDelete = { onDeleteGroup(group) },
                    )
                }
            }
            item { Spacer(Modifier.height(20.dp)) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeableTimelineCard(
    group: NotificationGroup,
    taskCount: Int,
    canCreateTask: Boolean,
    onClick: () -> Unit,
    onCreateTask: () -> Unit,
    onDelete: () -> Unit,
) {
    val state = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            when (value) {
                SwipeToDismissBoxValue.StartToEnd -> {
                    onCreateTask()
                    return@rememberSwipeToDismissBoxState false
                }
                SwipeToDismissBoxValue.EndToStart -> {
                    onDelete()
                    return@rememberSwipeToDismissBoxState true
                }
                SwipeToDismissBoxValue.Settled -> return@rememberSwipeToDismissBoxState false
            }
        },
        positionalThreshold = { distance -> distance * 0.35f },
    )
    SwipeToDismissBox(
        state = state,
        enableDismissFromStartToEnd = canCreateTask,
        backgroundContent = {
            val taskDirection = state.dismissDirection == SwipeToDismissBoxValue.StartToEnd
            val background = if (taskDirection) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.errorContainer
            }
            val foreground = if (taskDirection) {
                MaterialTheme.colorScheme.onPrimaryContainer
            } else {
                MaterialTheme.colorScheme.onErrorContainer
            }
            Row(
                Modifier.fillMaxSize().clip(RoundedCornerShape(20.dp)).background(background).padding(horizontal = 20.dp),
                horizontalArrangement = if (taskDirection) Arrangement.Start else Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    if (taskDirection) Icons.Outlined.AddTask else Icons.Outlined.Delete,
                    contentDescription = null,
                    tint = foreground,
                )
                Spacer(Modifier.width(8.dp))
                Text(if (taskDirection) "タスク化" else "削除", color = foreground, fontWeight = FontWeight.Medium)
            }
        },
    ) {
        TimelineCard(group, taskCount, onClick)
    }
}

@Composable
private fun SetupCard(title: String, text: String, button: String, onClick: () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant), shape = RoundedCornerShape(20.dp)) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(title, fontWeight = FontWeight.Medium)
            Text(text, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
            Button(onClick = onClick) { Text(button) }
        }
    }
}

@Composable
private fun TimelineCard(group: NotificationGroup, taskCount: Int, onClick: () -> Unit) {
    val formatter = remember { SimpleDateFormat("M/d H:mm", Locale.JAPAN) }
    val darkTheme = isSystemInDarkTheme()
    val textColor = if (group.isRead) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (group.isRead) {
                if (darkTheme) Color(0xFF141A16) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.58f)
            } else {
                MaterialTheme.colorScheme.surface
            },
        ),
        shape = RoundedCornerShape(20.dp),
    ) {
        Row(Modifier.padding(15.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AppIcon(group.packageName, group.appLabel)
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!group.isRead) {
                        Box(Modifier.padding(end = 8.dp).size(8.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary))
                    }
                    Text(
                        group.title.ifBlank { group.appLabel },
                        color = textColor,
                        fontWeight = if (group.isRead) FontWeight.Normal else FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f).padding(end = 8.dp),
                    )
                    Text(formatter.format(Date(group.receivedAt)), fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Row(
                    modifier = Modifier.padding(top = 2.dp, bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        if (group.isRead) "${group.appLabel} · 確認済み" else group.appLabel,
                        fontSize = 11.sp,
                        color = if (group.isRead) {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        } else {
                            MaterialTheme.colorScheme.primary
                        },
                    )
                    if (taskCount > 0) {
                        Text(
                            if (taskCount == 1) "タスク化済み" else "タスク ${taskCount}件",
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontSize = 10.sp,
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .padding(horizontal = 7.dp, vertical = 2.dp),
                        )
                    }
                }
                group.records.takeLast(4).forEachIndexed { index, record ->
                    NotificationPreview(
                        record = record,
                        color = if (!group.isRead && index == group.records.takeLast(4).lastIndex) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = if (index == 0) Modifier else Modifier.padding(top = 3.dp),
                    )
                }
                if (group.records.size > 4) {
                    Text(
                        "ほか${group.records.size - 4}件",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 5.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun TasksScreen(tasks: List<NotificationTask>, onComplete: (NotificationTask) -> Unit) {
    if (tasks.isEmpty()) {
        Column(
            Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                Icons.Outlined.CheckCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(42.dp),
            )
            Text("タスクはありません", fontWeight = FontWeight.Medium, modifier = Modifier.padding(top = 12.dp))
            Text(
                "通知を右へスワイプすると、ここへ移動します。",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 5.dp),
            )
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(tasks, key = { it.id }) { task ->
            TaskCard(task = task, onComplete = { onComplete(task) })
        }
        item { Spacer(Modifier.height(20.dp)) }
    }
}

@Composable
private fun TaskCard(task: NotificationTask, onComplete: () -> Unit) {
    val formatter = remember { SimpleDateFormat("M/d H:mm", Locale.JAPAN) }
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
    ) {
        Row(
            Modifier.fillMaxWidth().padding(15.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AppIcon(task.packageName, task.appLabel)
            Column(Modifier.weight(1f)) {
                Text(task.title, fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                if (task.body.isNotBlank()) {
                    Text(
                        task.body,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
                Text(
                    "${task.appLabel} · ${formatter.format(Date(task.createdAt))}",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(top = 7.dp),
                )
            }
            IconButton(onClick = onComplete) {
                Icon(Icons.Outlined.CheckCircle, contentDescription = "完了")
            }
        }
    }
}

@Composable
private fun AppIcon(packageName: String, label: String) {
    val packageManager = LocalContext.current.packageManager
    val bitmap = remember(packageName) {
        try {
            packageManager.getApplicationIcon(packageName).toBitmap(96, 96).asImageBitmap()
        } catch (_: PackageManager.NameNotFoundException) {
            null
        }
    }
    Box(
        Modifier.size(42.dp).clip(RoundedCornerShape(13.dp)).background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
        if (bitmap == null) {
            Text(label.take(1).uppercase(), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
        } else {
            Image(bitmap, contentDescription = "$label のアイコン", modifier = Modifier.fillMaxSize())
        }
    }
}

@Composable
private fun DetailScreen(group: NotificationGroup) {
    val formatter = remember { SimpleDateFormat("M/d H:mm:ss", Locale.JAPAN) }
    val listState = rememberLazyListState()
    LaunchedEffect(group.conversationKey) {
        listState.scrollToItem(group.records.size + 1)
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                AppIcon(group.packageName, group.appLabel)
                Column {
                    Text(group.title.ifBlank { group.appLabel }, fontWeight = FontWeight.Medium)
                    Text("${group.appLabel}から保存", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                }
            }
            Spacer(Modifier.height(18.dp))
        }
        items(group.records, key = { it.id }) { record ->
            Row(Modifier.fillMaxWidth().padding(bottom = 9.dp), verticalAlignment = Alignment.Bottom) {
                Column(
                    Modifier
                        .fillMaxWidth(0.86f)
                        .clip(RoundedCornerShape(16.dp, 16.dp, 16.dp, 5.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(12.dp),
                ) {
                    if (record.sender.isNotBlank()) {
                        Text("送信者: ${record.sender}", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                    }
                    NotificationBody(record)
                    Text(
                        "受信  ${formatter.format(Date(record.receivedAt))}",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.End),
                    )
                }
            }
        }
        item {
            Text(
                "通知に含まれた内容だけを表示しています。ここで見ても元アプリの既読状態は変わりません。",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 12.dp),
            )
        }
    }
}

@Composable
private fun NotificationPreview(record: NotificationRecord, color: Color, modifier: Modifier = Modifier) {
    Text(
        text = if (record.contentType == NotificationContentType.TEXT) record.message else record.contentType.label,
        fontSize = 14.sp,
        color = color,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier,
    )
}

@Composable
private fun NotificationBody(record: NotificationRecord) {
    if (record.contentType == NotificationContentType.TEXT) {
        Text(record.message)
        return
    }
    Text(record.contentType.label, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Medium)
    Text(
        "通知には${record.contentType.label}の本体が含まれていません",
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 12.sp,
    )
}

private val NotificationContentType.label: String
    get() = when (this) {
        NotificationContentType.TEXT -> "メッセージ"
        NotificationContentType.IMAGE -> "写真"
        NotificationContentType.STICKER -> "スタンプ"
        NotificationContentType.VIDEO -> "動画"
        NotificationContentType.FILE -> "ファイル"
        NotificationContentType.OTHER -> "その他"
    }

@Composable
private fun SettingsScreen(store: NotificationStore, onCleared: () -> Unit) {
    val context = LocalContext.current
    val repository = remember { InstalledAppRepository(context.applicationContext) }
    val preferences = remember { SourcePreferences(context.applicationContext) }
    var apps by remember { mutableStateOf(emptyList<InstalledApp>()) }
    var selected by remember { mutableStateOf(preferences.selectedPackages()) }
    var query by remember { mutableStateOf("") }
    var confirmClear by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { apps = withContext(Dispatchers.IO) { repository.apps() } }
    val visibleApps = remember(apps, query) {
        apps.filter { query.isBlank() || it.label.contains(query, ignoreCase = true) }
    }

    Column(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
            Text("通知を保存するアプリ", fontWeight = FontWeight.Medium, modifier = Modifier.padding(bottom = 4.dp))
            Text(
                "選択後に届く通知から保存します。過去の通知には遡りません。",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
            )
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("アプリを検索") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp),
                shape = RoundedCornerShape(16.dp),
            )
        }
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp),
        ) {
            items(visibleApps, key = { it.packageName }) { app ->
                AppSourceRow(
                    app = app,
                    checked = app.packageName in selected,
                    onChecked = { checked ->
                        preferences.setSelected(app.packageName, checked)
                        selected = preferences.selectedPackages()
                    },
                )
            }
            item {
                TextButton(onClick = { confirmClear = true }, modifier = Modifier.padding(top = 24.dp)) {
                    Text("保存した通知をすべて削除", color = MaterialTheme.colorScheme.error)
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }

    if (confirmClear) {
        AlertDialog(
            onDismissRequest = { confirmClear = false },
            title = { Text("すべて削除しますか？") },
            text = { Text("Bellogに保存した通知だけを削除します。元アプリの通知やメッセージには影響しません。") },
            confirmButton = {
                TextButton(onClick = {
                    store.clear()
                    confirmClear = false
                    onCleared()
                }) { Text("削除") }
            },
            dismissButton = { TextButton(onClick = { confirmClear = false }) { Text("キャンセル") } },
        )
    }
}

@Composable
private fun AppSourceRow(app: InstalledApp, checked: Boolean, onChecked: (Boolean) -> Unit) {
    val bitmap = remember(app.packageName) { app.icon.toBitmap(96, 96).asImageBitmap() }
    Row(
        Modifier.fillMaxWidth().clickable { onChecked(!checked) }.padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(bitmap, contentDescription = null, modifier = Modifier.size(42.dp).clip(CircleShape))
        Spacer(Modifier.width(13.dp))
        Column(Modifier.weight(1f)) {
            Text(app.label, fontWeight = FontWeight.Medium)
            Text(
                if (checked) "通知を保存中" else "保存しない",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp,
            )
        }
        Switch(checked = checked, onCheckedChange = onChecked)
    }
}
