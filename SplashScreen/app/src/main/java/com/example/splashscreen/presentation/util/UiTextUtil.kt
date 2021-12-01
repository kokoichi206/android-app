package com.example.splashscreen.presentation.util

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.splashscreen.R
import com.example.splashscreen.util.UiText

//@Composable
//fun uiText(uiText: UiText): String {
//    return when (uiText) {
//        is UiText.DynamicString -> uiText.value
//        is UiText.StringResource -> stringResource(id = uiText.id)
//    }
//}

@Composable
fun UiText.asString(): String {
    return when (this) {
        is UiText.DynamicString -> this.value
        is UiText.StringResource -> stringResource(id = this.id)
    }
}

fun UiText.asString(context: Context): String {
    return when (this) {
        is UiText.DynamicString -> this.value
        is UiText.StringResource -> context.getString(this.id)
    }
}
