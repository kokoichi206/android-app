package jp.mydns.kokoichi0206.jetpackglance

import androidx.glance.appwidget.GlanceAppWidgetReceiver

class GreetingsWidgetReceiver : GlanceAppWidgetReceiver() {

    override val glanceAppWidget = GreetingsWidget("Glance")

}
