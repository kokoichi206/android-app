package jp.mydns.kokoichi0206.effecthandler

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.effect.Effect
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.work.*
import coil.compose.rememberImagePainter
import dagger.hilt.android.AndroidEntryPoint
import jp.mydns.kokoichi0206.effecthandler.ui.theme.EffectHandlerTheme
import jp.mydns.kokoichi0206.effecthandler.workmanager.ColorFilterWorkder
import jp.mydns.kokoichi0206.effecthandler.workmanager.DownloadWorker
import jp.mydns.kokoichi0206.effecthandler.workmanager.WorkerParams
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Duration

//@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        if (checkSelfPermission(Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACTIVITY_RECOGNITION), 0)
//                recreate()
//            }
//        }

//        val downloadRequest = PeriodicWorkRequestBuilder<DownloadWorker>(
//            Duration.ofHours(5)
//        )
        val downloadRequest = OneTimeWorkRequestBuilder<DownloadWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(
                        NetworkType.CONNECTED
                    )
                    .build()
            )
            .build()
        val colorFilterRequest = OneTimeWorkRequestBuilder<ColorFilterWorkder>()
            .build()
        val workManager = WorkManager.getInstance(applicationContext)
        setContent {
            // not using...
            val viewModel = viewModel<MainViewModel>(
                factory = viewModelFactory {
                    MainViewModel(
                        SensorApp.appModule.lightSensor,
                        SensorApp.appModule.proximitySensor,
                        SensorApp.appModule.stepCounterSensor,
                    )
                }
            )

            EffectHandlerTheme {
                val workInfos = workManager
                    .getWorkInfosForUniqueWorkLiveData("download")
                    .observeAsState()
                    .value
                val downloadInfo = remember(key1 = workInfos) {
                    workInfos?.find { it.id == downloadRequest.id }
                }
                val filterInfo = remember(key1 = workInfos) {
                    workInfos?.find { it.id == colorFilterRequest.id }
                }
                val imageUri by derivedStateOf {
                    val downloadUri = downloadInfo?.outputData?.getString(WorkerParams.IMAGE_URI)
                        ?.toUri()
                    val filterUri = filterInfo?.outputData?.getString(WorkerParams.FILTER_URI)
                        ?.toUri()
                    filterUri ?: downloadUri
                }
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    imageUri?.let { uri ->
                        Image(
                            painter = rememberImagePainter(
                                data = uri
                            ),
                            contentDescription = null,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    Button(
                        onClick = {
                            workManager
                                .beginUniqueWork(
                                    "download",
                                    ExistingWorkPolicy.KEEP,
                                    downloadRequest
                                )
                                .then(colorFilterRequest)
                                .enqueue()
                        },
                        enabled = downloadInfo?.state != WorkInfo.State.RUNNING,
                    ) {
                        Text(text = "Start download")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    when (downloadInfo?.state) {
                        WorkInfo.State.RUNNING -> Text("Downloading...")
                        WorkInfo.State.SUCCEEDED -> Text("Download succeeded")
                        WorkInfo.State.FAILED -> Text("Download failed")
                        WorkInfo.State.CANCELLED -> Text("Download cancelled.")
                        WorkInfo.State.ENQUEUED -> Text("Download enqueued")
                        WorkInfo.State.BLOCKED -> Text("Download blocked")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    when (filterInfo?.state) {
                        WorkInfo.State.RUNNING -> Text("Applying filter...")
                        WorkInfo.State.SUCCEEDED -> Text("Applying filter succeeded")
                        WorkInfo.State.FAILED -> Text("Applying filter failed")
                        WorkInfo.State.CANCELLED -> Text("Applying filter cancelled.")
                        WorkInfo.State.ENQUEUED -> Text("Applying filter enqueued")
                        WorkInfo.State.BLOCKED -> Text("Applying filter blocked")
                    }
                }
            }
        }
    }
}

@Composable
fun sensorTest() {
    val viewModel = viewModel<MainViewModel>()
    val isReady = viewModel.isReady

    // LocalComposition より提供される Context を取得する
    val context = LocalContext.current

    // LocalComposition より提供される Lifecycle を取得する
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    // Permission Request を実行する Permission を定義する
    val permission = Manifest.permission.ACTIVITY_RECOGNITION

    // Permission Request を実行するための Launcher を作成する
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            Log.v("hoge", "PERMISSION REQUEST RESULT ${isGranted}")
            viewModel.setGranted()
        }
    )

    val lifecycleObserver = remember {
        LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                if (!permission.isGrantedPermission(context)) {
                    launcher.launch(permission)
                }
            }
        }
    }

    // lifecycle または lifecycleObserver が変化した、また破棄されたら呼び出される
    DisposableEffect(lifecycle, lifecycleObserver) {
        lifecycle.addObserver(lifecycleObserver)
        onDispose {
            lifecycle.removeObserver(lifecycleObserver)
        }
    }


    if (isReady) {
        val isDark = viewModel.isDark
        val steps = viewModel.stepValues
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (isDark) Color.DarkGray else Color.White
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (isDark) {
                    "It's dark outside"
                } else {
                    "It's light outside"
                },
                color = if (isDark) Color.White else Color.DarkGray
            )

            Text(
                text = steps
            )
        }
    }
}

private fun String.isGrantedPermission(context: Context): Boolean {
    // checkSelfPermission は PERMISSION_GRANTED or PERMISSION_DENIED のどちらかを返す
    // そのため checkSelfPermission の戻り値が PERMISSION_GRANTED であれば許可済みになる。
    return context.checkSelfPermission(this) == PackageManager.PERMISSION_GRANTED
}

@Composable
fun Effect() {
    var text by remember {
        mutableStateOf("")
    }
    EffectHandlerTheme {
        // LaunchedEffect is also Composable
        LaunchedEffect(key1 = text) {
            delay(1000L)
            println("The text is $text")
        }
    }
}

@Composable
fun RememberCoroutineScopeDemo() {
    val scope = rememberCoroutineScope()
    Button(onClick = {
        scope.launch {
            delay(1000L)
            println("Hello world!")
        }
    }) {

    }
}

@Composable
fun RememberUpdatedStateDemo(
    onTimeout: () -> Unit,
) {
    val updatedOnTimeout by rememberUpdatedState(newValue = onTimeout)
    LaunchedEffect(true) {
        delay(3000L)
        onTimeout()
    }
}

@Composable
fun DisposableEffectDemo() {
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(key1 = lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE) {
                println("On pause called")
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

@Composable
fun SideEffectDemo() {
    SideEffect {
        println("Called after every successful recomposition")
    }
}

@Composable
fun DerivedStateOfD() {
    var counter by remember {
        mutableStateOf(0)
    }
    val counterText by derivedStateOf {
        "The counter is $counter"
    }
    Button(onClick = { counter++ }) {
        Text(text = counterText)
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    EffectHandlerTheme {
        Greeting("Android")
    }
}