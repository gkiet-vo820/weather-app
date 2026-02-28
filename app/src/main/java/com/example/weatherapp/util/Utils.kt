package com.example.weatherapp.util

import java.text.Normalizer
import java.util.regex.Pattern

object Utils{
    fun formatTemp(tempC: Double, isCelsius: Boolean): String {
        return if (isCelsius) "${tempC.toInt()}°C" else "${(tempC * 9 / 5 + 32).toInt()}°F"
    }

    fun formatCityName(str: String): String {
        val nfd = Normalizer.normalize(str, Normalizer.Form.NFD)
        return Pattern.compile("\\p{InCombiningDiacriticalMarks}+").matcher(nfd).replaceAll("").replace('đ', 'd').replace('Đ', 'D').trim()
    }
}