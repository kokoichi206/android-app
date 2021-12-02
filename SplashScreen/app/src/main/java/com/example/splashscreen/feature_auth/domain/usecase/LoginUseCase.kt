package com.example.splashscreen.feature_auth.domain.usecase

import com.example.splashscreen.domain.util.ValidationUtil
import com.example.splashscreen.feature_auth.domain.models.AuthError
import com.example.splashscreen.feature_auth.domain.models.LoginResult
import com.example.splashscreen.feature_auth.domain.repository.AuthRepository

class LoginUseCase(
    private val repository: AuthRepository,
) {

    suspend operator fun invoke(email: String, password: String): LoginResult {
        val emailError = if (email.isBlank()) AuthError.FieldEmpty else null
        val passwordError = if (password.isBlank()) AuthError.FieldEmpty else null

        if (emailError != null || password != null) {
            return LoginResult(emailError, passwordError)
        }

        return LoginResult(
            result = repository.login(email, password)
        )
    }
}