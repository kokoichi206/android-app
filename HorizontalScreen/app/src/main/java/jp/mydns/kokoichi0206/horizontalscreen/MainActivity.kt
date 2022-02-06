package jp.mydns.kokoichi0206.horizontalscreen

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import jp.mydns.kokoichi0206.horizontalscreen.ui.theme.HorizontalScreenTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("hoge", "onCreate")
        setContent {
            HorizontalScreenTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    ShowName()
                }
            }
        }
    }
}


var isVertical by mutableStateOf(true)
@Composable
fun ShowName() {
    isVertical =
        LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT

    if (isVertical) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .background(Color.Blue),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "First Name!", fontSize = 24.sp,
                    textAlign = TextAlign.Center
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .background(Color.Red),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Last Name!", fontSize = 24.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    } else {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .background(Color.Blue),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "First Name!", fontSize = 24.sp,
                    textAlign = TextAlign.Center
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .background(Color.Red),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Last Name!", fontSize = 24.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
