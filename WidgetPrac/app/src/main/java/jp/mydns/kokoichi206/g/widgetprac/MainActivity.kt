package jp.mydns.kokoichi206.g.widgetprac

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import jp.mydns.kokoichi206.g.widgetprac.ui.theme.WidgetPracTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WidgetPracTheme {
            }
        }
    }
}
