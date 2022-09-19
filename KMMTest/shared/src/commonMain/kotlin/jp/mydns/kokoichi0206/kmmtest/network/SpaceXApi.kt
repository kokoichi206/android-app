package jp.mydns.kokoichi0206.kmmtest.network

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import jp.mydns.kokoichi0206.kmmtest.entity.RocketLaunch
import kotlinx.serialization.json.Json

/**
 * SpaceX public API.
 * https://docs.spacexdata.com/?version=latest
 */
class SpaceXApi {

    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                useAlternativeNames = false
            })
        }
    }

    companion object {
        private const val LAUNCHES_ENDPOINT = "https://api.spacexdata.com/v3/launches"
    }

    suspend fun getAllLaunches(): List<RocketLaunch> {
        return httpClient.get(LAUNCHES_ENDPOINT).body()
    }
}