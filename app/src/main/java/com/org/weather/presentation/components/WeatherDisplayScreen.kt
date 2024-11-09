package com.org.weather.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.org.weather.domain.model.WeatherDisplayData

@Composable
fun WeatherDetailsScreen(
    data: WeatherDisplayData?
) {
    data?.let {
        // A simple Card to wrap weather information
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Display weather data here
                WeatherIcon(url = data.iconUrl)
                Text(
                    text = "City Name: ${data.cityName}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Country: ${data.country}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )

                // Temperature and condition
                Text(
                    text = "${data.temperature}°C",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Feels like ${data.feelsLike}°C. ${data.condition}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimary
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Wind and humidity details
                WeatherDetailRow(label = "Wind Speed", value = data.windSpeed)
                WeatherDetailRow(label = "Humidity", value = data.humidity)
                WeatherDetailRow(label = "Dew Point", value = data.dewPoint)
                WeatherDetailRow(label = "Visibility", value = data.visibility)

            }
        }
    }

}

@Composable
fun WeatherDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
        )
    }
}


@Composable
fun WeatherIcon(url: String) {
    Image(
        painter = rememberAsyncImagePainter(url),
        contentDescription = "Weather Icon",
        modifier = Modifier.size(80.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewWeatherDetailsScreen() {
    val weatherDisplayData = WeatherDisplayData(
        iconUrl = "",
        cityName = "Plano",
        country = "US",
        temperature = "20",
        feelsLike = "20",
        condition = "Mist. Moderate breeze",
        windSpeed = "7.2m/s E",
        humidity = "92",
        dewPoint = "19",
        visibility = "3.2km"
    )
    WeatherDetailsScreen(
        data = weatherDisplayData,
    )
}