package com.example.weatherapp.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.weatherapp.model.ForecastItem
import com.example.weatherapp.util.Utils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun Forecast24hSection(forecastList: List<ForecastItem>?, isCelsius: Boolean) {
    Text("Dự báo 24h tới", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 12.dp))
    if (forecastList == null) {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(5) {
                Box(
                    modifier = Modifier
                        .size(width = 80.dp, height = 110.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .background(shimmerBrush())
                )
            }
        }
    } else {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(forecastList.take(8).size) { index ->
                val item = forecastList[index]
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.1f)),
                    shape = RoundedCornerShape(15.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val time = remember(item.dt) {
                            if (item.dt != null) {
                                val date = Date(item.dt * 1000)
                                val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
                                sdf.format(date)
                            } else {
                                item.dtTxt?.substring(11, 16) ?: "--:--"
                            }
                        }
                        Text(
                            time,
                            color = Color.White.copy(0.7f),
                            style = MaterialTheme.typography.labelMedium
                        )
                        AsyncImage(
                            model = "https://openweathermap.org/img/wn/${item.weatherList?.firstOrNull()?.icon}@2x.png",
                            contentDescription = null, modifier = Modifier.size(50.dp)
                        )
                        Text(
                            Utils.formatTemp(item.main?.temp ?: 0.0, isCelsius),
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DailyForecastSection(forecastList: List<ForecastItem>?, isCelsius: Boolean, onItemClick: (ForecastItem) -> Unit) {
    Text("Dự báo các ngày tới", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 12.dp))

    Card(colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.1f)), shape = RoundedCornerShape(20.dp)) {
        Column(modifier = Modifier.padding(8.dp)) {
            if (forecastList == null) {
                repeat(5) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.weight(1f).height(20.dp).clip(RoundedCornerShape(4.dp)).background(
                            shimmerBrush()
                        ))
                        Spacer(modifier = Modifier.width(16.dp))

                        Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(20.dp)).background(
                            shimmerBrush()
                        ))
                        Spacer(modifier = Modifier.width(16.dp))

                        Box(modifier = Modifier.weight(1f).height(20.dp).clip(RoundedCornerShape(4.dp)).background(
                            shimmerBrush()
                        ))
                    }
                }
            } else {

                val dailyItems = forecastList
                    .groupBy {
                        val date = Date((it.dt ?: 0) * 1000)
                        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)
                    }
                    .values
                    .map { dayGroup ->
                        dayGroup.find { it.dtTxt?.contains("12:00:00") == true } ?: dayGroup[dayGroup.size / 2]
                    }
                    .drop(1)
                    .take(5)
                dailyItems.forEach { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable { onItemClick(item) }.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val sdf = SimpleDateFormat("EEEE", Locale("vi", "VN"))
                        val date = Date((item.dt ?: 0) * 1000)
                        Text(
                            sdf.format(date).replaceFirstChar { it.uppercase() },
                            color = Color.White,
                            modifier = Modifier.weight(1f)
                        )
                        AsyncImage(
                            model = "https://openweathermap.org/img/wn/${item.weatherList?.firstOrNull()?.icon}@2x.png",
                            contentDescription = null, modifier = Modifier.size(40.dp)
                        )
                        Text(
                            Utils.formatTemp(item.main?.temp ?: 0.0, isCelsius),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.End,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}