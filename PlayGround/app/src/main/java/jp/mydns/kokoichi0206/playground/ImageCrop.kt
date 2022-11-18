package jp.mydns.kokoichi0206.playground

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.res.painterResource

@Composable
fun ImageCrop() {

    Box(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        Image(
            modifier = Modifier
                .fillMaxSize(),
            painter = painterResource(id = R.drawable.background),
            contentDescription = "",
        )

        Canvas(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            with(drawContext.canvas.nativeCanvas) {
                val checkPoint = saveLayer(null, null)

                drawRect(
                    color = Color.DarkGray.copy(alpha = 0.8f),
                    size = Size(canvasWidth, canvasHeight)
                )
                drawCircle(
                    color = Color.Transparent,
                    center = Offset(x = canvasWidth / 2, y = canvasHeight / 2),
                    radius = size.minDimension * 0.4f,
                    blendMode = BlendMode.Clear,
                )

                restoreToCount(checkPoint)
            }
        }
    }
}
