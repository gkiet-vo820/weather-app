package com.example.weatherapp.view

import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherapp.model.ForecastItem
import com.example.weatherapp.ui.theme.WeatherAppTheme
import com.example.weatherapp.util.Utils
import com.example.weatherapp.view.components.CurrentWeatherCard
import com.example.weatherapp.view.components.DailyForecastSection
import com.example.weatherapp.view.components.ErrorMessage
import com.example.weatherapp.view.components.Forecast24hSection
import com.example.weatherapp.view.components.HistorySection
import com.example.weatherapp.view.components.LocationPermissionSwitch
import com.example.weatherapp.view.components.NotificationPermission
import com.example.weatherapp.view.components.SearchBar
import com.example.weatherapp.view.components.WeatherDetailSheet
import com.example.weatherapp.view.components.getBackgroundColors
import com.example.weatherapp.view.components.showWeatherNotification
import com.example.weatherapp.viewmodel.WeatherViewModel

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            WeatherAppTheme {
                val viewModel: WeatherViewModel = viewModel()
                val context = LocalContext.current
                val apiKey = "8435d89eb717235c5501106cb332fe35"

                NotificationPermission()

                val data by viewModel.weatherData
                val loading by viewModel.isLoading
                val error by viewModel.errorMessage
                val historyItems by viewModel.searchHistory
                val forecastData by viewModel.forecastData

                val prefs = remember { context.getSharedPreferences("weather_prefs", MODE_PRIVATE) }

                var userInput by remember { mutableStateOf("") }
                var isCelsius by remember { mutableStateOf(true) }
                var isAutoLocation by remember { mutableStateOf(prefs.getBoolean("auto_location", false)) }
                var showSheet by remember { mutableStateOf(false) }
                var selectedForecast by remember { mutableStateOf<ForecastItem?>(null) }
                val keyboardController = LocalSoftwareKeyboardController.current

                DisposableEffect(prefs) {
                    val listener = SharedPreferences.OnSharedPreferenceChangeListener { p, key ->
                        if (key == "auto_location") {
                            isAutoLocation = p.getBoolean(key, false)
                        }
                    }
                    prefs.registerOnSharedPreferenceChangeListener(listener)
                    onDispose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
                }

                LaunchedEffect(Unit) {
                    viewModel.loadHistory(context)

                    val currentAuto = prefs.getBoolean("auto_location", false)
                    val lastCity = prefs.getString("last_city", null)

                    if (currentAuto) {
                        viewModel.fetchWeatherByLocation(apiKey, context)
                    } else if (lastCity != null) {
                        viewModel.fetchWeather(lastCity, apiKey, context)
                    }
                }

                LaunchedEffect(data) {
                    data?.let {
                        userInput = it.name

                        showWeatherNotification(
                            context,
                            "Thời tiết tại ${it.name}",
                            "Hiện tại là ${it.main.temp.toInt()}°C - ${it.weather.firstOrNull()?.description}"
                        )
                    }
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if (showSheet && selectedForecast != null) {
                        WeatherDetailSheet (selectedForecast!!) { showSheet = false }
                    }

                    PullToRefreshBox(
                        isRefreshing = loading,
                        onRefresh = { viewModel.refreshWeather(apiKey, context) },
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Brush.verticalGradient(getBackgroundColors(data?.weather?.firstOrNull()?.main)))
                                .verticalScroll(rememberScrollState())
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    onClick = { isCelsius = !isCelsius },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.2f)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(if (isCelsius) "Đơn vị: °C" else "Đơn vị: °F", color = Color.White)
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.background(Color.White.copy(0.1f), RoundedCornerShape(12.dp)).padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = if (isAutoLocation) "GPS: Bật" else "GPS: Tắt",
                                        color = if (isAutoLocation) Color(0xFF00E5FF) else Color.White.copy(0.6f),
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                    LocationPermissionSwitch (checked = isAutoLocation, onCheckedChange =  {enabled ->
                                        isAutoLocation = enabled
                                        prefs.edit().putBoolean("auto_location", enabled).apply()
                                        if (enabled) {
                                            viewModel.fetchWeatherByLocation(apiKey, context)
                                            userInput = ""
                                        }
                                    })
                                }
                            }

                            SearchBar(
                                value = userInput,
                                onValueChange = { userInput = it },
                                onSearch = {
                                    if (userInput.isNotBlank()) {
                                        isAutoLocation = false
                                        prefs.edit().putBoolean("auto_location", false)
                                            .apply()
                                        viewModel.fetchWeather(
                                            Utils.formatCityName(userInput),
                                            apiKey,
                                            context
                                        )
                                        keyboardController?.hide()
                                    }
                                }
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            if (historyItems.isNotEmpty()) {
                                HistorySection (
                                    items = historyItems,
                                    onItemClick = { city ->
                                        isAutoLocation = false
                                        prefs.edit().putBoolean("auto_location", false).apply()
                                        userInput = city
                                        viewModel.fetchWeather(city, apiKey, context)
                                    },
                                    onItemLongClick = { city ->
                                        viewModel.deleteHistoryItem(city, context)
                                    },
                                    onClear = { viewModel.clearHistory(context) }
                                )
                            }

                            error?.let { ErrorMessage(it) }

                            data?.let { currentWeather ->
                                Spacer(modifier = Modifier.height(24.dp))
                                //CurrentWeatherCard(currentWeather, isCelsius)
                                CurrentWeatherCard(data, isCelsius)

                                // Gọi thẳng hàm này, không bọc trong forecastData?.let nữa
                                Forecast24hSection(forecastData, isCelsius)
                                //forecastData?.let { Forecast24hSection(it, isCelsius) }
//                                forecastData?.let { DailyForecastSection(it, isCelsius) { item ->
//                                    selectedForecast = item
//                                    showSheet = true
//                                }}
                                DailyForecastSection(forecastData, isCelsius) { item ->
                                    selectedForecast = item
                                    showSheet = true
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}