package jp.mydns.kokoichi0206.weatherapp.domain.repository

import jp.mydns.kokoichi0206.weatherapp.domain.util.Resource
import jp.mydns.kokoichi0206.weatherapp.domain.weather.WeatherInfo

interface WeatherRepository {

    suspend fun getWeatherData(lat: Double, long: Double): Resource<WeatherInfo>
}