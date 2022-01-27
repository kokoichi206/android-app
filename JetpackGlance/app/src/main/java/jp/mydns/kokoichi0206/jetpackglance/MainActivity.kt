package jp.mydns.kokoichi0206.jetpackglance

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import jp.mydns.kokoichi0206.jetpackglance.ui.theme.JetpackGlanceTheme


class MainActivity() : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        val intent = intent
        val position = intent.getStringExtra("POSITION") ?: "00"

        super.onCreate(savedInstanceState)
        setContent {
            JetpackGlanceTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Greeting(position)
                }
            }
        }
    }
}


@Composable
fun Greeting(pos: String) {
    Box(
      modifier = Modifier
          .fillMaxSize()
          .background(Color.White),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "You Tapped $pos",
            fontSize = 40.sp,
            color = Color.Black,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    JetpackGlanceTheme {
        Greeting("Android")
    }
}