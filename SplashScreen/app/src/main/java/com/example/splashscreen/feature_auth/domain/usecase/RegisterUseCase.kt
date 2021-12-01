package com.example.splashscreen.feature_auth.domain.usecase

import com.example.splashscreen.feature_auth.domain.repository.AuthRepository
import com.example.splashscreen.util.SimpleResource

class RegisterUseCase(
    private val repository: AuthRepository
) {

    suspend operator fun invoke(
        email: String,
        username: String,
        password: String
    ): SimpleResource {
        return repository.register(email.trim(), username.trim(), password.trim())
    }
}