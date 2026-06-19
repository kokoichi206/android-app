package jp.mydns.kokoichi206.oshilauncher.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.ConcurrentHashMap

/**
 * 取り込み画像（壁紙・カスタムアイコン）の Bitmap デコードキャッシュ。
 * 壁紙(大)とアイコン(小)で必要解像度が違うため reqMaxPx をキーに含め、
 * lastModified もキーに含めることで同名差し替え時に自動失効させる。
 */
object ImageFiles {
    private val cache = ConcurrentHashMap<String, ImageBitmap>()

    fun peek(file: File, reqMaxPx: Int): ImageBitmap? = cache[key(file, reqMaxPx)]

    suspend fun load(file: File, reqMaxPx: Int): ImageBitmap? {
        val k = key(file, reqMaxPx)
        cache[k]?.let { return it }
        return withContext(Dispatchers.IO) { decodeDownsampled(file, reqMaxPx)?.asImageBitmap() }
            ?.also { cache[k] = it }
    }

    private fun key(file: File, reqMaxPx: Int) = "${file.absolutePath}@${file.lastModified()}#$reqMaxPx"

    private fun decodeDownsampled(file: File, reqMaxPx: Int): Bitmap? {
        if (!file.exists()) return null
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(file.absolutePath, bounds)
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null

        var sample = 1
        val longest = maxOf(bounds.outWidth, bounds.outHeight)
        while (longest / (sample * 2) >= reqMaxPx) sample *= 2

        val opts = BitmapFactory.Options().apply { inSampleSize = sample }
        return BitmapFactory.decodeFile(file.absolutePath, opts)
    }
}
