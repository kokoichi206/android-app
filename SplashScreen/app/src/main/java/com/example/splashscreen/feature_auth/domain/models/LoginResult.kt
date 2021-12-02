package com.example.splashscreen.feature_auth.domain.models

import com.example.splashscreen.util.SimpleResource

data class LoginResult(
    val emailError: AuthError? = null,
    val passwordError: AuthError? = null,
    val result: SimpleResource? = null,
)
