/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package jp.mydns.kokoichi206.wearosplayground.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.foundation.lazy.AutoCenteringParams
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.scrollAway
import com.google.android.horologist.compose.rotaryinput.rotaryWithScroll
import jp.mydns.kokoichi206.wearosplayground.presentation.theme.WearOSPlaygroundTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WearOSPlaygroundTheme {
                WearApp()
//                WearAppHoro()
            }
        }
    }
}

@OptIn(ExperimentalHorologistApi::class)
@Composable
fun WearApp() {
    val focusRequester = remember { FocusRequester() }
    val columnState = rememberScalingLazyListState(initialCenterItemIndex = 0)

    Scaffold(
        // スクロール位置を表示する。
        positionIndicator = {
            PositionIndicator(scalingLazyListState = columnState)
        },
        timeText = {
            TimeText()
        },
        modifier = Modifier
            .background(MaterialTheme.colors.background),
        vignette = {
            Vignette(
                // 比較のため上にだけ周辺減光を入れる。
                vignettePosition = VignettePosition.Top,
            )
        },
    ) {
        // 画面の上部と下部で要素が小さくなる。
        androidx.wear.compose.foundation.lazy.ScalingLazyColumn(
            modifier = Modifier
                .rotaryWithScroll(columnState, focusRequester)
                .fillMaxWidth(),
            state = columnState,
            // デフォルトでは idx=1 から始まる。
            autoCentering = AutoCenteringParams(itemIndex = 0)
        ) {
            items(15) { i ->
                Text(text = "content $i")
            }
        }

        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }
}


@OptIn(ExperimentalHorologistApi::class)
@Composable
fun WearAppHoro() {
    val columnState = ScalingLazyColumnDefaults.belowTimeText().create()

    Scaffold(
        timeText = {
            TimeText(
                modifier = Modifier
                    .fillMaxSize()
                    // https://github.com/android/wear-os-samples/blob/86978edd1e253d8d1022607f54db0ec6a113968a/ComposeAdvanced/app/src/main/java/com/example/android/wearable/composeadvanced/presentation/ui/watchlist/WatchListScreen.kt#L109
                    .scrollAway(columnState)
            )
        },
        modifier = Modifier
            .background(MaterialTheme.colors.background),
        vignette = {
            Vignette(
                // 比較のため上にだけ周辺減光を入れる。
                vignettePosition = VignettePosition.Top,
            )
        },
    ) {
        ScalingLazyColumn(
            modifier = Modifier
                .fillMaxWidth(),
            columnState = columnState,
        ) {
            items(15) { i ->
                Text(text = "content $i")
            }
        }
    }
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WearApp()
}