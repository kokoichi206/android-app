package jp.mydns.kokoichi0206.weatherapp.presentation

import jp.mydns.kokoichi0206.weatherapp.domain.weather.WeatherInfo

data class WeatherState(
    val weatherInfo: WeatherInfo? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
)
