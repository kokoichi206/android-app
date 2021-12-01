package com.example.splashscreen.domain.util

import android.util.Patterns
import com.example.splashscreen.feature_auth.domain.models.AuthError
import com.example.splashscreen.util.Constants

object ValidationUtil {

    fun validateEmail(email: String): AuthError? {
        val trimmedEmail = email.trim()

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return AuthError.InvalidEmail
        }
        if (trimmedEmail.isBlank()) {
            return AuthError.FieldEmpty
        }
        return null
    }

    fun validateUsername(username: String): AuthError? {
        val trimmedUsername = username.trim()

        if (trimmedUsername.length < Constants.MIN_USERNAME_LENGTH) {
            return AuthError.InputTooShort
        }
        if (trimmedUsername.isBlank()) {
            return AuthError.FieldEmpty
        }
        return null
    }

    fun validatePassword(password: String): AuthError? {
        val capitalLettersInPassword = password.any { it.isUpperCase() }
        val numberInPassword = password.any { it.isDigit() }
        if (!capitalLettersInPassword || !numberInPassword) {
            return AuthError.InvalidPassword
        }
        if (password.length < Constants.MIN_PASSWORD_LENGTH) {
            return AuthError.InputTooShort
        }
        if (password.isBlank()) {
            return AuthError.FieldEmpty
        }
        return null
    }
}