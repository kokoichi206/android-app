package jp.mydns.kokoichi0206.jetpackglance

import androidx.compose.material.Text
import androidx.compose.runtime.Composable

class GreetingsWidget(private val name: String): GlanceAppWidget() {

    @Composable
    override fun Content() {

        Text(text = "Hello $name")

    }

}