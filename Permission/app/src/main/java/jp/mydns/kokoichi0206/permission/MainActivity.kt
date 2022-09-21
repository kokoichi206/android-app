package jp.mydns.kokoichi0206.permission

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.shouldShowRationale
import jp.mydns.kokoichi0206.permission.ui.theme.PermissionTheme

@OptIn(ExperimentalPermissionsApi::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PermissionTheme {
                val permissionsState = rememberMultiplePermissionsState(
                    permissions = listOf(
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.CAMERA,
                    )
                )

                // permission を手動でユーザーが許可した時などは、
                // onResume などのライフサイクルイベントで拾うしかない（onCreate のみだと不十分）
                // Jetpack Compose でライフサイクルイベントを拾うにはこれ！
                val lifecycleOwner = LocalLifecycleOwner.current
                DisposableEffect(
                    key1 = lifecycleOwner,
                    effect = {
                        val observer = LifecycleEventObserver { _, event ->
                            if (event == Lifecycle.Event.ON_START) {
                                // これが実際に権限を要求するやつ〜
                                permissionsState.launchMultiplePermissionRequest()
                            }
                        }
                        lifecycleOwner.lifecycle.addObserver(observer)

                        onDispose {
                            lifecycleOwner.lifecycle.removeObserver(observer)
                        }
                    },
                )

//                LaunchedEffect(key1 = true) {
//                    permissionsState.launchMultiplePermissionRequest()
//                }
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    permissionsState.permissions.forEach { per ->
                        when(per.permission) {
                            Manifest.permission.CAMERA -> {
                                when {
                                    per.status.isGranted -> {
                                        Text(text = "Camera permission accepted")
                                    }
                                    per.status.shouldShowRationale -> {
                                        Text(text = "Camera permission is needed")
                                    }
                                    per.isPermanentlyDenied() -> {
                                        Text(text = "Camera permission is permanently denied.")
                                    }
                                }
                            }
                            Manifest.permission.RECORD_AUDIO -> {
                                when {
                                    per.status.isGranted -> {
                                        Text(text = "Record audio permission accepted")
                                    }
                                    per.status.shouldShowRationale -> {
                                        Text(text = "Record audio permission is needed")
                                    }
                                    per.isPermanentlyDenied() -> {
                                        Text(text = "Record audio permission is permanently denied.")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("hoge", "onresume")
    }
}
