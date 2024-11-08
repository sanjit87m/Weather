package com.org.weather.data.remote.api

import com.org.weather.data.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("data/2.5/weather")
    suspend fun getWeatherByCityName(
        @Query("q") locationQuery: String,
        @Query("appid") apiKey: String = "19e47770469f11c11829baa85a3c85c6"
    ): WeatherResponse

    @GET("data/2.5/weather")
    suspend fun getWeatherByLocation(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String = "19e47770469f11c11829baa85a3c85c6"
    ): WeatherResponse
}