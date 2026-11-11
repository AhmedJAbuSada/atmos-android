package dev.ahmedabusada.atmos.domain.location

import org.junit.Assert.assertThrows
import org.junit.Test

class CoordinatesTest {
    @Test
    fun `accepts the edges of the valid range`() {
        Coordinates(latitude = 90.0, longitude = 180.0)
        Coordinates(latitude = -90.0, longitude = -180.0)
    }

    @Test
    fun `rejects an impossible latitude`() {
        // The API answers 400 for latitude 999 (docs/api/samples/forecast_error.json);
        // we refuse to build such a value in the first place.
        assertThrows(IllegalArgumentException::class.java) { Coordinates(latitude = 999.0, longitude = 28.0) }
    }

    @Test
    fun `rejects an impossible longitude`() {
        assertThrows(IllegalArgumentException::class.java) { Coordinates(latitude = 41.0, longitude = -181.0) }
    }
}
