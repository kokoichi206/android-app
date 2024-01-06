package jp.mydns.kokoichi0206.playground

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.MagnifierStyle
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.magnifier
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

@Composable
fun ModifierText() {
//    MarqueeText()

//    MagnifyImage()

    CanvasModifier()
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MarqueeText() {
    // The text is moving
    Text(
        text = "Very very very long text. Very very very long text. Very very very long text. Very very very long text. Very very very long text. Very very very long text. ",
        maxLines = 1,
        modifier = Modifier
            .basicMarquee()
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MagnifyImage() {
    // dynamic offset
    var offset by remember {
        mutableStateOf(Offset.Zero)
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(true) {
                detectDragGestures { change, dragAmount ->
                    offset = change.position
                }
            }
            .magnifier(
                sourceCenter = {
                    offset - Offset(x = 0f, y = 50f)
                },
                magnifierCenter = {
                    offset - Offset(x = 0f, y = 50f)
                },
                style = MagnifierStyle(
                    size = DpSize(100.dp, 100.dp),
                    cornerRadius = 100.dp,
                )
            )
    ) {
        Image(
            painter = painterResource(id = R.drawable.ai),
            contentDescription = "image by ai",
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.FillWidth,
        )
    }
}

@Composable
fun CanvasModifier() {
    // dynamic offset
    var offset by remember {
        mutableStateOf(Offset.Zero)
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(true) {
                detectDragGestures { change, dragAmount ->
                    offset = change.position
                }
            }
            // .drawBehind {  }
            .drawWithContent {
                drawContent()
                drawCircle(
                    color = Color.Red,
                    radius = 200f,
                    center = offset,
                )
            }
    ) {
        Image(
            painter = painterResource(id = R.drawable.ai),
            contentDescription = "image by ai",
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.FillWidth,
        )
    }
}
