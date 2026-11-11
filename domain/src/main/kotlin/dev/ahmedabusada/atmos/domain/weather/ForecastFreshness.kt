package dev.ahmedabusada.atmos.domain.weather

import java.time.Clock
import java.time.Duration
import java.time.Instant
import javax.inject.Inject

/**
 * Decides whether cached data is still good enough to show without a download (F-07).
 *
 * The [Clock] is injected instead of calling `Instant.now()`, so tests can move time
 * forward without waiting.
 */
class ForecastFreshness @Inject constructor(private val clock: Clock) {
    fun isFresh(forecast: Forecast): Boolean = age(forecast) < MAX_AGE

    fun age(forecast: Forecast): Duration = Duration.between(forecast.fetchedAt, Instant.now(clock))

    companion object {
        /** Data younger than this is shown as-is, with no network call (US-06). */
        val MAX_AGE: Duration = Duration.ofMinutes(30)
    }
}
