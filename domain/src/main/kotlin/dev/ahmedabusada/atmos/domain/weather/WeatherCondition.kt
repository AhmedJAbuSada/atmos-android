package dev.ahmedabusada.atmos.domain.weather

/**
 * What the sky is doing, independent of any provider or language.
 *
 * The UI maps each value to a localized label and an icon; the domain only knows the
 * meaning. [fromWmoCode] translates the international WMO code that Open-Meteo returns
 * (API_CONTRACT §3).
 */
enum class WeatherCondition {
    Clear,
    MainlyClear,
    PartlyCloudy,
    Overcast,
    Fog,
    Drizzle,
    FreezingDrizzle,
    Rain,
    FreezingRain,
    Snow,
    RainShowers,
    SnowShowers,
    Thunderstorm,
    ThunderstormWithHail,

    /** A code we do not know. Shown with a neutral icon; it must never crash the app. */
    Unknown,
    ;

    companion object {
        fun fromWmoCode(code: Int?): WeatherCondition = when (code) {
            0 -> Clear
            1 -> MainlyClear
            2 -> PartlyCloudy
            3 -> Overcast
            45, 48 -> Fog
            51, 53, 55 -> Drizzle
            56, 57 -> FreezingDrizzle
            61, 63, 65 -> Rain
            66, 67 -> FreezingRain
            71, 73, 75, 77 -> Snow
            80, 81, 82 -> RainShowers
            85, 86 -> SnowShowers
            95 -> Thunderstorm
            96, 99 -> ThunderstormWithHail
            else -> Unknown
        }
    }
}
