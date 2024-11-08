package com.org.weather.data.remote.datasource

import com.org.weather.data.model.WeatherResponse

interface DataSource {
    suspend fun getWeatherData(locationQuery: String): WeatherResponse
    suspend fun getWeatherDataByLocation(lat: Double, lon: Double): WeatherResponse
}