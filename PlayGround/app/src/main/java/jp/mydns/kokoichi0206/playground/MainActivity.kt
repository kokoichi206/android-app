package jp.mydns.kokoichi0206.playground

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.PersistableBundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.window.OnBackInvokedDispatcher
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.os.BuildCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import jp.mydns.kokoichi0206.playground.alarm.AlarmItem
import jp.mydns.kokoichi0206.playground.alarm.AndroidAlarmScheduler
import jp.mydns.kokoichi0206.playground.blogs.MaterialYouTest
import jp.mydns.kokoichi0206.playground.downloader.AndroidDownloader
import jp.mydns.kokoichi0206.playground.ui.theme.PlayGroundTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import java.io.File
import java.time.LocalDateTime

class MainActivity : ComponentActivity() {

    private val api by lazy {
        Retrofit.Builder()
            .baseUrl("http://example.kokoichi0206.mydns.jp/")
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(AuthInterceptor())
                    .addInterceptor(
                        HttpLoggingInterceptor().setLevel(
                            HttpLoggingInterceptor.Level.BODY
                        )
                    )
                    .build()
            )
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(AnnotationApi::class.java)
    }

    @SuppressLint("UnsafeOptInUsageError")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        val user = User(
//            name = "John Doe",
//            birthDate = "2004-02-02"
////                    birthDate = "2004-02-02h    " // Throw exception example
//        )
//        lifecycleScope.launch {
//            api.getPost()
//            api.getPost()
//        }
//        return

        val downloader = AndroidDownloader(this)
        downloader.downloadFile("https://img.com/test.png")

        // 通知用のチャネルを作る必要がある
        val channel = NotificationChannel(
            "boot_completed",
            "Boot Completed",
            NotificationManager.IMPORTANCE_DEFAULT,
        )
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)

        registerForActivityResult(ActivityResultContracts.RequestPermission()) {}
        // テスト通知
        val builder = NotificationCompat.Builder(this, "boot_completed")
            .setSmallIcon(R.drawable.playground)
            .setContentTitle("MainActivity onCreate")
            .setContentText("textContent")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(2, builder.build())
        }

//        if (BuildCompat.isAtLeastT()) {
//            // Back Gesture を無効になった。
//            onBackInvokedDispatcher.registerOnBackInvokedCallback(
//                OnBackInvokedDispatcher.PRIORITY_DEFAULT
//            ) {
//                // onBackPressed logic
//                Log.d("hoge", "onBackPressed gesture")
//            }
//        }

        WindowCompat.setDecorFitsSystemWindows(window, false)

//        storage(this)

        val scheduler = AndroidAlarmScheduler(this)
        var alarmItem = AlarmItem(
            time = LocalDateTime.now()
                .plusSeconds(10),
            message = "Message From Alarm",
        )
        scheduler.schedule(alarmItem)

        setContent {
            PlayGroundTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        // Kaigi()
//                        ModifierTest()

//                        Messages()

                        // DevSummit animation
//                        Column {
//                            TopBar()
//                            ExpandingText()
//
//                            SurveyProgress()
//                            NormalSurveyProgress()
//
//                            ImageBorder()
//                        }

//                        Column {
//                            AnimatedIndicator()
//                            NormalIndicator()
//                        }

//                        Column(
//                            modifier = Modifier.padding(16.dp)
//                        ) {
//                            StylingText()
//                        }

//                        MaterialYou()
//                        MaterialYouCollapse()

//                        MaterialYouTest()

//                        MyImagePickerWithScaffold()

//                        ImageCrop()

//                        ScrollDetector()

//                        LazyStaggeredGrid()

//                        MessageText()

//                        SelectionUI()

                        Material3()
                    }
                }
            }
        }
    }
}

// https://www.youtube.com/watch?v=jcO6p5TlcGs&list=PLWz5rJ2EKKc8PO99T1QQLrPAJILqxJXW6&index=8&ab_channel=AndroidDevelopers
fun storage(context: Context) {

    runBlocking(Dispatchers.IO) {
        val url = ""
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        // use を使うと、勝手に閉じてくれるのでメモリリークの心配がない
        client.newCall(request).execute().use { response ->
            response.body?.byteStream()?.use { input ->
                val target = File(context.filesDir, "members.json")

                target.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        }
    }

//    val type = Environment.DIRECTORY_PICTURES
//    val target = Environment.getExternalStorageDirectory(type)
//    val newImage = File(target, "new-image-by-playground")
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PlayGroundTheme {
        Greeting("Android")
    }
}