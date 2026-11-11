package dev.ahmedabusada.atmos.domain.fakes

import dev.ahmedabusada.atmos.domain.location.City
import dev.ahmedabusada.atmos.domain.location.Coordinates
import dev.ahmedabusada.atmos.domain.settings.Temperature
import dev.ahmedabusada.atmos.domain.settings.WindSpeed
import dev.ahmedabusada.atmos.domain.weather.CurrentWeather
import dev.ahmedabusada.atmos.domain.weather.Forecast
import dev.ahmedabusada.atmos.domain.weather.WeatherCondition
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

/** Small builders so each test shows only the values it cares about. */
object TestData {
    val NOW: Instant = Instant.parse("2026-11-11T07:15:00Z")
    val ISTANBUL = Coordinates(latitude = 41.01, longitude = 28.97)

    fun forecast(fetchedAt: Instant = NOW, coordinates: Coordinates = ISTANBUL) = Forecast(
        coordinates = coordinates,
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
        hourly = emptyList(),
        daily = emptyList(),
        fetchedAt = fetchedAt,
    )

    fun city(id: Long, name: String = "City $id") = City(
        id = id,
        name = name,
        region = null,
        country = "Türkiye",
        countryCode = "TR",
        coordinates = Coordinates(39.92, 32.85),
    )
}
