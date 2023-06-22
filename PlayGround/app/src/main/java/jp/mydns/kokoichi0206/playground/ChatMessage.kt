package jp.mydns.kokoichi0206.playground

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin


@Composable
fun MessageText() {
    Text(
        modifier = Modifier
            .wrapContentSize()
            .graphicsLayer {
                shape = MessageTextShape(16.dp.toPx())
                clip = true
            }
            .background(color = Color(0xFF79E278))
            .padding(8.dp),
        text = "これメッセージです。",
    )
}

class MessageTextShape(private val cornerRadius: Float) : Shape {

    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        return Outline.Generic(
            path = drawMyMessagePath(size = size, cornerRadius = cornerRadius)
        )
    }
}

fun drawMyMessagePath(size: Size, cornerRadius: Float): Path {
    return Path().apply {
        reset()

        // Top left arc
        arcTo(
            rect = Rect(
                left = 0f,
                top = 0f,
                right = 2 * cornerRadius,
                bottom = 2 * cornerRadius,
            ),
            startAngleDegrees = 180.0f,
            sweepAngleDegrees = 90.0f,
            forceMoveTo = false,
        )
        lineTo(x = size.width - cornerRadius, y = 0f)

        // Top right arc
        arcTo(
            rect = Rect(
                left = size.width - 2 * cornerRadius,
                top = 0f,
                right = size.width,
                bottom = 2 * cornerRadius,
            ),
            startAngleDegrees = 270.0f,
            sweepAngleDegrees = 30.0f,
            forceMoveTo = false,
        )
        lineTo(x = size.width, y = 0f)
        lineTo(
            x = size.width - cornerRadius * (1 - cos(PI / 6)).toFloat(),
            y = cornerRadius * (1 - sin(PI / 6)).toFloat()
        )
        arcTo(
            rect = Rect(
                left = size.width - 2 * cornerRadius,
                top = 0f,
                right = size.width,
                bottom = 2 * cornerRadius,
            ),
            startAngleDegrees = 330.0f,
            sweepAngleDegrees = 30.0f,
            forceMoveTo = false,
        )
        lineTo(x = size.width, y = size.height - cornerRadius)

        // Bottom right arc
        arcTo(
            rect = Rect(
                left = size.width - 2 * cornerRadius,
                top = size.height - 2 * cornerRadius,
                right = size.width,
                bottom = size.height,
            ),
            startAngleDegrees = 0.0f,
            sweepAngleDegrees = 90.0f,
            forceMoveTo = false,
        )
        lineTo(x = cornerRadius, y = size.height)

        // Bottom left arc
        arcTo(
            rect = Rect(
                left = 0.0f,
                top = size.height - 2 * cornerRadius,
                right = 2 * cornerRadius,
                bottom = size.height,
            ),
            startAngleDegrees = 90.0f,
            sweepAngleDegrees = 90.0f,
            forceMoveTo = false,
        )
        lineTo(x = 0f, y = cornerRadius)

        close()
    }
}

@Preview
@Composable
fun OneMessagePreview() {
    MessageText()
}
