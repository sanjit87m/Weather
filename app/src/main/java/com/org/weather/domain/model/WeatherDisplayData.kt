package com.org.weather.domain.model

data class WeatherDisplayData(
    val iconUrl: String,
    val cityName: String,
    val country: String,
    val temperature: String,
    val feelsLike: String,
    val condition: String,
    val windSpeed: String,
    val humidity: String,
    val uvIndex: String, // Assuming it's part of the API; otherwise, you can set it as "N/A"
    val dewPoint: String,
    val visibility: String
)