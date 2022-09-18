package jp.mydns.kokoichi0206.pictureinpicture

import android.app.PendingIntent
import android.app.PictureInPictureParams
import android.app.RemoteAction
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Bundle
import android.util.Rational
import android.widget.VideoView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toAndroidRect
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.viewinterop.AndroidView
import jp.mydns.kokoichi0206.pictureinpicture.ui.theme.PictureInPictureTheme

class MainActivity : ComponentActivity() {

    class MyReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            println("clicked on pip actions")
        }
    }

    private val isPipSupported by lazy {
        packageManager.hasSystemFeature(
            PackageManager.FEATURE_PICTURE_IN_PICTURE
        )
    }

    private var videoViewBounds = Rect()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PictureInPictureTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                    AndroidView(
                        factory = {
                            VideoView(it, null).apply {
                                setVideoURI(Uri.parse("android.resource://$packageName/${R.raw.sample}"))
                                start()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .onGloballyPositioned {
                                // Compose が配置されたときに呼ばれるブロック
                                videoViewBounds = it
                                    .boundsInWindow()
                                    .toAndroidRect()
                            }
                    )
                }
            }
        }
    }

    private fun updatedPipParams(): PictureInPictureParams? {
        return PictureInPictureParams.Builder()
            .setSourceRectHint(videoViewBounds)
            .setAspectRatio(Rational(16, 9))
            .setActions(
                listOf(
                    RemoteAction(
                        Icon.createWithResource(
                            applicationContext,
                            R.drawable.ic_baseline_adb_24,
                        ),
                        "adb",
                        "adb",
                        PendingIntent.getBroadcast(
                            applicationContext,
                            0,
                            Intent(applicationContext, MyReceiver::class.java),
                            PendingIntent.FLAG_IMMUTABLE,
                        )
                    )
                )
            )
            .build()
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        if (!isPipSupported) {
            return
        }
        updatedPipParams()?.let { params ->
            enterPictureInPictureMode(params)
        }
    }
}
