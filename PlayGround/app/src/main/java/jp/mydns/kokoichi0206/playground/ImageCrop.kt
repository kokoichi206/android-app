package jp.mydns.kokoichi0206.playground

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.drawToBitmap
import coil.compose.rememberAsyncImagePainter
import java.io.ByteArrayOutputStream

@Composable
fun ImageCrop() {
    var saved by remember {
        mutableStateOf(false)
    }
    var bitmap by remember {
        mutableStateOf(Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888))
    }

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
        if (scale < 1) {
            scale = 1F
        }
    }
    Log.d("hoge", "scale: $scale")

    if (saved) {
        ImageCropped(bitmap = bitmap) {
            saved = !saved
        }
    } else {
        val snapShot = CaptureBitmap {
            ImageCropDoing(
                translateX = translateX,
                translateY = translateY,
                scale = scale,
                state = state,
                onDetectTransformGestures = { pan, zoom ->
                    scale = when {
                        scale < 0.5f -> 0.5f
                        scale > 3f -> 3f
                        else -> scale * zoom
                    }
                    translateX += pan.x
                    translateY += pan.y
                },
                onSaveButtonClicked = {
                    saved = !saved
                }
            )
        }

        val context = LocalContext.current
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopEnd
        ) {
            Button(onClick = {
                saved = !saved
                if (saved) {
                    bitmap = snapShot.invoke()

                    val bytes = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)

                    val fos = context.openFileOutput("cropped_image_test.jpg", Context.MODE_PRIVATE)
                    fos.write(bytes.toByteArray())
                }
            }) {
                Text(text = "Save")
            }
        }
    }
}

@Composable
fun ImageCropped(
    bitmap: Bitmap,
    onSaveButtonClicked: () -> Unit,
) {

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.TopEnd,
        ) {
            Button(onClick = {
                onSaveButtonClicked()
            }) {
                Text(text = "Back")
            }
        }

        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "",
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape)
                .scale(5f / 4)  // 最初円を描画するときに、1/5 だけスペースを開けているため！
                .border(0.dp, Color.Transparent, CircleShape),
            contentScale = ContentScale.Crop,
        )

        LocalContext.current.getFileStreamPath("cropped_image_test.jpg")?.let { file ->
            Log.d("hoge", "file.isFile: ${file.isFile}")
            Log.d("hoge", "file.freeSpace: ${file.freeSpace}")

            Image(
                painter = rememberAsyncImagePainter(Uri.fromFile(file)),
                contentDescription = "",
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
                    .scale(5f / 4)  // 最初円を描画するときに、1/5 だけスペースを開けているため！
                    .border(0.dp, Color.Transparent, CircleShape),
                contentScale = ContentScale.Crop,
            )
        }
    }
}

@Composable
fun ImageCropDoing(
    translateX: Float,
    translateY: Float,
    scale: Float,
    state: TransformableState,
    onDetectTransformGestures: (pan: Offset, zoom: Float) -> Unit,
    onSaveButtonClicked: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTransformGestures { centroid, pan, zoom, rotation ->
                    onDetectTransformGestures(pan, zoom)
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

@Composable
fun CaptureBitmap(
    content: @Composable () -> Unit,
): () -> Bitmap {

    val context = LocalContext.current

    /**
     * ComposeView that would take composable as its content
     * Kept in remember so recomposition doesn't re-initialize it
     **/
    val composeView = remember { ComposeView(context) }

    /**
     * Callback function which could get latest image bitmap
     **/
    fun captureBitmap(): Bitmap {
        return composeView.drawToBitmap()
    }

    /** Use Native View inside Composable **/
    AndroidView(
        factory = {
            composeView.apply {
                setContent {
                    content.invoke()
                }
            }
        }
    )

    /** returning callback to bitmap **/
    return ::captureBitmap
}
