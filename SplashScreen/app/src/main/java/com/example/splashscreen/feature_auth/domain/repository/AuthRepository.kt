package com.example.splashscreen.feature_auth.domain.repository

import com.example.splashscreen.feature_auth.data.dto.request.CreateAccountRequest
import com.example.splashscreen.feature_auth.data.dto.response.BasicApiResponse
import com.example.splashscreen.util.Resource
import com.example.splashscreen.util.SimpleResource

interface AuthRepository {

    suspend fun register(
        email: String,
        username: String,
        password: String
    ): SimpleResource
}