package jp.mydns.kokoichi206.g.widgetprac

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import jp.mydns.kokoichi206.g.widgetprac.presentation.UserScreen
import jp.mydns.kokoichi206.g.widgetprac.presentation.ViewerViewModel
import jp.mydns.kokoichi206.g.widgetprac.presentation.login.GithubConstants
import jp.mydns.kokoichi206.g.widgetprac.presentation.login.GithubWebViewClient
import jp.mydns.kokoichi206.g.widgetprac.ui.theme.WidgetPracTheme
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WidgetPracTheme {
                val viewModel = hiltViewModel<ViewerViewModel>()
                val state by viewModel.state.collectAsState()
                UserScreen(state = state)

//                val ss = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())
//                val githubAuthURLFull =  GithubConstants.AUTHURL + "?client_id=" + GithubConstants.CLIENT_ID + "&scope=" + GithubConstants.SCOPE + "&redirect_uri=" + GithubConstants.REDIRECT_URI + "&state=" + ss
////                Login("http://localhost")
////                Login(githubAuthURLFull)
//                Login(githubAuthURLFull)

            }
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun Login(url: String) {
    Log.d("hoge", "accessing $url ...")

    AndroidView(
        factory = ::WebView,
        update = { webView ->
            webView.webViewClient = GithubWebViewClient()
            // false
            Log.d("webView.settings.javaScriptEnabled", webView.settings.javaScriptEnabled.toString())
            // false
            Log.d("webView.settings.javaScriptCanOpenWindowsAutomatically", webView.settings.javaScriptCanOpenWindowsAutomatically.toString())
            webView.settings.javaScriptEnabled = true
            webView.settings.javaScriptCanOpenWindowsAutomatically = true
            webView.loadUrl(url)
        }
    )
}
