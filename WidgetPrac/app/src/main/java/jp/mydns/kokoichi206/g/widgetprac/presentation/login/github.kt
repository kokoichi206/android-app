package jp.mydns.kokoichi206.g.widgetprac.presentation.login

import android.net.Uri
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.json.JSONTokener
import java.io.OutputStreamWriter
import java.net.URL
import javax.net.ssl.HttpsURLConnection

object GithubConstants {
    val CLIENT_ID = "xxx"
    val CLIENT_SECRET = "xxx"
    val REDIRECT_URI = "xxx"
    val SCOPE = "read:user,user:email"
    val AUTHURL = "https://github.com/login/oauth/authorize"
    val TOKENURL = "https://github.com/login/oauth/access_token"
}

class GithubWebViewClient : WebViewClient() {

    override fun shouldOverrideUrlLoading(
        view: WebView?,
        request: WebResourceRequest?
    ): Boolean {
        Log.d("hoge", "shouldOverrideUrlLoading")
        Log.d("hoge", "request!!.url.toString(): ${request!!.url.toString()}")
        if (request!!.url.toString().startsWith(GithubConstants.REDIRECT_URI)) {
            handleUrl(request.url.toString())
            // Close the dialog after getting the authorization code
            if (request.url.toString().contains("code=")) {
                Log.d("hoge", request.url.toString())
            }
            return true
        }
        view?.loadUrl(request!!.url.toString())
        return true
    }

    // Check webview url for access token code or error
    private fun handleUrl(url: String) {
        val uri = Uri.parse(url)
        if (url.contains("code")) {
            val githubCode = uri.getQueryParameter("code") ?: ""
            requestForAccessToken(githubCode)
        }
    }

    fun requestForAccessToken(code: String) {
        val grantType = "authorization_code"
        val postParams =
            "grant_type=" + grantType + "&code=" + code + "&redirect_uri=" + GithubConstants.REDIRECT_URI + "&client_id=" + GithubConstants.CLIENT_ID + "&client_secret=" + GithubConstants.CLIENT_SECRET
        GlobalScope.launch(Dispatchers.Default) {
            val url = URL(GithubConstants.TOKENURL)
            val httpsURLConnection =
                withContext(Dispatchers.IO) { url.openConnection() as HttpsURLConnection }
            httpsURLConnection.requestMethod = "POST"
            httpsURLConnection.setRequestProperty(
                "Accept",
                "application/json"
            );
            httpsURLConnection.doInput = true
            httpsURLConnection.doOutput = true
            val outputStreamWriter = OutputStreamWriter(httpsURLConnection.outputStream)
            withContext(Dispatchers.IO) {
                outputStreamWriter.write(postParams)
                outputStreamWriter.flush()
            }
            val response = httpsURLConnection.inputStream.bufferedReader()
                .use { it.readText() }  // defaults to UTF-8
            withContext(Dispatchers.Main) {
                val jsonObject = JSONTokener(response).nextValue() as JSONObject
                val accessToken = jsonObject.getString("access_token") //The access token
                // Get user's id, first name, last name, profile pic url
                fetchGithubUserProfile(accessToken)
            }
        }
    }

    fun fetchGithubUserProfile(token: String) {
        GlobalScope.launch(Dispatchers.Default) {
            val tokenURLFull =
                "https://api.github.com/user"
            val url = URL(tokenURLFull)
            val httpsURLConnection =
                withContext(Dispatchers.IO) { url.openConnection() as HttpsURLConnection }
            httpsURLConnection.requestMethod = "GET"
            httpsURLConnection.setRequestProperty("Authorization", "Bearer $token")
            httpsURLConnection.doInput = true
            httpsURLConnection.doOutput = false
            val response = httpsURLConnection.inputStream.bufferedReader()
                .use { it.readText() }  // defaults to UTF-8
            val jsonObject = JSONTokener(response).nextValue() as JSONObject
            Log.i("GitHub Access Token: ", token)
            // GitHub Id
            val githubId = jsonObject.getInt("id")
            Log.i("GitHub Id: ", githubId.toString())
            // GitHub Display Name
            val githubDisplayName = jsonObject.getString("login")
            Log.i("GitHub Display Name: ", githubDisplayName)
            // GitHub Email
            val githubEmail = jsonObject.getString("email")
            Log.i("GitHub Email: ", githubEmail)
            // GitHub Profile Avatar URL
            val githubAvatarURL = jsonObject.getString("avatar_url")
            Log.i("Github Profile Avatar URL: ", githubAvatarURL)
        }
    }
}
