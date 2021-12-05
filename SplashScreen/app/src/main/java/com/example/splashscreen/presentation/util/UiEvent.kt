package com.example.splashscreen.presentation.util

import com.example.splashscreen.util.UiText


sealed class UiEvent {
    data class SnackbarEvent(val uiText: UiText): UiEvent()
    data class Navigate(val route: String): UiEvent()
}
