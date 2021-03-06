package io.kokoichi.sample.kotlinscraping

import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.rememberImagePainter
import io.kokoichi.sample.kotlinscraping.ui.theme.KotlinScrapingTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Log.d("test", "set content")
            KotlinScrapingTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {

                    val navController = rememberNavController()
                    NavHost(navController, startDestination = "main") {

                        composable("main") { MainView(navController) }

                        composable(
                            route = "webView/url={url}",
                            arguments = listOf(navArgument("url") { type = NavType.StringType })
                        ) { backStackEntry ->

                            var url = backStackEntry.arguments?.getString("url")
                            if (url == null) {
                                url = "https://blog.nogizaka46.com/"
                            }
                            WebViewWidget(url)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MainView(navController: NavHostController) {
    Column() {
        Greeting("Android")
        BlogPics("renkaiwamoto", navController)
    }
}

data class urls(
    val imgUrl: String,
    val contentUrl: String,
)

data class article(
    val title: String,
    val url: String,
)

fun scrape_web(ip: String) = runBlocking {
    var topContent = ""

    var imgUrls = mutableListOf<String>()
    var articles = mutableListOf<article>()
    var urlsList = mutableListOf<urls>()
    withContext(Dispatchers.Default) {
        val document = Jsoup.connect("https://blog.nogizaka46.com/renka.iwamoto/").get()

//        val CSS_QUERY =
        val titleH1s = document.select("h1.clearfix")
        Log.d("test", "length is " + titleH1s.size)
        for (titleH1 in titleH1s) {
            val aTag = titleH1.select("a").first()
            Log.d("test", aTag.text())
            Log.d("test", aTag.attr("href"))

            articles.add(
                article(
                    title = aTag.text(),
                    url = aTag.attr("href").replace("http://", "https://")
                )
            )

            if (topContent == "") {
                topContent = aTag.attr("href").replace("http://", "https://")
            }
        }

        Log.d("test", "topContent: " + topContent)

        for (article in articles) {
//            var doc1 = Jsoup.connect(topContent).get()
            var doc1 = Jsoup.connect(article.url).get()

            var doc2 = doc1.select(".entrybody")
            val imgTags = doc2.select("img")
            // MAYBE: ??????????????????????????????2????????????????????????Total 5????????????????????????????????????
            // ?????????????????????????????????????????????
//        var numContentImgs = 0

            var url = ""
            for (imgTag in imgTags) {
                url = imgTag.attr("src").replace("http://", "https://")
                if (url != "https://img.nogizaka46.com/blog/img/dot.gif") {
                    imgUrls.add(url)

                    urlsList.add(
                        urls(
                            imgUrl = url,
                            contentUrl = article.url
                        )
                    )
                }
            }
        }

        Log.d("test", imgUrls.toString())
    }
    return@runBlocking urlsList
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    KotlinScrapingTheme {
        Greeting("Android")
    }
}

@Composable
fun BlogPics(name: String, navController: NavHostController) {
    // TODO, ???????????????blog ????????????????????????
    // ?????????????????????????????????iwamotorenka -> ???????????????renka.iwamoto
    var urls = scrape_web("hoge")
    Log.d("test", "actual #urls is " + urls.size)
    LazyRow {
        items(urls) { url ->

            Image(
                painter = rememberImagePainter(url.imgUrl),  // ???????????? size ????????????
                contentDescription = null,
                modifier = Modifier
                    .width(160.dp)  // ?????????size ???????????????????????????????????????
                    .size(200.dp)
                    .padding(0.dp, 10.dp, 0.dp, 0.dp)
                    .clickable {

                        // navigation ????????? / ?????????????????????????????????????????????
                        val encodedUrl = url.contentUrl.replace("/", SLASH_ENCODED)

                        // navigation ????????????????????????
                        val WEB_VIEW_URL = "webView" + "/url=$encodedUrl"
                        navController.navigate(WEB_VIEW_URL)

                        Log.d("test", "content URL is: " + encodedUrl)
                    }
            )
        }
    }
}

val SLASH_ENCODED = "%2F"

@Composable
fun WebViewWidget(
    contentUrl: String
) {
    AndroidView(
        factory = {
            WebView(it)
        },
        update = { webView ->
            webView.webViewClient = WebViewClient()
            Log.d("test", contentUrl)
            webView.loadUrl(contentUrl)
        }
    )
}

