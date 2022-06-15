package jp.mydns.kokoichi0206.coroutines

import retrofit2.Response
import retrofit2.http.GET

interface MeAPI {

    @GET("me")
    suspend fun getMyInfo(): Response<Me>
}

data class Me(
    val name: String,
    val age: Int,
)
