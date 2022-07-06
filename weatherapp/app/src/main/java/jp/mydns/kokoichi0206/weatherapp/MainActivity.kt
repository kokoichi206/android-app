package jp.mydns.kokoichi0206.weatherapp

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint
import jp.mydns.kokoichi0206.weatherapp.presentation.WeatherCard
import jp.mydns.kokoichi0206.weatherapp.presentation.WeatherForecastToday
import jp.mydns.kokoichi0206.weatherapp.presentation.WeatherForecastWeek
import jp.mydns.kokoichi0206.weatherapp.presentation.WeatherViewModel
import jp.mydns.kokoichi0206.weatherapp.presentation.ui.theme.DarkBlue
import jp.mydns.kokoichi0206.weatherapp.presentation.ui.theme.DeepBlue
import jp.mydns.kokoichi0206.weatherapp.presentation.ui.theme.WeatherAppTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: WeatherViewModel by viewModels()
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {
            viewModel.loadWeatherInfo()
        }
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            )
        )

        setContent {
            WeatherAppTheme {
                val swipeRefreshState = rememberSwipeRefreshState(false)

                SwipeRefresh(
                    state = swipeRefreshState,
                    onRefresh = {
                        viewModel.loadWeatherInfo()
                    },
                ) {
                    MainScreen(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun MainScreen(
    viewModel: WeatherViewModel,
) {
    // Change ActionBar color
    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(
        color = DarkBlue,
    )

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        // ----- Main -----
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBlue)
                .verticalScroll(rememberScrollState()),
        ) {
            WeatherCard(
                state = viewModel.state,
                backgroundColor = DeepBlue,
            )
            Spacer(modifier = Modifier.height(16.dp))
            WeatherForecastToday(state = viewModel.state)

            Spacer(modifier = Modifier.height(16.dp))
            WeatherForecastWeek(state = viewModel.state)
        }

        // ----- Additional -----
        if (viewModel.state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
            )
        }
        viewModel.state.error?.let { error ->
            Text(
                text = error,
                color = Color.Red,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center),
            )
        }
    }
}
