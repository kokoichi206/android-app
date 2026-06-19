package jp.mydns.kokoichi206.oshilauncher.wallpaper

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.SystemClock
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import jp.mydns.kokoichi206.oshilauncher.data.CustomizationStore
import jp.mydns.kokoichi206.oshilauncher.data.WallpaperTransition
import java.io.File
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

private const val FRAME_MS = 16L

/**
 * 推し画像のスライドショー・ライブ壁紙。ホームにもロック画面にもシステムが描画する。
 *
 * もたつき対策:
 * - 画像デコードは専用スレッドで行い、描画スレッドを止めない。
 * - 次画像は保持中に先読みしておき、切替の瞬間に同期デコードしない。
 * - [SurfaceHolder.lockHardwareCanvas] で GPU 描画する。
 * 電池対策として可視中のみ描画する（Ken Burns は常時微動のため毎フレーム、他方式は次切替まで間引く）。
 */
class SlideshowWallpaperService : WallpaperService() {

    override fun onCreateEngine(): Engine = SlideshowEngine()

    private inner class SlideshowEngine : Engine() {
        private val store = CustomizationStore(applicationContext)
        private val handler = Handler(Looper.getMainLooper())
        private val decodeThread = HandlerThread("oshi-wallpaper-decode").apply { start() }
        private val bg = Handler(decodeThread.looper)
        private val paint = Paint(Paint.FILTER_BITMAP_FLAG)

        private var visible = false
        private var width = 0
        private var height = 0

        private var files: List<File> = emptyList()
        private var style = WallpaperTransition.DEFAULT
        private var holdMs = 3000L
        private var transitionMs = 1300f
        private var index = 0
        private var currentBitmap: Bitmap? = null
        private var nextBitmap: Bitmap? = null // 先読み済みの次画像
        private var transitioning = false
        private var transitionStart = 0L
        private var lastSwitch = 0L

        private val frame = Runnable { tick() }

        override fun onVisibilityChanged(v: Boolean) {
            visible = v
            if (v) {
                reload()
                lastSwitch = SystemClock.uptimeMillis()
                tick()
            } else {
                handler.removeCallbacks(frame)
            }
        }

        override fun onSurfaceChanged(holder: SurfaceHolder, format: Int, w: Int, h: Int) {
            width = w
            height = h
            loadCurrent()
            preloadNext()
            drawFrame()
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            handler.removeCallbacks(frame)
        }

        override fun onDestroy() {
            handler.removeCallbacks(frame)
            decodeThread.quitSafely()
            currentBitmap?.recycle()
            nextBitmap?.recycle()
        }

        private fun reload() {
            files = store.wallpaperFiles()
            holdMs = store.intervalSeconds() * 1000L
            style = store.transition()
            transitionMs = style.durationMs.toFloat()
            transitioning = false
            if (index !in files.indices) index = 0
            loadCurrent()
            preloadNext()
        }

        /** 専用スレッドでデコードし、結果はメインで受けて描画。描画スレッドは止めない。 */
        private fun decodeAsync(file: File, assign: (Bitmap?) -> Unit) {
            val w = width
            bg.post {
                val bmp = decodeCover(file, w, height)
                handler.post {
                    assign(bmp)
                    if (visible) drawFrame()
                }
            }
        }

        private fun loadCurrent() {
            if (files.isEmpty() || width <= 0) return
            val i = index.coerceIn(0, files.lastIndex)
            decodeAsync(files[i]) { bmp -> currentBitmap?.recycle(); currentBitmap = bmp }
        }

        private fun preloadNext() {
            if (files.size < 2 || width <= 0) { nextBitmap = null; return }
            val ni = (index + 1) % files.size
            decodeAsync(files[ni]) { bmp -> nextBitmap?.recycle(); nextBitmap = bmp }
        }

        private fun tick() {
            if (!visible) return
            val now = SystemClock.uptimeMillis()
            if (transitioning) {
                if ((now - transitionStart) / transitionMs >= 1f) finalizeTransition(now)
            } else if (files.size > 1 && nextBitmap != null && now - lastSwitch >= holdMs) {
                transitionStart = now
                transitioning = true
            }
            drawFrame()

            val delay = when {
                transitioning -> FRAME_MS
                style == WallpaperTransition.KEN_BURNS && files.isNotEmpty() -> FRAME_MS
                files.size > 1 ->
                    if (nextBitmap != null) (holdMs - (now - lastSwitch)).coerceIn(FRAME_MS, holdMs) else FRAME_MS
                else -> -1L
            }
            if (delay > 0) {
                handler.removeCallbacks(frame)
                handler.postDelayed(frame, delay)
            }
        }

        private fun drawFrame() {
            val holder = surfaceHolder
            val canvas: Canvas = holder.lockHardwareCanvas() ?: return
            try {
                canvas.drawColor(Color.BLACK)
                if (files.isEmpty()) return
                val now = SystemClock.uptimeMillis()
                if (transitioning) {
                    val e = easeInOut(((now - transitionStart) / transitionMs).coerceIn(0f, 1f))
                    drawTransition(canvas, e, now)
                } else if (style == WallpaperTransition.KEN_BURNS) {
                    drawScene(canvas, currentBitmap, ambientScale(now), ambientDx(now), 1f)
                } else {
                    drawScene(canvas, currentBitmap, 1f, 0f, 1f)
                }
            } finally {
                holder.unlockCanvasAndPost(canvas)
            }
        }

        private fun drawTransition(canvas: Canvas, e: Float, now: Long) {
            when (style) {
                WallpaperTransition.CROSSFADE -> {
                    drawScene(canvas, currentBitmap, 1f, 0f, 1f - e)
                    drawScene(canvas, nextBitmap, 1f, 0f, e)
                }
                WallpaperTransition.KEN_BURNS -> {
                    drawScene(canvas, currentBitmap, ambientScale(now), ambientDx(now), 1f - e)
                    drawScene(canvas, nextBitmap, 1.05f, 0f, e)
                }
                WallpaperTransition.ZOOM -> {
                    drawScene(canvas, currentBitmap, 1f + 0.05f * e, 0f, 1f - e)
                    drawScene(canvas, nextBitmap, 1.18f - 0.18f * e, 0f, e)
                }
                WallpaperTransition.PUSH -> {
                    drawScene(canvas, currentBitmap, 1f, -width * e, 1f)
                    drawScene(canvas, nextBitmap, 1f, width * (1f - e), 1f)
                }
            }
        }

        private fun finalizeTransition(now: Long) {
            currentBitmap?.recycle()
            index = (index + 1) % files.size
            currentBitmap = nextBitmap
            nextBitmap = null
            transitioning = false
            lastSwitch = now
            preloadNext()
        }

        /** サーフェスを覆うように中央クロップ。scale で拡大率、dx で水平オフセット、alpha で不透明度。 */
        private fun drawScene(canvas: Canvas, bmp: Bitmap?, scale: Float, dx: Float, alpha: Float) {
            if (bmp == null || bmp.width <= 0 || bmp.height <= 0) return
            val base = max(width.toFloat() / bmp.width, height.toFloat() / bmp.height) * scale
            val dw = bmp.width * base
            val dh = bmp.height * base
            val left = (width - dw) / 2f + dx
            val top = (height - dh) / 2f
            paint.alpha = (alpha.coerceIn(0f, 1f) * 255).toInt()
            canvas.drawBitmap(bmp, null, RectF(left, top, left + dw, top + dh), paint)
        }

        private fun ambientScale(now: Long): Float =
            1.06f + 0.06f * (0.5f - 0.5f * cos(now / 4000.0)).toFloat()

        private fun ambientDx(now: Long): Float = width * 0.02f * sin(now / 5200.0).toFloat()

        private fun decodeCover(file: File, w: Int, h: Int): Bitmap? {
            if (!file.exists() || w <= 0) return null
            val reqMax = max(w, h)
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeFile(file.absolutePath, bounds)
            if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null
            var sample = 1
            val longest = max(bounds.outWidth, bounds.outHeight)
            while (longest / (sample * 2) >= reqMax) sample *= 2
            val opts = BitmapFactory.Options().apply { inSampleSize = sample }
            return BitmapFactory.decodeFile(file.absolutePath, opts)
        }

        private fun easeInOut(t: Float): Float = t * t * (3f - 2f * t)
    }
}
