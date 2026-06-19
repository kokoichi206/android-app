package jp.mydns.kokoichi206.oshicompanion

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = darkColorScheme()) {
                PocScreen()
            }
        }
    }
}

@Composable
private fun PocScreen() {
    val context = LocalContext.current
    Column(
        Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("companion 方式 PoC", style = MaterialTheme.typography.titleLarge)
        Text(
            "このアプリはランチャーではありません。標準ランチャーに『自前ウィジェット』と" +
                "『カスタムアイコンのショートカット』を載せられるか検証します。",
            style = MaterialTheme.typography.bodyMedium,
        )

        Button(onClick = { requestPinWidget(context) }, modifier = Modifier) {
            Text("ウィジェットをホームに追加")
        }
        Text(
            "※ ランチャーのウィジェット一覧から『Oshi Companion』を選んでも置けます（配置・移動は標準どおり）。",
            style = MaterialTheme.typography.bodySmall,
        )

        Button(onClick = { requestPinIconShortcut(context) }) {
            Text("推しアイコン（ショートカット）を追加")
        }
        Text(
            "※ カスタム画像のショートカットをホームに作成。これが WidgetClub のアイコン変更と同方式。",
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

private fun requestPinWidget(context: Context) {
    val awm = AppWidgetManager.getInstance(context)
    val provider = ComponentName(context, OshiWidgetProvider::class.java)
    if (awm.isRequestPinAppWidgetSupported) {
        awm.requestPinAppWidget(provider, null, null)
    } else {
        Toast.makeText(context, "このランチャーはピン留め非対応。ウィジェット一覧から追加してください", Toast.LENGTH_LONG).show()
    }
}

private fun requestPinIconShortcut(context: Context) {
    if (!ShortcutManagerCompat.isRequestPinShortcutSupported(context)) {
        Toast.makeText(context, "このランチャーはショートカットのピン留め非対応", Toast.LENGTH_LONG).show()
        return
    }
    val intent = Intent(context, MainActivity::class.java).apply { action = Intent.ACTION_VIEW }
    val shortcut = ShortcutInfoCompat.Builder(context, "oshi-poc-shortcut")
        .setShortLabel("推しアプリ")
        .setIcon(IconCompat.createWithBitmap(makeIconBitmap()))
        .setIntent(intent)
        .build()
    ShortcutManagerCompat.requestPinShortcut(context, shortcut, null)
}

/** カスタムアイコン画像を実行時生成（推し色の角丸＋★）。 */
private fun makeIconBitmap(): Bitmap {
    val size = 192
    val bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bmp)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    paint.color = 0xFFE48FB1.toInt()
    canvas.drawRoundRect(RectF(0f, 0f, size.toFloat(), size.toFloat()), 44f, 44f, paint)
    paint.color = Color.WHITE
    paint.textSize = 110f
    paint.textAlign = Paint.Align.CENTER
    canvas.drawText("★", size / 2f, size / 2f + 40f, paint)
    return bmp
}
