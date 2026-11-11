package dev.ahmedabusada.atmos.ui.preview

import dev.ahmedabusada.atmos.domain.location.City
import dev.ahmedabusada.atmos.domain.location.Coordinates
import dev.ahmedabusada.atmos.domain.settings.Temperature
import dev.ahmedabusada.atmos.domain.settings.WindSpeed
import dev.ahmedabusada.atmos.domain.weather.CurrentWeather
import dev.ahmedabusada.atmos.domain.weather.DailyForecast
import dev.ahmedabusada.atmos.domain.weather.Forecast
import dev.ahmedabusada.atmos.domain.weather.HourlyForecast
import dev.ahmedabusada.atmos.domain.weather.WeatherCondition
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * Fake but realistic data for previews, shaped like the recorded Istanbul response in
 * docs/api/samples. In this step the screens run only on this data; step 06 connects them
 * to the real use cases.
 */
object PreviewData {
    private val start = LocalDateTime.parse("2026-11-11T10:00")
    private val today = LocalDate.parse("2026-11-11")
    private val hourlyCodes = listOf(3, 3, 2, 61, 63, 61, 3, 2, 1, 0, 0, 0)
    private val dailyCodes = listOf(3, 3, 61, 80, 1, 0, 71)

    val forecast = Forecast(
        coordinates = Coordinates(41.01, 28.97),
        timeZone = ZoneId.of("Europe/Istanbul"),
        current = CurrentWeather(
            time = LocalDateTime.parse("2026-11-11T10:15"),
            temperature = Temperature(14.9),
            feelsLike = Temperature(11.2),
            humidityPercent = 69,
            windSpeed = WindSpeed(24.8),
            condition = WeatherCondition.Overcast,
            isDay = true,
        ),
        hourly = List(24) { hour ->
            HourlyForecast(
                time = start.plusHours(hour.toLong()),
                temperature = Temperature(14.8 + (hour % 6) - 2),
                condition = WeatherCondition.fromWmoCode(hourlyCodes[hour % hourlyCodes.size]),
                precipitationChancePercent = listOf(0, 0, 20, 70, 80, 40)[hour % 6],
            )
        },
        daily = List(7) { day ->
            DailyForecast(
                date = today.plusDays(day.toLong()),
                condition = WeatherCondition.fromWmoCode(dailyCodes[day]),
                minTemperature = Temperature(11.4 + day % 3),
                maxTemperature = Temperature(16.9 + day % 4),
                sunrise = today.plusDays(day.toLong()).atTime(7, 46),
                sunset = today.plusDays(day.toLong()).atTime(17, 49),
            )
        },
        fetchedAt = Instant.parse("2026-11-11T07:15:00Z"),
    )

    val istanbul = City(745044, "İstanbul", "İstanbul", "Türkiye", "TR", Coordinates(41.01384, 28.94966))
    val ankara = City(323786, "Ankara", "Ankara", "Türkiye", "TR", Coordinates(39.91987, 32.85427))
    val london = City(2643743, "London", "England", "United Kingdom", "GB", Coordinates(51.50853, -0.12574))
}
