package jp.mydns.kokoichi0206.playground

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import jp.mydns.kokoichi0206.playground.blogs.MaterialYouTest
import jp.mydns.kokoichi0206.playground.ui.theme.PlayGroundTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PlayGroundTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White),
                        contentAlignment = Alignment.Center,
                    ) {
                        // Kaigi()
//                        ModifierTest()

//                        Messages()

                        // DevSummit animation
//                        Column {
//                            TopBar()
//                            ExpandingText()
//
//                            SurveyProgress()
//                            NormalSurveyProgress()
//
//                            ImageBorder()
//                        }

//                        Column {
//                            AnimatedIndicator()
//                            NormalIndicator()
//                        }

//                        Column(
//                            modifier = Modifier.padding(16.dp)
//                        ) {
//                            StylingText()
//                        }

//                        MaterialYou()
//                        MaterialYouCollapse()

                        MaterialYouTest()
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PlayGroundTheme {
        Greeting("Android")
    }
}