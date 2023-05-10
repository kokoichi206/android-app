package jp.mydns.kokoichi206.g.imagesilder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import jp.mydns.kokoichi206.g.imagesilder.ui.theme.ImageSilderTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val imgs = listOf(
            R.drawable.github_mark,
            R.drawable.github_grass,
            R.drawable.pie_chart,
        )

        setContent {
            ImageSilderTheme {
                val pagerState = rememberPagerState()
                val scope = rememberCoroutineScope()

                Box(modifier = Modifier.fillMaxSize()) {
                    HorizontalPager(
                        pageCount = imgs.size,
                        state = pagerState,
                        key = { imgs[it] },
                        pageSize = PageSize.Fill,
                        // pageSize = PageSize.Fixed(300.dp),
                    ) { idx ->
                        Image(
                            painter = painterResource(id = imgs[idx]),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }

                    Box(
                        modifier = Modifier
                            .offset(y = -(16).dp)
                            .fillMaxWidth(0.5f)
                            .clip(RoundedCornerShape(100))
                            .background(MaterialTheme.colorScheme.background)
                            .padding(8.dp)
                            .align(Alignment.BottomCenter),
                    ) {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(
                                        pagerState.currentPage - 1,
                                    )
                                }
                            },
                            modifier = Modifier.align(Alignment.CenterStart),
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowLeft,
                                contentDescription = "Go back",
                            )
                        }

                        IconButton(
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(
                                        pagerState.currentPage + 1,
                                    )
                                }
                            },
                            modifier = Modifier.align(Alignment.CenterEnd),
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowRight,
                                contentDescription = "Go forward",
                            )
                        }
                    }
                }
            }
        }
    }
}
