package jp.mydns.kokoichi0206.dragdrop

import android.os.Bundle
import android.util.Log
import android.util.Size
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import jp.mydns.kokoichi0206.dragdrop.ui.theme.DragDropTheme
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DragDropTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    DraggableBox()
                }
            }
        }
    }
}

@Composable
fun DraggableBox() {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val height = this.constraints.maxHeight
        val width = this.constraints.maxWidth

        var offsetX by remember { mutableStateOf(0f) }
        var offsetY by remember { mutableStateOf(0f) }

        var vx by remember { mutableStateOf(0f) }
        var vy by remember { mutableStateOf(0f) }

        val BOX_SIZE = 50.dp
        val BOX_SIZE_PX = with(LocalDensity.current) { BOX_SIZE.toPx() }

        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasSize = size
            val canvasWidth = size.width
            val canvasHeight = size.height
            drawRect(
                color = Color.Green,
                size = Size()
            )
        }

        Box(
            Modifier
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .clip(RoundedCornerShape(100))
                .background(Color.Blue)
                .size(BOX_SIZE)
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consumeAllChanges()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                        vx = dragAmount.x
                        vy = dragAmount.y
                        Log.d("hoge", vx.toString())
                    }
                }
        )

        LaunchedEffect(key1 = offsetX) {
            delay(20)
            if (offsetX + BOX_SIZE_PX > width) {
                offsetX = width - BOX_SIZE_PX
            }
            offsetX += vx
            offsetY += vy

            if (offsetX + BOX_SIZE_PX > width) {
                offsetX = width - BOX_SIZE_PX
                vx *= -1
            }
            if (offsetX < 0) {
                offsetX = 0f
                vx *= -1
            }
            if (offsetY + BOX_SIZE_PX > height) {
                offsetY = height - BOX_SIZE_PX
                vy *= -1
            }
            if (offsetY < 0) {
                offsetY = 0f
                vy *= -1
            }
        }
    }
}

fun updateX() {

}
