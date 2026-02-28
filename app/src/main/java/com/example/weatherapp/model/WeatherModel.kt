package com.example.weatherapp.model

import com.squareup.moshi.Json

data class WeatherResponse (
    @Json(name = "name") val name: String,
    @Json(name = "main") val main: MainData,
    @Json(name = "weather") val weather: List<WeatherDescription>,
    @Json(name = "wind") val wind: Wind,
    @Json(name = "visibility") val visibility: Int
)

data class MainData (
    @Json(name = "temp") val temp: Double,
    @Json(name = "humidity") val humidity: Int,
    @Json(name = "pressure") val pressure: Int,
    @Json(name = "feels_like") val feelsLike: Double
)

data class WeatherDescription (
    @Json(name = "main") val main: String,
    @Json(name = "description") val description: String,
    @Json(name = "icon") val icon: String
)

data class Wind(
    @Json(name = "speed") val speed: Double
)