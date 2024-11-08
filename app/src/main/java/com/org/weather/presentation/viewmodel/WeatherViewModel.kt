package com.org.weather.presentation.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.org.weather.util.UiState
import com.org.weather.domain.model.WeatherDisplayData
import com.org.weather.domain.usecase.UseCase
import com.org.weather.presentation.ui.DispatcherProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherUseCase: UseCase,
    private val sharedPreferences: SharedPreferences, // Injecting SharedPreferences here
    private val dispatchers: DispatcherProvider
): ViewModel() {

    private val _weatherData = MutableStateFlow<UiState<WeatherDisplayData>?>(null)
    val weatherData = _weatherData

     fun getWeatherData(cityName: String, countryCode: String, stateCode: String? = null) {
            viewModelScope.launch(dispatchers.main) {
                _weatherData.value = UiState.Loading
                val locationQuery = buildLocationQuery(cityName, stateCode, countryCode)
                weatherUseCase(locationQuery)
                    .flowOn(dispatchers.io)
                    .catch {
                        throwable ->
                        val message: String? = if (throwable is retrofit2.HttpException) {
                            "Error message" + throwable.message + " Error code " + throwable.code()
                        } else {
                            throwable.message
                        }
                        _weatherData.value = UiState.Error(message?: "Error")
                    }.collect {

                        _weatherData.value = UiState.Success(it)
                        // Save last searched city and state
                        saveLastSearch(cityName, stateCode)
                    }
            }
    }


    fun getWeatherDataByLocation(lat: Double, lon: Double) {
        viewModelScope.launch(dispatchers.main) {
            _weatherData.value = UiState.Loading
            weatherUseCase.fetchWeatherByLocation(lat, lon)
                .flowOn(dispatchers.io)
                .catch {
                        throwable ->
                    val message: String? = if (throwable is retrofit2.HttpException) {
                        "Error message" + throwable.message + " Error code " + throwable.code()
                    } else {
                        throwable.message
                    }
                    _weatherData.value = UiState.Error(message?: "Error")
                }.collect {
                    _weatherData.value = UiState.Success(it)
                }
        }
    }

    fun getLastCity(): String? = sharedPreferences.getString("last_city", null)
    fun getLastState(): String? = sharedPreferences.getString("last_state", null)

   private fun buildLocationQuery(cityName: String, stateCode: String? = null, countryCode: String): String {
        return if (stateCode != null) {
            "$cityName,$stateCode,$countryCode" // city,state,country
        } else if (countryCode.isNotEmpty()) {
            "$cityName,$countryCode" // city,country
        } else {
            cityName // just city
        }
    }

   private fun saveLastSearch(cityName: String, stateCode: String?) {
        sharedPreferences.edit().apply {
            putString("last_city", cityName)
            putString("last_state", stateCode)
            apply()
        }
    }
}


