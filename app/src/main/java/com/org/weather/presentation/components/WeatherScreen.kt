package com.org.weather.presentation.components

import android.Manifest
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.location.LocationServices
import com.org.weather.util.UiState
import com.org.weather.presentation.viewmodel.WeatherViewModel

@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel = hiltViewModel()
) {

    // Collecting uiState from the ViewModel as a State
    val uiState = viewModel.weatherData.collectAsState().value

    var cityName by remember { mutableStateOf(viewModel.getLastCity() ?: "") }
    var stateCode by remember { mutableStateOf(viewModel.getLastState()?: "") }
    // State to track location permission
    var hasLocationPermission by remember { mutableStateOf(false) }

    // Permission launcher for requesting location
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasLocationPermission = isGranted
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)

        // Auto-load last searched city if available
        if (!viewModel.getLastCity().isNullOrEmpty()) {
            viewModel.getWeatherData(
                cityName = cityName,
                countryCode = "US",
                stateCode = stateCode
            )
        }
    }

    // Check and retrieve weather data if permission is granted
    if (hasLocationPermission && viewModel.getLastCity().isNullOrEmpty()) {
        RetrieveWeatherDataWithLocation(viewModel)
    } else {
        // You could show a message or UI explaining why location is needed
    }

    // Obtain the keyboard controller
    val keyboardController = LocalSoftwareKeyboardController.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        OutlinedTextField(
            value = cityName,
            onValueChange = { cityName = it },
            label = { Text("Enter city name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = stateCode,
            onValueChange = { stateCode = it },
            label = { Text("Enter state code (optional)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                // Hide the keyboard
                keyboardController?.hide()
                if (cityName.isNotEmpty()) {
                     UiState.Loading
                    viewModel.getWeatherData(
                        cityName = cityName,
                        countryCode = "US",
                        stateCode = stateCode.ifEmpty { null },
                    )
                }
            },
            modifier = Modifier.align(Alignment.End),
            enabled = uiState !is UiState.Loading // Disable button during loading

        ) {
            Text("Search")
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Handling different states
        if (uiState == null) {
            InitialScreen()
        } else{
            when(uiState) {
                is UiState.Loading -> LoadingScreen()
                is UiState.Success -> WeatherDetailsScreen(uiState.data)
                is UiState.Error -> ErrorScreen(uiState.message)
            }
        }
    }
}

@Composable
fun LoadingScreen() {
    Box(contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun InitialScreen() {
    Box(contentAlignment = Alignment.Center) {
        Text(text = "No data available")
    }
}

@Composable
fun ErrorScreen(message: String) {
    Box(contentAlignment = Alignment.Center) {
        Text(text = "Error: $message", color = MaterialTheme.colorScheme.error)
    }
}

@Composable
fun RetrieveWeatherDataWithLocation(
    viewModel: WeatherViewModel
) {
    val context = LocalContext.current // Get context from LocalContext
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    // Fetch location and weather data when permission is granted
    LaunchedEffect(Unit) {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    viewModel.getWeatherDataByLocation(location.latitude, location.longitude)
                }
            }
            .addOnFailureListener {
                // Handle location error (e.g., display an error message)
            }
    }
}



