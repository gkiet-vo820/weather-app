package com.example.weatherapp.model

import com.squareup.moshi.Json

data class ForecastResponse(
    @Json(name = "list") val list: List<ForecastItem>
)

data class ForecastItem(
    @Json(name = "dt") val dt: Long?,
    @Json(name = "dt_txt") val dtTxt: String?,
    @Json(name = "main") val main: MainData?,
    @Json(name = "weather") val weatherList: List<WeatherDescription>?,
    @Json(name = "wind") val wind: Wind?
)