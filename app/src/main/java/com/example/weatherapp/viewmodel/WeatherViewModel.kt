package com.example.weatherapp.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.model.ForecastItem
import com.example.weatherapp.model.WeatherResponse
import com.example.weatherapp.service.RetrofitClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.launch
import retrofit2.HttpException
import androidx.glance.appwidget.updateAll
import com.example.weatherapp.widget.WeatherWidget
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class WeatherViewModel : ViewModel() {
    var weatherData = mutableStateOf<WeatherResponse?>(null)
    var isLoading = mutableStateOf(false)
    var errorMessage = mutableStateOf<String?>(null)
    var forecastData = mutableStateOf<List<ForecastItem>?>(null)
    var searchHistory = mutableStateOf<List<String>>(listOf())

    fun fetchWeather(cityName: String, apiKey: String, context: Context) {
        viewModelScope.launch {
            try {
                isLoading.value = true
                errorMessage.value = null
                weatherData.value = null
                forecastData.value = null

                coroutineScope {
                    val weatherDeferred = async { RetrofitClient.instance.getWeather(cityName, apiKey) }
                    val forecastDeferred = async { RetrofitClient.instance.getForecast(cityName, apiKey) }

                    val weatherRes = weatherDeferred.await()
                    val forecastRes = forecastDeferred.await()

                    weatherData.value = weatherRes
                    forecastData.value = forecastRes.list

                    updateWidgetData(weatherRes.name, weatherRes.main.temp,
                        weatherRes.weather.firstOrNull()?.description ?: "", context)
                    saveCityToHistory(weatherRes.name, context)
                }

//                val weatherRes = RetrofitClient.instance.getWeather(cityName, apiKey)
//                val forecastRes = RetrofitClient.instance.getForecast(cityName, apiKey)
//
//                weatherData.value = weatherRes
//                forecastData.value = forecastRes.list
//
//                updateWidgetData(weatherRes.name, weatherRes.main.temp,
//                    weatherRes.weather.firstOrNull()?.description ?: "", context)
//                saveCityToHistory(weatherRes.name, context)

            } catch (e: HttpException){
                if (e.code() == 404) {
                    errorMessage.value = "Thành phố không tồn tại hoặc sai tên!"
                } else {
                    errorMessage.value = "Lỗi hệ thống: ${e.code()}"
                }
            } catch (e: Exception) {
                android.util.Log.e("WEATHER_DEBUG", "Lỗi rồi bạn ơi: ${e.message}")
                e.printStackTrace()
            } finally {
                isLoading.value = false
            }
        }
    }

    fun fetchWeatherByLocation(apiKey: String, context: Context){
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        try {
            isLoading.value = true

            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { location ->
                    if (location != null) {
                        fetchWeatherByCoords(location.latitude, location.longitude, apiKey, context)
                    } else {
                        errorMessage.value = "Không thể lấy vị trí. Hãy bật GPS!"
                        isLoading.value = false
                    }
                }
                .addOnFailureListener {
                    errorMessage.value = "Lỗi khi truy cập vị trí: ${it.message}"
                    isLoading.value = false
                }
        } catch (e: SecurityException) {
            errorMessage.value = "Bạn chưa cấp quyền vị trí cho App!"
            isLoading.value = false
        }
    }

    private fun fetchWeatherByCoords(lat: Double, lon: Double, apiKey: String, context: Context) {
        viewModelScope.launch {
            try {
                isLoading.value = true
                errorMessage.value = null
                weatherData.value = null
                forecastData.value = null

                coroutineScope {
                    val weatherDeferred = async { RetrofitClient.instance.getWeatherByCoords(lat, lon, apiKey) }
                    val forecastDeferred = async { RetrofitClient.instance.getForecastByCoords(lat, lon, apiKey) }

                    val weatherRes = weatherDeferred.await()
                    val forecastRes = forecastDeferred.await()

                    weatherData.value = weatherRes
                    forecastData.value = forecastRes.list

                    updateWidgetData(weatherRes.name, weatherRes.main.temp,
                        weatherRes.weather.firstOrNull()?.description ?: "", context)
                    saveCityToHistory(weatherRes.name, context)
                }

//                val weatherRes = RetrofitClient.instance.getWeatherByCoords(lat, lon, apiKey)
//                val forecastRes = RetrofitClient.instance.getForecastByCoords(lat, lon, apiKey)
//
//                weatherData.value = weatherRes
//                forecastData.value = forecastRes.list
//
//
//                updateWidgetData(weatherRes.name, weatherRes.main.temp,
//                    weatherRes.weather.firstOrNull()?.description ?: "", context)
//                saveCityToHistory(weatherRes.name, context)

            } catch (e: Exception) {
                errorMessage.value = "Lỗi khi lấy dữ liệu từ tọa độ!"
            } finally {
                isLoading.value = false
            }
        }
    }

    private fun updateWidgetData(cityName: String, temp: Double, condition: String, context: Context) {
        val sharedPreferences = context.getSharedPreferences("weather_prefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("last_city", cityName)
            putFloat("last_temp", temp.toFloat())
            putString("last_condition", condition)
            commit()
        }

        viewModelScope.launch {
            try {
                WeatherWidget().updateAll(context.applicationContext)
            } catch (e: Exception) {
                android.util.Log.e("WIDGET", "Update failed", e)
            }
        }
    }

    fun refreshWeather(apiKey: String, context: Context) {
        val lastCity = context.getSharedPreferences("weather_prefs", Context.MODE_PRIVATE)
            .getString("last_city", null)
        lastCity?.let { fetchWeather(it, apiKey, context) }
    }

    private fun saveCityToHistory(cityName: String, context: Context) {
        val sharedPreferences = context.getSharedPreferences("weather_prefs", Context.MODE_PRIVATE)
        val historyString = sharedPreferences.getString("search_history", "") ?: ""

        val historyList = historyString.split(",").map { it.trim() }.filter { it.isNotBlank() }.toMutableList()

        if (historyList.contains(cityName)) {
            historyList.remove(cityName)
        }

        historyList.add(0, cityName.trim())

        val updatedHistory = historyList.take(10).joinToString(",")

        sharedPreferences.edit().putString("search_history", updatedHistory).apply()
        searchHistory.value = historyList.take(10)
    }

    fun loadHistory(context: Context) {
        val sharedPreferences = context.getSharedPreferences("weather_prefs", Context.MODE_PRIVATE)
        val historyString = sharedPreferences.getString("search_history", "") ?: ""
        searchHistory.value = historyString.split(",")
            .map { it.trim() }
            .filter { it.isNotBlank() }
    }

    fun clearHistory(context: Context) {
        val sharedPreferences = context.getSharedPreferences("weather_prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().remove("search_history").apply()

        searchHistory.value = listOf()
    }

    fun deleteHistoryItem(cityName: String, context: Context) {
        val sharedPreferences = context.getSharedPreferences("weather_prefs", Context.MODE_PRIVATE)
        val historyString = sharedPreferences.getString("search_history", "") ?: ""

        val historyList = historyString.split(",")
            .map { it.trim() }
            .filter { it.isNotBlank() && it != cityName }

        val updateHistory = historyList.joinToString (",")
        sharedPreferences.edit().putString("search_history", updateHistory).apply()

        searchHistory.value = historyList

    }
}