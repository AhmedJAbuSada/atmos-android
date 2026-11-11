package dev.ahmedabusada.atmos.data.mapper

import dev.ahmedabusada.atmos.data.remote.InvalidResponseException
import dev.ahmedabusada.atmos.data.remote.dto.CurrentDto
import dev.ahmedabusada.atmos.data.remote.dto.DailyDto
import dev.ahmedabusada.atmos.data.remote.dto.ForecastDto
import dev.ahmedabusada.atmos.data.remote.dto.HourlyDto
import dev.ahmedabusada.atmos.domain.location.Coordinates
import dev.ahmedabusada.atmos.domain.settings.Temperature
import dev.ahmedabusada.atmos.domain.settings.WindSpeed
import dev.ahmedabusada.atmos.domain.weather.CurrentWeather
import dev.ahmedabusada.atmos.domain.weather.DailyForecast
import dev.ahmedabusada.atmos.domain.weather.Forecast
import dev.ahmedabusada.atmos.domain.weather.HourlyForecast
import dev.ahmedabusada.atmos.domain.weather.WeatherCondition
import java.time.DateTimeException
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

/**
 * Turns the JSON-shaped [ForecastDto] into the domain [Forecast].
 *
 * - [requested] are the coordinates **we asked for**. The API snaps them to its grid
 *   (API_CONTRACT §1, point 5), so the response's own coordinates are ignored.
 * - The current conditions are required; without them there is nothing to show, so
 *   that is an [InvalidResponseException].
 * - Hourly and daily entries are zipped from parallel arrays. An entry with a missing
 *   time or temperature is **skipped**, not fatal: a forecast with 23 hours is still
 *   useful.
 */
internal fun ForecastDto.toDomain(requested: Coordinates, fetchedAt: Instant): Forecast {
    val current = current?.toDomain() ?: throw InvalidResponseException("Missing current conditions")
    return Forecast(
        coordinates = requested,
        timeZone = zoneOf(timezone, utcOffsetSeconds),
        current = current,
        hourly = hourly?.toDomain().orEmpty(),
        daily = daily?.toDomain().orEmpty(),
        fetchedAt = fetchedAt,
    )
}

private fun CurrentDto.toDomain(): CurrentWeather? {
    return CurrentWeather(
        time = parseDateTime(time) ?: return null,
        temperature = Temperature(temperature ?: return null),
        feelsLike = Temperature(apparentTemperature ?: temperature),
        humidityPercent = relativeHumidity ?: return null,
        windSpeed = WindSpeed(windSpeed ?: return null),
        condition = WeatherCondition.fromWmoCode(weatherCode),
        // The API sends 1 or 0. Unknown? Assume day: a sun icon is the safer default.
        isDay = isDay != 0,
    )
}

private fun HourlyDto.toDomain(): List<HourlyForecast> = time.indices.mapNotNull { i ->
    HourlyForecast(
        time = parseDateTime(time[i]) ?: return@mapNotNull null,
        temperature = Temperature(temperature.getOrNull(i) ?: return@mapNotNull null),
        condition = WeatherCondition.fromWmoCode(weatherCode.getOrNull(i)),
        precipitationChancePercent = precipitationProbability.getOrNull(i),
    )
}

private fun DailyDto.toDomain(): List<DailyForecast> = time.indices.mapNotNull { i ->
    DailyForecast(
        date = parseDate(time[i]) ?: return@mapNotNull null,
        condition = WeatherCondition.fromWmoCode(weatherCode.getOrNull(i)),
        minTemperature = Temperature(temperatureMin.getOrNull(i) ?: return@mapNotNull null),
        maxTemperature = Temperature(temperatureMax.getOrNull(i) ?: return@mapNotNull null),
        sunrise = parseDateTime(sunrise.getOrNull(i)),
        sunset = parseDateTime(sunset.getOrNull(i)),
    )
}

/** Times are local to the place and have no offset, for example `2026-11-11T10:15`. */
private fun parseDateTime(value: String?): LocalDateTime? = value?.let {
    try {
        LocalDateTime.parse(it)
    } catch (_: DateTimeException) {
        null
    }
}

private fun parseDate(value: String?): LocalDate? = value?.let {
    try {
        LocalDate.parse(it)
    } catch (_: DateTimeException) {
        null
    }
}

/** Prefer the named zone (handles daylight saving); fall back to the fixed offset, then UTC. */
private fun zoneOf(name: String?, offsetSeconds: Int?): ZoneId {
    name?.let {
        try {
            return ZoneId.of(it)
        } catch (_: DateTimeException) {
            // fall through to the offset
        }
    }
    return offsetSeconds?.let(ZoneOffset::ofTotalSeconds) ?: ZoneOffset.UTC
}
