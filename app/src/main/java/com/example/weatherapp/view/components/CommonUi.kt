package com.example.weatherapp.view.components

import android.Manifest
import android.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.weatherapp.model.ForecastItem
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationPermissionSwitch(checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    val state = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    Switch(
        checked = checked,
        onCheckedChange = {
            if (state.status.isGranted) onCheckedChange(it)
            else state.launchPermissionRequest()
        },
        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF00E5FF), checkedTrackColor = Color(0xFF00E5FF).copy(0.4f)),
        modifier = Modifier.scale(0.8f)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherDetailSheet(item: ForecastItem, onDismiss: () -> Unit) {
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = Color(0xFF1A1C1E), contentColor = Color.White) {
        Column(modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp, top = 8.dp, start = 24.dp, end = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Chi tiết thời tiết", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(24.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                WeatherDetailItem("Độ ẩm", "${item.main?.humidity}%", R.drawable.ic_menu_mylocation)
                WeatherDetailItem("Gió", "${item.wind?.speed} m/s", R.drawable.ic_menu_directions)
                WeatherDetailItem("Áp suất", "${item.main?.pressure} hPa", R.drawable.ic_menu_compass)
            }
        }
    }
}

@Composable
fun ErrorMessage(msg: String) {
    Text(text = msg, color = Color(0xFFFF5252), fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth().padding(top = 16.dp), textAlign = TextAlign.Center)
}

@Composable
fun getBackgroundColors(weatherMain: String?) : List<Color> {
    return when (weatherMain) {
        "Clear" -> listOf(Color(0xFF4A90E2), Color(0xFF50E3C2))
        "Clouds" -> listOf(Color(0xFF757F9A), Color(0xFFD7DDE8))
        "Rain", "Drizzle"-> listOf(Color(0xFF232526), Color(0xFF414345))
        "Thunderstorm" -> listOf(Color(0xFF0F2027), Color(0xFF203A43), Color(0xFF2C5364))
        "Snow" -> listOf(Color(0xFFE1F5FE), Color(0xFFB3E5FC))
        "Mist", "Smoke", "Haze", "Fog" -> listOf(Color(0xFF606c88), Color(0xFF3f4c6b))
        else -> listOf(Color(0xFF81D4FA), Color(0xFF01579B))
    }
}