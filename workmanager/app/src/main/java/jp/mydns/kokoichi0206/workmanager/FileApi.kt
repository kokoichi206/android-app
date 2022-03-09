package jp.mydns.kokoichi0206.workmanager

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET

interface FileApi {

    @GET("/imgs/example/0.png")
    suspend fun downloadImage(): Response<ResponseBody>

    companion object {
        val instance by lazy {
            Retrofit.Builder()
                .baseUrl("https://kokoichi0206.mydns.jp/")
                .build()
                .create(FileApi::class.java)
        }
    }
}