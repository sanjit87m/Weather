package com.org.weather.data.repository

import com.org.weather.data.model.Clouds
import com.org.weather.data.model.Coord
import com.org.weather.data.model.Main
import com.org.weather.data.model.Sys
import com.org.weather.data.model.Weather
import com.org.weather.data.model.WeatherResponse
import com.org.weather.data.model.Wind
import com.org.weather.data.remote.datasource.DataSource

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations


class WeatherRepositoryImplTest {
    @Mock
    private lateinit var remoteDataSource: DataSource
    private lateinit var weatherRepository: WeatherRepositoryImpl

    @Before
    fun setup() {

        // Initialize Mockito
        MockitoAnnotations.openMocks(this)
        weatherRepository = WeatherRepositoryImpl(remoteDataSource)
    }

    @Test
    fun `fetchWeatherData should emit WeatherResponse when successful`() = runTest {
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

        `when`(remoteDataSource.getWeatherData(locationQuery)).thenReturn(expectedResponse)

        // Act
        val result = weatherRepository.fetchWeatherData(locationQuery).first()

        // Assert
        assertEquals(expectedResponse, result)
        verify(remoteDataSource).getWeatherData(locationQuery)


    }
}