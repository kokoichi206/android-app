package com.example.splashscreen.feature_auth.data.repository

import androidx.compose.ui.graphics.vector.RenderVectorGroup
import com.example.splashscreen.R
import com.example.splashscreen.feature_auth.data.dto.request.CreateAccountRequest
import com.example.splashscreen.feature_auth.data.remote.AuthApi
import com.example.splashscreen.feature_auth.domain.repository.AuthRepository
import com.example.splashscreen.util.Resource
import com.example.splashscreen.util.SimpleResource
import com.example.splashscreen.util.UiText
import retrofit2.HttpException
import java.io.IOException

class AuthRepositoryImpl(
    private val api: AuthApi
): AuthRepository {

    override suspend fun register(
        email: String,
        username: String,
        password: String
    ): SimpleResource {
        val request = CreateAccountRequest(email, username, password)
        return try {
            val response = api.register(request)
            if (response.successful) {
                Resource.Success(Unit)
            } else {
                response.message?.let { msg ->
                    Resource.Error(UiText.DynamicString(msg))
                } ?: Resource.Error(UiText.StringResource(R.string.error_unknown))

            }
        } catch(e: IOException) {
            Resource.Error(
                uiText = UiText.StringResource(R.string.error_coulnt_reach_server)
            )
        }catch(e: HttpException) {
            Resource.Error(
                uiText = UiText.StringResource(R.string.oopt_something_went_wrong)
            )
        }
    }
}