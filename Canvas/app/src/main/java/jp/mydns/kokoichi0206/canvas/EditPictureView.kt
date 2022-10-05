package jp.mydns.kokoichi0206.canvas

import android.util.Log
import android.view.MotionEvent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntSize
import java.io.Serializable

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EditPictureView() {
    // 指でなぞられた軌跡の履歴
    val tracks = rememberSaveable {
        mutableStateOf<List<PathHis>?>(null)
    }

    // なぞられる画像
    val srcImage = ImageBitmap.imageResource(id = R.drawable.cat)

    // 背景におく、透明レイヤー画像
    val bgImage =
        ImageBitmap.imageResource(R.drawable.transparent_img)

    val drawScope = canvasContent(tracks.value, srcImage, bgImage)

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInteropFilter {
                when (it.action) {
                    MotionEvent.ACTION_DOWN -> {
                        tracks.value = ArrayList<PathHis>().apply {
                            tracks.value?.let {
                                addAll(it)
                            }
                            add(PathHis.MoveTo(it.x, it.y))
                        }
                    }
                    MotionEvent.ACTION_MOVE, MotionEvent.ACTION_UP -> {
                        tracks.value = ArrayList<PathHis>().apply {
                            tracks.value?.let {
                                addAll(it)
                            }
                            add(PathHis.LineTo(it.x, it.y))
                        }
                    }
                    else -> {
                        Log.d("hoge", it.toString())
                    }
                }
                true
            },
        onDraw = drawScope,
    )
}

sealed class PathHis : Serializable {
    abstract val x: Float
    abstract val y: Float

    data class MoveTo(override val x: Float, override val y: Float) : PathHis()
    data class LineTo(override val x: Float = 0f, override val y: Float = 0f) : PathHis()
}

private fun canvasContent(
    tracks: List<PathHis>?,
    srcImage: ImageBitmap,
    bgImage: ImageBitmap,
): DrawScope.() -> Unit {

    Log.d("hoge", "===== tracks =====")
    Log.d("hoge", tracks.toString())

    return {

        // PathHisリスト -> Pathリストに変換
        val paths = ArrayList<Path>()
        tracks?.let {
            var path = Path()
            it.forEach { pathHis ->
                when (pathHis) {
                    is PathHis.MoveTo -> {
                        // 今までの編集から飛んだ部分に指をおいたら
                        // 今までの軌跡は終了。記録して、新しい軌跡を始める。
                        paths.add(path)
                        path = Path()
                        path.moveTo(pathHis.x, pathHis.y)
                    }
                    is PathHis.LineTo -> {
                        path.lineTo(pathHis.x, pathHis.y)
                    }
                }
            }
            // 最後の軌跡をリストイン
            paths.add(path)
        }

        val canvasWidth = this.size.width
        val canvasHeight = this.size.height

        Log.d("hoge", "===== canvas =====")
        Log.d("hoge", "canvasWidth: $canvasWidth")
        Log.d("hoge", "canvasHeight: $canvasHeight")

        val srcImageBitmapWidth = srcImage.width
        val srcImageBitmapHeight = srcImage.height

        Log.d("hoge", "===== srcImage =====")
        Log.d("hoge", "srcImageBitmapWidth: $srcImageBitmapWidth")
        Log.d("hoge", "srcImageBitmapHeight: $srcImageBitmapHeight")

        val scaleWidth = canvasWidth / srcImageBitmapWidth
        val scaleHeight = canvasHeight / srcImageBitmapHeight
        val scale = scaleHeight.coerceAtMost(scaleWidth)
        Log.d("hoge", "===== scale =====")
        Log.d("hoge", "scaleWidth: $scaleWidth")
        Log.d("hoge", "scaleHeight: $scaleHeight")
        Log.d("hoge", "scale: $scale")

        val editSrcWidth = (srcImageBitmapWidth * scale).toInt()
        val editSrcHeight = (srcImageBitmapHeight * scale).toInt()

        Log.d("hoge", "===== editSrc =====")
        Log.d("hoge", "editSrcWidth: $editSrcWidth")
        Log.d("hoge", "editSrcHeight: $editSrcHeight")

        val offset = Offset((canvasWidth - editSrcWidth) / 2, (canvasHeight - editSrcHeight) / 2)

        inset(horizontal = offset.x, vertical = offset.y) {
            drawImage(
                image = bgImage,
                dstSize = IntSize(editSrcWidth, editSrcHeight)
            )
            drawImage(
                image = srcImage,
                dstSize = IntSize(editSrcWidth, editSrcHeight)
            )

            val transStroke = 40f

            clipRect(right = editSrcWidth.toFloat(), bottom = editSrcHeight.toFloat()) {

                inset(-offset.x, -offset.y) {
                    paths.forEach { path ->
                        drawPath(
                            path = path,
                            color = Color.Red,
                            style = Stroke(width = transStroke),
                            blendMode = BlendMode.DstOut,
                        )
                    }
                }
            }

            if (tracks != null) {
                drawImage(
                    image = bgImage,
                    dstSize = IntSize(
                        editSrcWidth,
                        editSrcHeight
                    ),
                    blendMode = BlendMode.DstOver
                )
            }

            val lastTouch = tracks?.lastOrNull()
            if (lastTouch != null) {
                clipRect(right = editSrcWidth.toFloat(), bottom = editSrcHeight.toFloat()) {
                    inset(-offset.x, -offset.y) {
                        drawCircle(
                            color = Color.Red,
                            center = Offset(
                                x = lastTouch.x,
                                y = lastTouch.y
                            ),
                            radius = transStroke / 2,
                            style = Stroke(width = transStroke / 4)
                        )
                    }
                }
            }
        }
    }
}
