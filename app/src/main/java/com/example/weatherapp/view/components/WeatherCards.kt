package com.example.weatherapp.view.components

import android.R
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.weatherapp.model.WeatherResponse
import com.example.weatherapp.util.Utils

@Composable
fun CurrentWeatherCard(data: WeatherResponse?, isCelsius: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.1f)),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, Color.White.copy(0.2f))
    ) {
        if (data == null) {
            Column(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(modifier = Modifier.width(100.dp).height(24.dp).clip(RoundedCornerShape(4.dp)).background(
                    shimmerBrush()
                ))
                Spacer(modifier = Modifier.height(16.dp))
                Box(modifier = Modifier.size(120.dp).clip(RoundedCornerShape(60.dp)).background(
                    shimmerBrush()
                ))
                Spacer(modifier = Modifier.height(16.dp))
                Box(modifier = Modifier.width(80.dp).height(40.dp).clip(RoundedCornerShape(4.dp)).background(
                    shimmerBrush()
                ))
            }
        } else {
            Column(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(data.name, color = Color.White, style = MaterialTheme.typography.headlineSmall)
                Box(modifier = Modifier.size(150.dp)) {
                    WeatherAnimation(condition = data.weather.firstOrNull()?.main)
                }
                Text(
                    Utils.formatTemp(data.main.temp, isCelsius),
                    color = if (data.main.temp > 30) Color.Red else Color(0xFF00E5FF),
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    data.weather.firstOrNull()?.description?.uppercase() ?: "",
                    color = Color.White.copy(0.8f)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    WeatherDetailItem(
                        "Gió",
                        "${data.wind.speed} m/s",
                        R.drawable.ic_menu_directions
                    )
                    WeatherDetailItem(
                        "Độ ẩm",
                        "${data.main.humidity}%",
                        R.drawable.ic_menu_mylocation
                    )
                    WeatherDetailItem(
                        "Áp suất",
                        "${data.main.pressure} hPa",
                        R.drawable.ic_menu_compass
                    )
                }
            }
        }
    }
}

@Composable
fun WeatherDetailItem(label: String, value: String, iconRes: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(painter = painterResource(id = iconRes), contentDescription = null, tint = Color.White.copy(0.9f), modifier = Modifier.size(28.dp))
        Text(text = value, color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
        Text(text = label, color = Color.White.copy(0.6f), style = MaterialTheme.typography.labelSmall)
    }
}