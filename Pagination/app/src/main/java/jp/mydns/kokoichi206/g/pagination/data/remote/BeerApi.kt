package jp.mydns.kokoichi206.g.pagination.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface BeerApi {

    companion object {
        const val BASE_URL = "https://api.punkapi.com/v2/"
    }

    @GET("beers")
    suspend fun getBeers(
        @Query("page") page: Int,
        @Query("per_page") pageCount: Int,
    ): List<BeerDto>
}
