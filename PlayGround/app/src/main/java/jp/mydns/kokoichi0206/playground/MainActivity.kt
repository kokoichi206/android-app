package jp.mydns.kokoichi0206.playground

import android.content.Context
import android.os.Bundle
import android.os.Environment
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import jp.mydns.kokoichi0206.playground.blogs.MaterialYouTest
import jp.mydns.kokoichi0206.playground.ui.theme.PlayGroundTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        storage(this)

        setContent {
            PlayGroundTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White),
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

                        MaterialYouTest()
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