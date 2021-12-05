package com.example.splashscreen.feature_auth.domain.usecase

import com.example.splashscreen.feature_auth.domain.repository.AuthRepository
import com.example.splashscreen.util.SimpleResource

class AuthenticateUseCase(
    private val repository: AuthRepository
) {

    suspend operator fun invoke(): SimpleResource {
        return repository.authenticate()
    }
}