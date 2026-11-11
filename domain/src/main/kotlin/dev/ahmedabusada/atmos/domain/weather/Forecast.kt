package dev.ahmedabusada.atmos.domain.weather

import dev.ahmedabusada.atmos.domain.location.Coordinates
import dev.ahmedabusada.atmos.domain.settings.Temperature
import dev.ahmedabusada.atmos.domain.settings.WindSpeed
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * The full forecast for one place, as the app understands it.
 *
 * Names are domain names (`temperature`, not `temperature_2m`), and times are already
 * real date/time types in the place's own time zone. Compare with `ForecastDto` in
 * :data (step 04), which mirrors the JSON.
 *
 * [fetchedAt] is when the data was downloaded, used to decide if it is still fresh
 * (F-07) and to show "Last updated 2 h ago" (F-08).
 */
data class Forecast(
    val coordinates: Coordinates,
    val timeZone: ZoneId,
    val current: CurrentWeather,
    val hourly: List<HourlyForecast>,
    val daily: List<DailyForecast>,
    val fetchedAt: Instant,
)

/** Conditions right now (F-01). */
data class CurrentWeather(
    val time: LocalDateTime,
    val temperature: Temperature,
    val feelsLike: Temperature,
    val humidityPercent: Int,
    val windSpeed: WindSpeed,
    val condition: WeatherCondition,
    val isDay: Boolean,
)

/** One hour of the 24-hour forecast (F-03). [precipitationChancePercent] may be unknown. */
data class HourlyForecast(
    val time: LocalDateTime,
    val temperature: Temperature,
    val condition: WeatherCondition,
    val precipitationChancePercent: Int?,
)

/** One day of the 7-day forecast (F-04). */
data class DailyForecast(
    val date: LocalDate,
    val condition: WeatherCondition,
    val minTemperature: Temperature,
    val maxTemperature: Temperature,
    val sunrise: LocalDateTime?,
    val sunset: LocalDateTime?,
)
