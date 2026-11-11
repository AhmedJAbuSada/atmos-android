package dev.ahmedabusada.atmos.domain.settings

import org.junit.Assert.assertEquals
import org.junit.Test

class UnitsTest {
    private val delta = 0.001

    @Test
    fun `celsius is returned unchanged`() {
        assertEquals(14.9, Temperature(14.9).valueIn(TemperatureUnit.Celsius), delta)
    }

    @Test
    fun `celsius converts to fahrenheit`() {
        assertEquals(32.0, Temperature(0.0).valueIn(TemperatureUnit.Fahrenheit), delta)
        assertEquals(212.0, Temperature(100.0).valueIn(TemperatureUnit.Fahrenheit), delta)
        assertEquals(-40.0, Temperature(-40.0).valueIn(TemperatureUnit.Fahrenheit), delta)
    }

    @Test
    fun `wind speed converts to every unit`() {
        val wind = WindSpeed(36.0)

        assertEquals(36.0, wind.valueIn(WindSpeedUnit.KilometersPerHour), delta)
        assertEquals(10.0, wind.valueIn(WindSpeedUnit.MetersPerSecond), delta)
        assertEquals(22.369, wind.valueIn(WindSpeedUnit.MilesPerHour), delta)
    }
}
