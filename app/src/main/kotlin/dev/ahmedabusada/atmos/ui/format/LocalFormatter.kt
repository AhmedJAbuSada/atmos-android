package dev.ahmedabusada.atmos.ui.format

import android.text.format.DateFormat
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext

/**
 * A [WeatherFormatter] for the language the app is shown in right now.
 *
 * The locale comes from the Compose configuration, not `Locale.getDefault()`: with the
 * per-app language setting (ADR-0005) the app can be in Arabic while the phone is in
 * English, and the configuration is what reflects that.
 */
@Composable
fun rememberWeatherFormatter(): WeatherFormatter {
    val locale = LocalConfiguration.current.locales[0]
    val use24Hour = DateFormat.is24HourFormat(LocalContext.current)
    return remember(locale, use24Hour) { WeatherFormatter(locale, use24Hour) }
}
