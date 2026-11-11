package dev.ahmedabusada.atmos.domain.weather

import org.junit.Assert.assertEquals
import org.junit.Test

class WeatherConditionTest {
    @Test
    fun `every documented WMO code maps to a known condition`() {
        val expected = mapOf(
            0 to WeatherCondition.Clear,
            1 to WeatherCondition.MainlyClear,
            2 to WeatherCondition.PartlyCloudy,
            3 to WeatherCondition.Overcast,
            45 to WeatherCondition.Fog,
            48 to WeatherCondition.Fog,
            51 to WeatherCondition.Drizzle,
            53 to WeatherCondition.Drizzle,
            55 to WeatherCondition.Drizzle,
            56 to WeatherCondition.FreezingDrizzle,
            57 to WeatherCondition.FreezingDrizzle,
            61 to WeatherCondition.Rain,
            63 to WeatherCondition.Rain,
            65 to WeatherCondition.Rain,
            66 to WeatherCondition.FreezingRain,
            67 to WeatherCondition.FreezingRain,
            71 to WeatherCondition.Snow,
            73 to WeatherCondition.Snow,
            75 to WeatherCondition.Snow,
            77 to WeatherCondition.Snow,
            80 to WeatherCondition.RainShowers,
            81 to WeatherCondition.RainShowers,
            82 to WeatherCondition.RainShowers,
            85 to WeatherCondition.SnowShowers,
            86 to WeatherCondition.SnowShowers,
            95 to WeatherCondition.Thunderstorm,
            96 to WeatherCondition.ThunderstormWithHail,
            99 to WeatherCondition.ThunderstormWithHail,
        )

        expected.forEach { (code, condition) ->
            assertEquals("WMO code $code", condition, WeatherCondition.fromWmoCode(code))
        }
    }

    @Test
    fun `an unknown or missing code maps to Unknown instead of crashing`() {
        assertEquals(WeatherCondition.Unknown, WeatherCondition.fromWmoCode(4))
        assertEquals(WeatherCondition.Unknown, WeatherCondition.fromWmoCode(-1))
        assertEquals(WeatherCondition.Unknown, WeatherCondition.fromWmoCode(null))
    }
}
