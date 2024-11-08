package com.org.weather.util

sealed class UiState<out T> {
    data class Success<T : Any>(val data: T?) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
    data object Loading : UiState<Nothing>()
}