package jp.mydns.kokoichi0206.deeplink

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import jp.mydns.kokoichi0206.deeplink.ui.theme.DeeplinkTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DeeplinkTheme {
            }
        }
    }
}
