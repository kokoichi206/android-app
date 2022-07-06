package jp.mydns.kokoichi0206.weatherapp.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.DayOfWeek
import java.time.LocalDateTime

@Composable
fun WeatherForecastWeek(
    state: WeatherState,
    modifier: Modifier = Modifier,
) {
    state.weatherInfo?.weatherDataPerDay?.let { data ->

        Column {
            for (index: Int in 0..data.size) {
                data[index]?.let { dataPerDay ->

                    val maxTemp = dataPerDay
                        .maxByOrNull { it.temperatureCelsius }
                        ?.temperatureCelsius?.toInt()
                    val minTemp = dataPerDay
                        .minByOrNull { it.temperatureCelsius }
                        ?.temperatureCelsius?.toInt()

                    // humidity: the first of the day <- OK??
                    val humidity = dataPerDay[0].humidity.toInt()

                    val date = LocalDateTime.now().plusDays(index.toLong())
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            modifier = Modifier.width(30.dp),
                            text = "${date.dayOfMonth}",
                            fontSize = 24.sp,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                        )
                        Text(
                            text = "(${dayOfWeekToJapanese(date.dayOfWeek)})",
                            fontSize = 20.sp,
                            color = Color.White,
                        )

                        Text(
                            text = buildAnnotatedString {
                                withStyle(style = SpanStyle(fontSize = 20.sp)) {
                                    append("$maxTemp")
                                }
                                withStyle(style = SpanStyle(fontSize = 14.sp)) {
                                    append("°C")
                                }
                            },
                            color = Color.Red,
                        )
                        Text(
                            text = buildAnnotatedString {
                                withStyle(style = SpanStyle(fontSize = 20.sp)) {
                                    append("$minTemp")
                                }
                                withStyle(style = SpanStyle(fontSize = 14.sp)) {
                                    append("°C")
                                }
//                                append("ello ")
                            },
                            color = Color.Blue,
                        )
                        Text(
                            text = "${humidity}%",
                            fontSize = 20.sp,
                            color = Color.White,
                        )
                    }
                }
            }
        }
    }
}

fun dayOfWeekToJapanese(dayOfWeek: DayOfWeek): String {
    return when (dayOfWeek) {
        DayOfWeek.MONDAY -> "月"
        DayOfWeek.TUESDAY -> "火"
        DayOfWeek.WEDNESDAY -> "水"
        DayOfWeek.THURSDAY -> "木"
        DayOfWeek.FRIDAY -> "金"
        DayOfWeek.SATURDAY -> "土"
        DayOfWeek.SUNDAY -> "日"
    }
}
