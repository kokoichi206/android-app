package jp.mydns.kokoichi0206.playground

import android.content.res.Resources
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ImageCrop() {

    var scale by remember {
        mutableStateOf(1f)
    }
    var translateX by remember {
        mutableStateOf(0f)
    }
    var translateY by remember {
        mutableStateOf(0f)
    }
    val state = rememberTransformableState { zoomChange, _, _ ->
        scale *= zoomChange
        if(scale < 1){
            scale = 1F
        }
    }
    Log.d("hoge", "scale: $scale")
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTransformGestures { centroid, pan, zoom, rotation ->
                    scale = when {
                        scale < 0.5f -> 0.5f
                        scale > 3f -> 3f
                        else -> scale * zoom
                    }
                    translateX += pan.x
                    translateY += pan.y
                    Log.d("hoge", "translateX: $translateX")
                    Log.d("hoge", "translateY: $translateY")
                    Log.d("hoge", "centroid: $centroid")
                    Log.d("hoge", "change: $pan")
                    Log.d("hoge", "rotation: $rotation")
                }
            },
    ) {
        Image(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale
                )
//                .pointerInput(Unit) {
//                    detectDragGestures { change, dragAmount ->
//                        translateX += dragAmount.x * scale
//                        translateY += dragAmount.y * scale
//                        Log.d("hoge", "translateX: $translateX")
//                        Log.d("hoge", "translateY: $translateY")
//                        Log.d("hoge", "change: $change")
//                    }
//                }
                .offset(
                    x = translateX.toDp(),
                    y = translateY.toDp(),
                )
                .transformable(state = state),
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

fun Float.toDp(): Dp {
    return (this / Resources.getSystem().displayMetrics.density).dp
}
