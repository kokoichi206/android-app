package com.example.splashscreen.presentation.register

import com.example.splashscreen.util.UiText

data class RegisterState(
    val successful: Boolean? = null,
    val message: UiText? = null,
    val isLoading: Boolean = false,
)
