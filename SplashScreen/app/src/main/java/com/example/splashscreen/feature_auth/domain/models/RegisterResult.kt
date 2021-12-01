package com.example.splashscreen.feature_auth.domain.models

import com.example.splashscreen.util.SimpleResource

data class RegisterResult(
    val emailError: AuthError? = null,
    val usernameEError: AuthError? = null,
    val passwordError: AuthError? = null,
    val result: SimpleResource? = null,
)
