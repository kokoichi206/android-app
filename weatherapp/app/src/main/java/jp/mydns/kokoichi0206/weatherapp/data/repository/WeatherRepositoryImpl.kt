package jp.mydns.kokoichi0206.weatherapp.data.repository

import jp.mydns.kokoichi0206.weatherapp.data.mappers.toWeatherInfo
import jp.mydns.kokoichi0206.weatherapp.data.remote.WeatherApi
import jp.mydns.kokoichi0206.weatherapp.domain.repository.WeatherRepository
import jp.mydns.kokoichi0206.weatherapp.domain.util.Resource
import jp.mydns.kokoichi0206.weatherapp.domain.weather.WeatherInfo
import java.lang.Exception
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val api: WeatherApi,
) : WeatherRepository {

    override suspend fun getWeatherData(lat: Double, long: Double): Resource<WeatherInfo> {
        return try {
            Resource.Success(
                data = api.getWeatherData(
                    lat = lat,
                    long = long,
                ).toWeatherInfo()
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e.message ?: "An unknown ERROR")
        }
    }
}