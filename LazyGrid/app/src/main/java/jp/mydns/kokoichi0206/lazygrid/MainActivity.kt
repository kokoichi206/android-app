package jp.mydns.kokoichi0206.lazygrid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import jp.mydns.kokoichi0206.lazygrid.ui.theme.LazyGridTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LazyGridTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                    val state = rememberLazyListState(
                        initialFirstVisibleItemIndex = 99
                    )
                    LazyVerticalGrid(
//                        cells = GridCells.Fixed(5),
                        cells = GridCells.Adaptive(100.dp),
                        state = state,
                        content = {
                            items(100) { i ->
                                Box(
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .aspectRatio(1f)
                                        .clip(RoundedCornerShape(5.dp))
                                        .background(Color.Green),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(text = "Item $i")
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}
