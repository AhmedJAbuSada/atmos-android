package dev.ahmedabusada.atmos.data.mapper

import dev.ahmedabusada.atmos.data.remote.AtmosJson
import dev.ahmedabusada.atmos.data.remote.InvalidResponseException
import dev.ahmedabusada.atmos.data.remote.dto.CurrentDto
import dev.ahmedabusada.atmos.data.remote.dto.ForecastDto
import dev.ahmedabusada.atmos.data.remote.dto.HourlyDto
import dev.ahmedabusada.atmos.data.testing.Samples
import dev.ahmedabusada.atmos.domain.location.Coordinates
import dev.ahmedabusada.atmos.domain.weather.WeatherCondition
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

class ForecastMapperTest {
    private val requested = Coordinates(41.01, 28.97)
    private val fetchedAt = Instant.parse("2026-11-11T07:15:00Z")
    private val delta = 0.001

    private fun recorded() = AtmosJson.decodeFromString<ForecastDto>(Samples.read("forecast_istanbul.json"))
        .toDomain(requested, fetchedAt)

    @Test
    fun `maps the current conditions of the recorded Istanbul response`() {
        val current = recorded().current

        assertEquals(LocalDateTime.parse("2026-11-11T10:15"), current.time)
        assertEquals(14.9, current.temperature.celsius, delta)
        assertEquals(11.2, current.feelsLike.celsius, delta)
        assertEquals(69, current.humidityPercent)
        assertEquals(24.8, current.windSpeed.kilometersPerHour, delta)
        assertEquals(WeatherCondition.Overcast, current.condition)
        assertTrue(current.isDay)
    }

    @Test
    fun `zips the parallel hourly arrays into 24 hours`() {
        val hourly = recorded().hourly

        assertEquals(24, hourly.size)
        assertEquals(LocalDateTime.parse("2026-11-11T10:00"), hourly.first().time)
        assertEquals(14.8, hourly.first().temperature.celsius, delta)
        assertEquals(WeatherCondition.PartlyCloudy, hourly.first().condition)
        assertEquals(0, hourly.first().precipitationChancePercent)
    }

    @Test
    fun `maps 7 days with sunrise and sunset`() {
        val daily = recorded().daily

        assertEquals(7, daily.size)
        assertEquals(LocalDate.parse("2026-11-11"), daily.first().date)
        assertEquals(11.4, daily.first().minTemperature.celsius, delta)
        assertEquals(16.9, daily.first().maxTemperature.celsius, delta)
        assertEquals(LocalDateTime.parse("2026-11-11T07:46"), daily.first().sunrise)
        assertEquals(LocalDateTime.parse("2026-11-11T17:49"), daily.first().sunset)
    }

    @Test
    fun `keeps the requested coordinates, not the snapped ones from the response`() {
        // The response says 41.02444, 28.940546 (the API's grid point).
        assertEquals(requested, recorded().coordinates)
    }

    @Test
    fun `uses the named time zone and the fetch time`() {
        val forecast = recorded()

        assertEquals(ZoneId.of("Europe/Istanbul"), forecast.timeZone)
        assertEquals(fetchedAt, forecast.fetchedAt)
    }

    @Test
    fun `given no current conditions, then the response is invalid`() {
        assertThrows(InvalidResponseException::class.java) {
            ForecastDto(current = null).toDomain(requested, fetchedAt)
        }
    }

    @Test
    fun `given arrays of different lengths, then maps only complete hours instead of crashing`() {
        val dto = ForecastDto(
            current = validCurrent(),
            hourly = HourlyDto(
                time = listOf("2026-11-11T10:00", "2026-11-11T11:00", "2026-11-11T12:00"),
                temperature = listOf(14.8, null), // one value missing, one null
                weatherCode = listOf(2),
                precipitationProbability = emptyList(),
            ),
        )

        val hourly = dto.toDomain(requested, fetchedAt).hourly

        assertEquals(1, hourly.size)
        assertNull("missing precipitation stays unknown", hourly.single().precipitationChancePercent)
    }

    @Test
    fun `given an unknown time zone, then falls back to the UTC offset`() {
        val dto = ForecastDto(timezone = "Mars/Olympus", utcOffsetSeconds = 10_800, current = validCurrent())

        assertEquals(ZoneId.of("+03:00"), dto.toDomain(requested, fetchedAt).timeZone)
    }

    @Test
    fun `given no apparent temperature, then feels like equals the temperature`() {
        val dto = ForecastDto(current = validCurrent().copy(apparentTemperature = null))

        assertEquals(15.0, dto.toDomain(requested, fetchedAt).current.feelsLike.celsius, delta)
    }

    @Test
    fun `given is_day 0, then it is night`() {
        val dto = ForecastDto(current = validCurrent().copy(isDay = 0))

        assertEquals(false, dto.toDomain(requested, fetchedAt).current.isDay)
    }

    private fun validCurrent() = CurrentDto(
        time = "2026-11-11T10:15",
        temperature = 15.0,
        apparentTemperature = 12.0,
        relativeHumidity = 60,
        windSpeed = 10.0,
        weatherCode = 0,
        isDay = 1,
    )
}
