package dev.ahmedabusada.atmos.ui.format

import dev.ahmedabusada.atmos.domain.settings.Temperature
import dev.ahmedabusada.atmos.domain.settings.TemperatureUnit
import dev.ahmedabusada.atmos.domain.settings.WindSpeed
import dev.ahmedabusada.atmos.domain.settings.WindSpeedUnit
import java.text.NumberFormat
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.roundToInt

/**
 * Turns domain values into text for one [locale] (ADR-0005). Pure Kotlin on `java.text`
 * and `java.time`, so it is unit tested on the JVM in every language.
 *
 * Why not `"$value°"`? Because the locale changes more than the words:
 *
 * | | English | Türkçe | العربية |
 * |---|---|---|---|
 * | Percentage | `69%` | `%69` | `69%` |
 * | Decimal | `6.9 m/s` | `6,9 m/s` | `6.9 m/s` |
 * | Day | `Thursday` | `Perşembe` | `الخميس` |
 *
 * Arabic uses Western digits (0–9) by product decision (ADR-0005, point 7); see [forDisplay].
 */
class WeatherFormatter(locale: Locale, private val use24HourClock: Boolean) {
    private val locale: Locale = forDisplay(locale)
    private val integer = NumberFormat.getIntegerInstance(this.locale)
    private val oneDecimal = NumberFormat.getNumberInstance(this.locale).apply {
        minimumFractionDigits = 0
        maximumFractionDigits = 1
    }
    private val percent = NumberFormat.getPercentInstance(this.locale)
    private val hourFormat = DateTimeFormatter.ofPattern(if (use24HourClock) "HH:mm" else "h a", this.locale)

    /** `15°`. Whole degrees: decimals add noise, not information, to a weather glance. */
    fun temperature(value: Temperature, unit: TemperatureUnit): String =
        integer.format(value.valueIn(unit).roundToInt()) + DEGREE

    /** `25 km/h`, `6,9 m/s` in Turkish, `15 mph`. Metres per second keep one decimal. */
    fun windSpeed(value: WindSpeed, unit: WindSpeedUnit): String {
        val number = value.valueIn(unit)
        val text = when (unit) {
            WindSpeedUnit.MetersPerSecond -> oneDecimal.format(number)
            WindSpeedUnit.KilometersPerHour, WindSpeedUnit.MilesPerHour -> integer.format(number.roundToInt())
        }
        return "$text ${unitSymbol(unit)}"
    }

    /** `69%`, `%69` in Turkish. */
    fun percentage(value: Int): String = percent.format(value / PERCENT)

    /** `14:00` or `2 PM`, following the device's 24-hour setting. */
    fun hour(time: LocalDateTime): String = hourFormat.format(time)

    /** `Thursday`, `Perşembe`, `الخميس`. */
    fun dayName(day: DayOfWeek): String = day.getDisplayName(TextStyle.FULL, locale)

    /**
     * Unit symbols are international (SI), so they are not translated: `km/h` is `km/h`
     * in Turkish too. Arabic apps often keep them in Latin script as well.
     */
    private fun unitSymbol(unit: WindSpeedUnit) = when (unit) {
        WindSpeedUnit.KilometersPerHour -> "km/h"
        WindSpeedUnit.MetersPerSecond -> "m/s"
        WindSpeedUnit.MilesPerHour -> "mph"
    }

    companion object {
        private const val DEGREE = "°"
        private const val PERCENT = 100.0

        /**
         * Asks for Latin digits in Arabic with the Unicode extension `nu-latn`. Without
         * it, Java formats `15` as `١٥`. Other languages are unchanged.
         */
        fun forDisplay(locale: Locale): Locale = if (locale.language == "ar") {
            Locale.Builder().setLocale(locale).setUnicodeLocaleKeyword("nu", "latn").build()
        } else {
            locale
        }
    }
}
