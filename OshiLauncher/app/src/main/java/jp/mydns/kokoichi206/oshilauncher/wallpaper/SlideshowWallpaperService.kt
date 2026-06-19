package jp.mydns.kokoichi206.oshilauncher.wallpaper

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import jp.mydns.kokoichi206.oshilauncher.data.CustomizationStore
import java.io.File
import kotlin.math.max

private const val FRAME_MS = 16L
private const val TRANSITION_MS = 800f

/**
 * 推し画像のスライドショー・ライブ壁紙。ホームにもロック画面にもシステムが描画するため、
 * 「ロック画面で時間ごとに少し動く」体験はこのサービスで実現する（アプリは OS ロック画面に
 * 直接描けないため、これが正規ルート）。
 *
 * 電池対策として可視中のみ描画し、静止保持中はフレームを間引く（次の切替時刻まで待つ）。
 * 画像セット・切替秒数は launcher 本体と同じ [CustomizationStore] を共有する。
 */
class SlideshowWallpaperService : WallpaperService() {

    override fun onCreateEngine(): Engine = SlideshowEngine()

    private inner class SlideshowEngine : Engine() {
        private val store = CustomizationStore(applicationContext)
        private val handler = Handler(Looper.getMainLooper())
        private val paint = Paint(Paint.FILTER_BITMAP_FLAG)

        private var visible = false
        private var width = 0
        private var height = 0

        private var files: List<File> = emptyList()
        private var holdMs = 3000L
        private var index = 0
        private var currentBitmap: Bitmap? = null
        private var nextBitmap: Bitmap? = null
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
            decodeCurrent()
            drawFrame()
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            handler.removeCallbacks(frame)
        }

        override fun onDestroy() {
            handler.removeCallbacks(frame)
            currentBitmap?.recycle()
            nextBitmap?.recycle()
        }

        private fun reload() {
            files = store.wallpaperFiles()
            holdMs = store.intervalSeconds() * 1000L
            transitioning = false
            if (index !in files.indices) index = 0
            decodeCurrent()
        }

        private fun decodeCurrent() {
            if (width <= 0 || files.isEmpty()) return
            currentBitmap = decodeCover(files[index.coerceIn(0, files.lastIndex)])
        }

        private fun tick() {
            if (!visible) return
            drawFrame()
            val delay = when {
                transitioning -> FRAME_MS
                files.size > 1 -> {
                    val remaining = holdMs - (SystemClock.uptimeMillis() - lastSwitch)
                    remaining.coerceIn(FRAME_MS, holdMs)
                }
                else -> -1L // 単一/未設定は再描画不要
            }
            if (delay > 0) {
                handler.removeCallbacks(frame)
                handler.postDelayed(frame, delay)
            }
        }

        private fun drawFrame() {
            val holder = surfaceHolder
            val canvas: Canvas = holder.lockCanvas() ?: return
            try {
                canvas.drawColor(Color.BLACK)
                if (files.isEmpty()) return

                val now = SystemClock.uptimeMillis()
                if (!transitioning && files.size > 1 && now - lastSwitch >= holdMs) {
                    startTransition(now)
                }

                if (transitioning) {
                    val t = ((now - transitionStart) / TRANSITION_MS).coerceIn(0f, 1f)
                    val e = easeInOut(t)
                    currentBitmap?.let { drawCover(canvas, it, -width * 0.18f * e, 1f - e) }
                    nextBitmap?.let { drawCover(canvas, it, width * (1f - e), e) }
                    if (t >= 1f) finalizeTransition(now)
                } else {
                    currentBitmap?.let { drawCover(canvas, it, 0f, 1f) }
                }
            } finally {
                holder.unlockCanvasAndPost(canvas)
            }
        }

        private fun startTransition(now: Long) {
            val nextIndex = (index + 1) % files.size
            nextBitmap = decodeCover(files[nextIndex])
            transitionStart = now
            transitioning = true
        }

        private fun finalizeTransition(now: Long) {
            currentBitmap?.recycle()
            index = (index + 1) % files.size
            currentBitmap = nextBitmap
            nextBitmap = null
            transitioning = false
            lastSwitch = now
        }

        /** サーフェスを覆うように中央クロップで描画。dx で水平オフセット、alpha で不透明度。 */
        private fun drawCover(canvas: Canvas, bmp: Bitmap, dx: Float, alpha: Float) {
            if (bmp.width <= 0 || bmp.height <= 0) return
            val scale = max(width.toFloat() / bmp.width, height.toFloat() / bmp.height)
            val dw = bmp.width * scale
            val dh = bmp.height * scale
            val left = (width - dw) / 2f + dx
            val top = (height - dh) / 2f
            paint.alpha = (alpha.coerceIn(0f, 1f) * 255).toInt()
            canvas.drawBitmap(bmp, null, RectF(left, top, left + dw, top + dh), paint)
        }

        private fun decodeCover(file: File): Bitmap? {
            if (!file.exists() || width <= 0) return null
            val reqMax = max(width, height)
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
