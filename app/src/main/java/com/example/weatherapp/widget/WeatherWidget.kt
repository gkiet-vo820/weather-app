package com.example.weatherapp.widget

    import android.annotation.SuppressLint
    import android.content.Context
    import androidx.compose.ui.graphics.Color
    import androidx.compose.ui.unit.dp
    import androidx.compose.ui.unit.sp
    import androidx.glance.GlanceId
    import androidx.glance.GlanceModifier
    import androidx.glance.action.actionStartActivity
    import androidx.glance.action.clickable
    import androidx.glance.appwidget.GlanceAppWidget
    import androidx.glance.appwidget.appWidgetBackground
    import androidx.glance.appwidget.cornerRadius
    import androidx.glance.appwidget.provideContent
    import androidx.glance.background
    import androidx.glance.layout.Alignment
    import androidx.glance.layout.Box
    import androidx.glance.layout.Column
    import androidx.glance.layout.fillMaxSize
    import androidx.glance.layout.padding
    import androidx.glance.text.FontWeight
    import androidx.glance.text.Text
    import androidx.glance.text.TextAlign
    import androidx.glance.text.TextStyle
    import androidx.glance.unit.ColorProvider
    import com.example.weatherapp.view.MainActivity

    class WeatherWidget : GlanceAppWidget() {
        @SuppressLint("RestrictedApi")
        override suspend fun provideGlance(context: Context, id: GlanceId) {

            provideContent {
                val preferences = context.getSharedPreferences("weather_prefs", Context.MODE_PRIVATE)
                val city = preferences.getString("last_city", "") ?: ""
                val temp = preferences.getFloat("last_temp", 0f)
                val condition = preferences.getString("last_condition", "") ?: ""

                val backgroundColor = when (condition) {
                    "Clear" -> Color(0xFF4FC3F7)
                    "Clouds" -> Color(0xFF90A4AE)
                    "Rain", "Drizzle", "Thunderstorm" -> Color(0xFF37474F)
                    "Snow" -> Color(0xFFE1F5FE)
                    else -> Color(0xFF2196F3)
                }
                Box(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .appWidgetBackground()
                        .background(backgroundColor)
                        .cornerRadius(16.dp)
                        .padding(16.dp)
                        .clickable(actionStartActivity<MainActivity>()),
                    contentAlignment = Alignment.Center
                ) {
                    if (city.isEmpty()) {
                        Text(
                            text = "Chưa có dữ liệu\nChạm để mở App",
                            style = TextStyle(
                                color = ColorProvider(Color.White),
                                textAlign = TextAlign.Center
                            )
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = city,
                                style = TextStyle(
                                    color = ColorProvider(Color.White),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                            )
                            Text(
                                text = "${temp.toInt()}°C",
                                style = TextStyle(
                                    color = ColorProvider(Color.White),
                                    fontSize = 32.sp
                                )
                            )
                            if (condition.isNotEmpty()) {
                                Text(
                                    text = condition.replaceFirstChar { it.uppercase() },
                                    style = TextStyle(
                                        color = ColorProvider(Color.White.copy(alpha = 0.8f)),
                                        fontSize = 14.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }