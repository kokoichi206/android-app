package com.example.splashscreen.feature_auth.domain.models

import com.example.splashscreen.presentation.register.RegisterState

sealed class AuthError : Error() {
    object FieldEmpty : AuthError()
    object InputTooShort : AuthError()
    object InvalidEmail : AuthError()
    object InvalidPassword : AuthError()
}
