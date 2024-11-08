package com.org.weather.domain.usecase

import com.org.weather.domain.model.WeatherDisplayData
import kotlinx.coroutines.flow.Flow

interface UseCase {
    suspend operator fun invoke(locationQuery: String): Flow<WeatherDisplayData>
    suspend fun fetchWeatherByLocation(lat: Double, lon: Double): Flow<WeatherDisplayData>
}