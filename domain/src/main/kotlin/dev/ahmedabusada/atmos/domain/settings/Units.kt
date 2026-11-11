package dev.ahmedabusada.atmos.domain.settings

/** Unit the user wants temperatures shown in (F-09). */
enum class TemperatureUnit { Celsius, Fahrenheit }

/** Unit the user wants wind speed shown in (F-09). */
enum class WindSpeedUnit { KilometersPerHour, MetersPerSecond, MilesPerHour }

/**
 * A temperature. Stored in Celsius, the unit the API returns (API_CONTRACT §1), and
 * converted only when shown. A value class costs nothing at runtime but stops you from
 * passing a wind speed where a temperature is expected.
 */
@JvmInline
value class Temperature(val celsius: Double) {
    fun valueIn(unit: TemperatureUnit): Double = when (unit) {
        TemperatureUnit.Celsius -> celsius
        TemperatureUnit.Fahrenheit -> celsius * 9.0 / 5.0 + 32.0
    }
}

/** A wind speed, stored in km/h as the API returns it. */
@JvmInline
value class WindSpeed(val kilometersPerHour: Double) {
    fun valueIn(unit: WindSpeedUnit): Double = when (unit) {
        WindSpeedUnit.KilometersPerHour -> kilometersPerHour
        WindSpeedUnit.MetersPerSecond -> kilometersPerHour / KMH_PER_MS
        WindSpeedUnit.MilesPerHour -> kilometersPerHour / KMH_PER_MPH
    }

    private companion object {
        const val KMH_PER_MS = 3.6
        const val KMH_PER_MPH = 1.609344
    }
}
