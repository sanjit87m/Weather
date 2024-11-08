package com.org.weather.domain.repository

import com.org.weather.data.model.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    suspend fun fetchWeatherData(locationQuery: String): Flow<WeatherResponse>
    suspend fun fetchWeatherByLocation(lat: Double, lon: Double): Flow<WeatherResponse>
}