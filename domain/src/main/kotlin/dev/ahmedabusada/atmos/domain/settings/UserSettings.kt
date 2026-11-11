package dev.ahmedabusada.atmos.domain.settings

/** The app language (ADR-0005). [System] follows the device; the others override it. */
enum class AppLanguage(val tag: String?) {
    System(null),
    English("en"),
    Turkish("tr"),
    Arabic("ar"),
}

/** Light or dark appearance. [System] follows the device setting. */
enum class AppTheme { System, Light, Dark }

/** Everything the user can change in Settings (F-09). Defaults are the first-run values. */
data class UserSettings(
    val temperatureUnit: TemperatureUnit = TemperatureUnit.Celsius,
    val windSpeedUnit: WindSpeedUnit = WindSpeedUnit.KilometersPerHour,
    val language: AppLanguage = AppLanguage.System,
    val theme: AppTheme = AppTheme.System,
)
