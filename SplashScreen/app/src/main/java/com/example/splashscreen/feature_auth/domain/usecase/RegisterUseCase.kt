package com.example.splashscreen.feature_auth.domain.usecase

import android.util.Patterns
import com.example.splashscreen.domain.util.ValidationUtil
import com.example.splashscreen.feature_auth.domain.models.AuthError
import com.example.splashscreen.feature_auth.domain.models.RegisterResult
import com.example.splashscreen.feature_auth.domain.repository.AuthRepository
import com.example.splashscreen.util.Constants
import com.example.splashscreen.util.SimpleResource

class RegisterUseCase(
    private val repository: AuthRepository
) {

    suspend operator fun invoke(
        email: String,
        username: String,
        password: String
    ): RegisterResult {
        val emailError = ValidationUtil.validateEmail(email)
        val usernameError = ValidationUtil.validateUsername(username)
        val passwordError = ValidationUtil.validatePassword(password)

        // return result before network request
        if(emailError != null || usernameError != null || passwordError != null) {
            return RegisterResult(
                emailError = emailError,
                usernameEError = usernameError,
                passwordError = passwordError,
            )
        }

        val result = repository.register(email.trim(), username.trim(), password.trim())

        return RegisterResult(
            result = result,
        )
    }
}