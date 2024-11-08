package com.org.weather.domain.usecase

import com.org.weather.data.model.WeatherResponse
import com.org.weather.domain.model.WeatherDisplayData
import com.org.weather.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WeatherUseCase @Inject constructor(private val weatherRepository: WeatherRepository): UseCase {

    override suspend operator fun invoke(locationQuery: String): Flow<WeatherDisplayData> {
        return weatherRepository.fetchWeatherData(locationQuery)
            .map { response -> mapWeatherResponseToDisplayData(response) }
    }

    override suspend fun fetchWeatherByLocation(lat: Double, lon: Double): Flow<WeatherDisplayData> {
        return weatherRepository.fetchWeatherByLocation(lat, lon)
            .map { response -> mapWeatherResponseToDisplayData(response) }
    }

    private fun mapWeatherResponseToDisplayData(response: WeatherResponse): WeatherDisplayData {
        val weatherIconCode = response.weather.firstOrNull()?.icon ?: "01d" // Default to "01d" if no icon code is available
        // Converting temperatures from Kelvin to Celsius
        val temperatureCelsius = (response.main.temp - 273.15).toInt().toString()
        val feelsLikeCelsius = (response.main.feels_like - 273.15).toInt().toString()
        val dewPointCelsius = calculateDewPoint(response.main.temp, response.main.humidity)

        return WeatherDisplayData(
            iconUrl = getIconUrl(weatherIconCode),
            cityName = response.name,
            country = response.sys.country,
            temperature = temperatureCelsius,
            feelsLike = feelsLikeCelsius,
            condition = response.weather.firstOrNull()?.description?.capitalize() ?: "Unknown",
            windSpeed = "${response.wind.speed} m/s ${getWindDirection(response.wind.deg)}",
            humidity = "${response.main.humidity}%",
            uvIndex = "0", // Placeholder if UV data isn't available
            dewPoint = "${dewPointCelsius}°C",
            visibility = "${response.visibility / 1000.0} km"
        )
    }

    private fun calculateDewPoint(tempKelvin: Double, humidity: Int): Int {
        // Formula for dew point approximation
        val tempCelsius = tempKelvin - 273.15
        val dewPoint = tempCelsius - (100 - humidity) / 5.0
        return dewPoint.toInt()
    }

    private fun getWindDirection(degrees: Int): String {
        return when ((degrees / 45) % 8) {
            0 -> "N"
            1 -> "NE"
            2 -> "E"
            3 -> "SE"
            4 -> "S"
            5 -> "SW"
            6 -> "W"
            else -> "NW"
        }
    }

  private  fun getIconUrl(iconCode: String): String {
        return "https://openweathermap.org/img/wn/${iconCode}@2x.png"
    }
}
