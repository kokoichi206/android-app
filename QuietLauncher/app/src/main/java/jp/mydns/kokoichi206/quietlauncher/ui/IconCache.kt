package jp.mydns.kokoichi206.quietlauncher.ui

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import jp.mydns.kokoichi206.quietlauncher.data.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap

/**
 * アイコンの同期ロード（PackageManager 取得 + Bitmap 変換）をメインスレッドで行うと
 * ドロワー展開アニメーションの最初のフレームに乗ってカクつくため、
 * IO スレッドで一度だけ読み、以後はメモリから返す。
 */
object IconCache {
    private val cache = ConcurrentHashMap<String, ImageBitmap>()

    fun peek(packageName: String): ImageBitmap? = cache[packageName]

    suspend fun load(packageName: String, repo: AppRepository): ImageBitmap? =
        cache[packageName] ?: withContext(Dispatchers.IO) {
            try {
                repo.icon(packageName)?.toBitmap(96, 96)?.asImageBitmap()
            } catch (e: Exception) {
                null
            }
        }?.also { cache[packageName] = it }
}
