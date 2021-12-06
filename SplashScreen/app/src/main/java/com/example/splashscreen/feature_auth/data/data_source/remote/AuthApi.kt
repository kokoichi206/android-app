package com.example.splashscreen.feature_auth.data.data_source.remote

import com.example.splashscreen.feature_auth.data.dto.request.CreateAccountRequest
import com.example.splashscreen.feature_auth.data.dto.request.LoginRequest
import com.example.splashscreen.feature_auth.data.dto.response.AuthResponse
import com.example.splashscreen.feature_auth.data.dto.response.BasicApiResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApi {

    @POST("/api/user/create")
    suspend fun register(
        @Body request: CreateAccountRequest
    ): BasicApiResponse<Unit>

    @POST("/api/user/login")
    suspend fun login(
        @Body request: LoginRequest
    ): BasicApiResponse<AuthResponse>

    @GET("/api/user/authenticate")
    suspend fun authenticate()
//    suspend fun authenticate(): Response<Unit>

    companion object {
        const val BASE_URL = "http://10.0.2.2:8001/"
    }
}