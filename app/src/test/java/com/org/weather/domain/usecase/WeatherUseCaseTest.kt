package com.org.weather.domain.usecase

import com.org.weather.data.model.Clouds
import com.org.weather.data.model.Coord
import com.org.weather.data.model.Main
import com.org.weather.data.model.Sys
import com.org.weather.data.model.Weather
import com.org.weather.data.model.WeatherResponse
import com.org.weather.data.model.Wind
import com.org.weather.domain.model.WeatherDisplayData
import com.org.weather.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations


class WeatherUseCaseTest {
    @Mock
    private lateinit var weatherRepository: WeatherRepository
    private lateinit var weatherUseCase: WeatherUseCase

    @Before
    fun setup() {
        // Mock the WeatherRepository
       MockitoAnnotations.openMocks(this)
        // Create the instance of WeatherUseCase
        weatherUseCase = WeatherUseCase(weatherRepository)
    }

    @Test
    fun `invoke should return WeatherResponse from WeatherRepository`() = runTest {
        // Arrange
        val locationQuery = "Plano"
        val expectedResponse = WeatherResponse(
            coord = Coord(-96.6989, 33.0198),
            weather = listOf(
                Weather(id = 804, main = "Clouds", description = "overcast clouds", icon = "04d")
            ),
            base = "stations",
            main = Main(
                temp = 291.13,
                feels_like = 291.23,
                temp_min = 289.91,
                temp_max = 292.75,
                pressure = 1018,
                humidity = 86,
                sea_level = 1018,
                grnd_level = 997
            ),
            visibility = 10000,
            wind = Wind(speed = 5.66, deg = 100, gust = 8.23),
            clouds = Clouds(all = 100),
            dt = 1730998974,
            sys = Sys(
                type = 2,
                id = 2009819,
                country = "US",
                sunrise = 1730983840,
                sunset = 1731022224
            ),
            timezone = -21600,
            id = 4719457,
            name = "Plano",
            cod = 200
        )

        val expectedWeatherDisplayData = WeatherDisplayData(
            iconUrl="https://openweathermap.org/img/wn/04d@2x.png",
            cityName="Plano",
            country="US",
            temperature="17",
            feelsLike="18",
            condition="Overcast clouds",
            windSpeed="5.66 m/s E",
            humidity="86%",
            uvIndex="0",
            dewPoint="15°C",
            visibility="10.0 km"
        )

        // Mock the weatherRepository to return the expected response when invoked
        `when`(weatherRepository.fetchWeatherData(locationQuery)).thenReturn(flowOf(expectedResponse) )

        // Act
        val result = weatherUseCase(locationQuery).first()

        // Assert
        assertEquals(expectedWeatherDisplayData, result)

        // Verify that fetchWeatherData was called with the correct locationQuery
        verify(weatherRepository).fetchWeatherData(locationQuery)
    }
}