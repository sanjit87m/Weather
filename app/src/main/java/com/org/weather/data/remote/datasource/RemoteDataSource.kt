package com.org.weather.data.remote.datasource

import com.org.weather.data.model.WeatherResponse
import com.org.weather.data.remote.api.WeatherApi
import javax.inject.Inject

class RemoteDataSource @Inject constructor(private val weatherApi: WeatherApi): DataSource {

    override suspend fun getWeatherData(locationQuery: String): WeatherResponse {
        return weatherApi.getWeatherByCityName(locationQuery)
    }
    override suspend fun getWeatherDataByLocation(lat: Double, lon: Double): WeatherResponse {
        return weatherApi.getWeatherByLocation(lat, lon)
    }

}