package jp.mydns.kokoichi206.g.widgetprac

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import jp.mydns.kokoichi206.g.widgetprac.presentation.UserScreen
import jp.mydns.kokoichi206.g.widgetprac.presentation.ViewerViewModel
import jp.mydns.kokoichi206.g.widgetprac.ui.theme.WidgetPracTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WidgetPracTheme {
                val viewModel = hiltViewModel<ViewerViewModel>()
                val state by viewModel.state.collectAsState()
                UserScreen(state = state)
            }
        }
    }
}
