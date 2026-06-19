package jp.mydns.kokoichi206.oshicompanion

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * companion 方式の検証用 AppWidget。
 * 配置・移動・リサイズは標準ランチャーが行い、ここは中身（RemoteViews）を供給するだけ。
 * これが WidgetClub のウィジェットと同じ仕組み。
 */
class OshiWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        val time = SimpleDateFormat("H:mm", Locale.JAPAN).format(Date())
        val openApp = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE,
        )
        appWidgetIds.forEach { id ->
            val views = RemoteViews(context.packageName, R.layout.oshi_widget).apply {
                setTextViewText(R.id.widget_label, "推し")
                setTextViewText(R.id.widget_time, time)
                setOnClickPendingIntent(R.id.widget_label, openApp)
            }
            appWidgetManager.updateAppWidget(id, views)
        }
    }
}
