package com.org.weather.presentation.components

interface Screen {
    val route: String
}

object WeatherScreen: Screen{
    override val route: String
        get() = "weather_screen"

}