package com.example.splashscreen.domain.states

import com.example.splashscreen.feature_auth.domain.models.AuthError

data class PasswordTextFieldState(
    val text: String = "",
    val error: AuthError? = null,
    val isPasswordVisible: Boolean = false,
)
