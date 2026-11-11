package dev.ahmedabusada.atmos.ui.format

import dev.ahmedabusada.atmos.domain.settings.Temperature
import dev.ahmedabusada.atmos.domain.settings.TemperatureUnit
import dev.ahmedabusada.atmos.domain.settings.WindSpeed
import dev.ahmedabusada.atmos.domain.settings.WindSpeedUnit
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.util.Locale

/** The same values, formatted in all three app languages. */
class WeatherFormatterTest {
    /** The visible text, without the invisible directional isolates. */
    private fun String.visible() = filterNot { it == '\u2066' || it == '\u2069' }

    private val english = WeatherFormatter(Locale.forLanguageTag("en"), use24HourClock = true)
    private val turkish = WeatherFormatter(Locale.forLanguageTag("tr"), use24HourClock = true)
    private val arabic = WeatherFormatter(Locale.forLanguageTag("ar"), use24HourClock = true)

    @Test
    fun `temperatures are whole degrees in the chosen unit`() {
        assertEquals("15°", english.temperature(Temperature(14.9), TemperatureUnit.Celsius).visible())
        assertEquals("59°", english.temperature(Temperature(14.9), TemperatureUnit.Fahrenheit).visible())
        assertEquals("-3°", english.temperature(Temperature(-2.6), TemperatureUnit.Celsius).visible())
    }

    @Test
    fun `arabic uses western digits, as decided in ADR-0005`() {
        assertEquals("15°", arabic.temperature(Temperature(14.9), TemperatureUnit.Celsius).visible())
        assertEquals("25 km/h", arabic.windSpeed(WindSpeed(24.8), WindSpeedUnit.KilometersPerHour).visible())
    }

    @Test
    fun `turkish uses a decimal comma`() {
        assertEquals("6,9 m/s", turkish.windSpeed(WindSpeed(24.8), WindSpeedUnit.MetersPerSecond).visible())
        assertEquals("6.9 m/s", english.windSpeed(WindSpeed(24.8), WindSpeedUnit.MetersPerSecond).visible())
    }

    @Test
    fun `turkish puts the percent sign first`() {
        assertEquals("69%", english.percentage(69).visible())
        assertEquals("%69", turkish.percentage(69).visible())
    }

    @Test
    fun `arabic percentages use western digits`() {
        val text = arabic.percentage(69).visible()
        assertTrue("was $text", text.contains("69"))
    }

    @Test
    fun `wind speed in every unit`() {
        val wind = WindSpeed(24.8)
        assertEquals("25 km/h", english.windSpeed(wind, WindSpeedUnit.KilometersPerHour).visible())
        assertEquals("15 mph", english.windSpeed(wind, WindSpeedUnit.MilesPerHour).visible())
    }

    @Test
    fun `hours follow the device clock setting`() {
        val time = LocalDateTime.parse("2026-11-11T14:00")
        assertEquals("14:00", english.hour(time).visible())
        assertEquals("2 PM", WeatherFormatter(Locale.forLanguageTag("en"), use24HourClock = false).hour(time).visible())
    }

    @Test
    fun `day names are in the app language`() {
        assertEquals("Thursday", english.dayName(DayOfWeek.THURSDAY).visible())
        assertEquals("Perşembe", turkish.dayName(DayOfWeek.THURSDAY).visible())
        assertEquals("الخميس", arabic.dayName(DayOfWeek.THURSDAY).visible())
    }

    @Test
    fun `values are isolated so an Arabic sentence cannot reorder them`() {
        val wind = arabic.windSpeed(WindSpeed(24.8), WindSpeedUnit.KilometersPerHour)

        assertEquals('\u2066', wind.first())
        assertEquals('\u2069', wind.last())
    }
}
