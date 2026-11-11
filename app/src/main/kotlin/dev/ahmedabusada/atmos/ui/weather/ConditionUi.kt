package dev.ahmedabusada.atmos.ui.weather

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import dev.ahmedabusada.atmos.R
import dev.ahmedabusada.atmos.domain.weather.WeatherCondition

/**
 * How a [WeatherCondition] looks: a label and an icon. The domain knows *what* the weather
 * is; only the presentation layer knows how to say it in three languages and draw it.
 */
@StringRes
fun WeatherCondition.labelRes(): Int = when (this) {
    WeatherCondition.Clear -> R.string.condition_clear
    WeatherCondition.MainlyClear -> R.string.condition_mainly_clear
    WeatherCondition.PartlyCloudy -> R.string.condition_partly_cloudy
    WeatherCondition.Overcast -> R.string.condition_overcast
    WeatherCondition.Fog -> R.string.condition_fog
    WeatherCondition.Drizzle -> R.string.condition_drizzle
    WeatherCondition.FreezingDrizzle -> R.string.condition_freezing_drizzle
    WeatherCondition.Rain -> R.string.condition_rain
    WeatherCondition.FreezingRain -> R.string.condition_freezing_rain
    WeatherCondition.Snow -> R.string.condition_snow
    WeatherCondition.RainShowers -> R.string.condition_rain_showers
    WeatherCondition.SnowShowers -> R.string.condition_snow_showers
    WeatherCondition.Thunderstorm -> R.string.condition_thunderstorm
    WeatherCondition.ThunderstormWithHail -> R.string.condition_thunderstorm_hail
    WeatherCondition.Unknown -> R.string.condition_unknown
}

/** At night, a clear sky shows a moon, not a sun (US-01). */
@DrawableRes
fun WeatherCondition.iconRes(isDay: Boolean = true): Int = when (this) {
    WeatherCondition.Clear ->
        if (isDay) R.drawable.ic_weather_clear_day else R.drawable.ic_weather_clear_night

    WeatherCondition.MainlyClear, WeatherCondition.PartlyCloudy ->
        if (isDay) R.drawable.ic_weather_partly_cloudy_day else R.drawable.ic_weather_partly_cloudy_night

    WeatherCondition.Overcast -> R.drawable.ic_weather_cloudy

    WeatherCondition.Fog -> R.drawable.ic_weather_fog

    WeatherCondition.Drizzle, WeatherCondition.FreezingDrizzle -> R.drawable.ic_weather_drizzle

    WeatherCondition.Rain, WeatherCondition.FreezingRain, WeatherCondition.RainShowers -> R.drawable.ic_weather_rain

    WeatherCondition.Snow, WeatherCondition.SnowShowers -> R.drawable.ic_weather_snow

    WeatherCondition.Thunderstorm, WeatherCondition.ThunderstormWithHail -> R.drawable.ic_weather_thunderstorm

    WeatherCondition.Unknown -> R.drawable.ic_weather_unknown
}
