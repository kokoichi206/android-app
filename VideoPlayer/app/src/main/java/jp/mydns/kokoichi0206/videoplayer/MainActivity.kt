package jp.mydns.kokoichi0206.videoplayer

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import jp.mydns.kokoichi0206.videoplayer.ui.theme.VideoPlayerTheme
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VideoPlayerTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                    MovieListScreen()
                }
            }
        }
    }
}

@Composable
fun VideoView(context: Context, mediaUri: Uri) {
    val exoPlayer = remember(context) {
        SimpleExoPlayer.Builder(context).build().apply {
            val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(context,
                Util.getUserAgent(context, context.packageName))

            val source = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(mediaUri))
            this.prepare(source)
            this.playWhenReady = true
        }
    }

    DisposableEffect(
        AndroidView(
            modifier =
            Modifier.testTag("VideoPlayer"),
            factory = {
                PlayerView(context).apply {
                    player = exoPlayer
                    layoutParams =
                        FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams
                                .MATCH_PARENT,
                            ViewGroup.LayoutParams
                                .WRAP_CONTENT
                        )
                }
            }
        )
    ) {
        onDispose {
            // relase player when no longer needed
            exoPlayer.release()
        }
    }

}

interface MovieApi {
    @GET("cgi-bin/movies.sh")
    suspend fun getMovies(): Movies
}

data class Movies(
    val base_dir: String,
    val titles: List<String>,
)

@Composable
fun MovieListScreen() {
    val MY_DOMAIN = "https://kokoichi0206.mydns.jp"
    val retrofit = Retrofit.Builder()
        .baseUrl(MY_DOMAIN)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(MovieApi::class.java)

    var target by remember {
        mutableStateOf("")
    }
    var movies by remember {
        mutableStateOf(List(20) { "" })
    }
    LaunchedEffect(true) {
        val response = retrofit.getMovies()
        target = response.base_dir
        movies = response.titles
    }

    val baseUrl = "${MY_DOMAIN}/${target}"
    LazyColumn {
        items(movies) { movie ->
            if (movie.isNotEmpty()) {
                Text(
                    text = movie,
                    fontSize = 25.sp,
                )
                ExoPlayer(url = "${baseUrl}/${movie}")
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@SuppressLint("RememberReturnType")
@Composable
fun ExoPlayer(
    url: String,
) {
    val context = LocalContext.current
    val player = SimpleExoPlayer.Builder(context).build()
    val playerView = PlayerView(context)
    val mediaItem = MediaItem.fromUri(url)
    val playWhenReady by rememberSaveable {
        mutableStateOf(true)
    }
    player.setMediaItem(mediaItem)
    playerView.player = player

    LaunchedEffect(player) {
        player.prepare()
//        player.playWhenReady = playWhenReady
    }

    AndroidView(
        factory = {
            playerView
        },
        modifier = Modifier
            .height(300.dp)
    )
}