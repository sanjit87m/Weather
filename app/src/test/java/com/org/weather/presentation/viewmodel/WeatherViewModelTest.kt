package com.org.weather.presentation.viewmodel

import android.content.SharedPreferences
import com.org.weather.TestDispatchers
import com.org.weather.util.UiState
import com.org.weather.domain.model.WeatherDisplayData
import com.org.weather.domain.usecase.UseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`


@OptIn(ExperimentalCoroutinesApi::class)
class WeatherViewModelTest {

    // Test dispatcher for coroutines
    private lateinit var testDispatcher: TestDispatchers

    private lateinit var weatherViewModel: WeatherViewModel
    private val mockWeatherUseCase = mock(UseCase::class.java)
    private val mockSharedPreferences = mock(SharedPreferences::class.java)
    private val mockEditor = mock(SharedPreferences.Editor::class.java)

    @Before
    fun setUp() {
        testDispatcher = TestDispatchers()
        // Mock the SharedPreferences editor
        `when`(mockSharedPreferences.edit()).thenReturn(mockEditor)

        // Create the ViewModel with mocked dependencies
        weatherViewModel = WeatherViewModel(mockWeatherUseCase, mockSharedPreferences, testDispatcher)
    }

    @Test
    fun `test getWeatherData successfully updates state`() = runTest {
        // Arrange
        val cityName = "Plano"
        val countryCode = "US"
        val weatherDisplayData = WeatherDisplayData(
        iconUrl = "iconUrl",
        cityName = "Plano",
        country = "US",
        temperature = "20",
        feelsLike = "20",
        condition = "Mist. Moderate breeze",
        windSpeed = "7.2m/s E",
        humidity = "92",
        uvIndex = "0",
        dewPoint = "19",
        visibility = "3.2km"
    )

        // Simulate UseCase returning mock weather data
        `when`(mockWeatherUseCase.invoke(anyString())).thenReturn(flow { emit(weatherDisplayData) })
        // Act
        weatherViewModel.getWeatherData(cityName, countryCode)

        advanceUntilIdle() // Ensures the flow completes
        assert(weatherViewModel.weatherData.value is UiState.Success)
        val result = (weatherViewModel.weatherData.value as UiState.Success).data
        assert(result == weatherDisplayData)

        // Verify SharedPreferences save method was called
        verify(mockEditor).putString("last_city", cityName)
        verify(mockEditor).putString("last_state", null)
    }

    @Test
    fun `test getWeatherData handles error correctly`() = runTest {
        // Arrange
        val cityName = "Plano"
        val countryCode = "US"
        val errorMessage = "Error message"

        // Simulate UseCase throwing an error
        `when`(mockWeatherUseCase.invoke(anyString())).thenReturn(flow { throw Exception(errorMessage) })

        // Act
        weatherViewModel.getWeatherData(cityName, countryCode)

        advanceUntilIdle()

        assert(weatherViewModel.weatherData.value is UiState.Error)
        val result = (weatherViewModel.weatherData.value as UiState.Error).message
        assert(result == errorMessage)

        // Verify SharedPreferences save method was not called due to error
        verify(mockEditor, never()).putString(anyString(), anyString())
    }

    @Test
    fun `test getLastCity returns correct value from SharedPreferences`() {
        // Arrange
        val lastCity = "Plano"
        `when`(mockSharedPreferences.getString("last_city", null)).thenReturn(lastCity)

        // Act
        val result = weatherViewModel.getLastCity()

        // Assert
        assert(result == lastCity)
    }

    @Test
    fun `test getLastState returns correct value from SharedPreferences`() {
        // Arrange
        val lastState = "TX"
        `when`(mockSharedPreferences.getString("last_state", null)).thenReturn(lastState)

        // Act
        val result = weatherViewModel.getLastState()

        // Assert
        assert(result == lastState)
    }
}