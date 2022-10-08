package jp.mydns.kokoichi0206

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalTextApi::class)
@Composable
fun Kaigi() {

    Column {
        SelectionContainer {
            // SelectionContainer 使うと Column の効果が消える？
            Column {
                Text(
                    text = "Hi, This is a very long string.",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = TextStyle(
                        fontSize = 44.sp,
                    ),
                )

                Text(
                    text = "This is a second string which can be selected.",
                    style = TextStyle(
                        fontSize = 24.sp,
                    ),
                )

                // Lower API
                BasicText(
                    text = "Base Compose of the Text",
                    style = TextStyle(
                        fontSize = 24.sp,
                        color = Color.Green,
                    ),
                )

                Text(
                    text = "Hello \nWorld!!",
                    style = TextStyle(
                        lineHeight = 0.5.em,
                    ),
                )

                Text(
                    text = "Hello World!!",
                    style = TextStyle(
                        lineHeight = 0.5.em,
                        lineHeightStyle = LineHeightStyle(
                            trim = LineHeightStyle.Trim.FirstLineTop,
//                        trim = LineHeightStyle.Trim.FirstLineTop,
                            alignment = LineHeightStyle.Alignment.Bottom,
                        )
                    ),
                )
            }
        }

        Text(
            text = "ここをタップして今すぐダウンロードしてください。この部分はセレクトできません。",
            style = TextStyle(
                // Simple は TextField とか。
                // このフィルタが Android 13 以上。
                lineBreak = LineBreak.Paragraph,
//                fontFamily = FontFamily(
//                    Font(
//                       googleFont = GoogleFont("Yusei Magic"),
//                       fontProvider = provider,
//                    )
//                )
            )
        )

        Text(
            text = "ここをタップして今すぐダウンロードしてください。この部分はセレクトできません。",
            style = TextStyle(
                // Simple は TextField とか。
                // このフィルタが Android 13 以上。
                lineBreak = LineBreak.Paragraph,
            )
        )
    }
}