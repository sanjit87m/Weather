package com.org.weather.data.repository

import com.org.weather.data.model.WeatherResponse
import com.org.weather.data.remote.datasource.DataSource
import com.org.weather.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class WeatherRepositoryImpl(private val remoteDataSource: DataSource): WeatherRepository {
    override suspend fun fetchWeatherData(locationQuery: String): Flow<WeatherResponse> {
        return  flow { emit(remoteDataSource.getWeatherData(locationQuery)) }
    }

    override suspend fun fetchWeatherByLocation(lat: Double, lon: Double): Flow<WeatherResponse> {
        return flow { emit(remoteDataSource.getWeatherDataByLocation(lat, lon)) }
    }
}