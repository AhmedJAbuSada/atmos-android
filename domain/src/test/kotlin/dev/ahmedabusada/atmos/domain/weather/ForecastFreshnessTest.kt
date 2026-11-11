package dev.ahmedabusada.atmos.domain.weather

import dev.ahmedabusada.atmos.domain.fakes.TestData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Clock
import java.time.Duration
import java.time.ZoneOffset

class ForecastFreshnessTest {
    private val freshness = ForecastFreshness(Clock.fixed(TestData.NOW, ZoneOffset.UTC))

    @Test
    fun `data fetched 29 minutes ago is fresh`() {
        assertTrue(freshness.isFresh(TestData.forecast(fetchedAt = TestData.NOW.minus(Duration.ofMinutes(29)))))
    }

    @Test
    fun `data fetched exactly 30 minutes ago is stale`() {
        assertFalse(freshness.isFresh(TestData.forecast(fetchedAt = TestData.NOW.minus(Duration.ofMinutes(30)))))
    }

    @Test
    fun `age is measured from the fetch time`() {
        val forecast = TestData.forecast(fetchedAt = TestData.NOW.minus(Duration.ofHours(2)))

        assertEquals(Duration.ofHours(2), freshness.age(forecast))
    }
}
