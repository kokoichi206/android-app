package jp.mydns.kokoichi0206.googlemaps

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.mydns.kokoichi0206.googlemaps.domain.repository.ParkingSpotRepository
import jp.mydns.kokoichi0206.googlemaps.presentation.MapScreen
import jp.mydns.kokoichi0206.googlemaps.ui.theme.GoogleMapsTheme
import javax.inject.Inject

@HiltViewModel
class MainActivity @Inject constructor(
    private val repository: ParkingSpotRepository
) : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GoogleMapsTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    MapScreen()
                }
            }
        }
    }
}
