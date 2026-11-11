package dev.ahmedabusada.atmos.domain.weather

import dev.ahmedabusada.atmos.domain.common.AppResult
import dev.ahmedabusada.atmos.domain.common.DomainError
import dev.ahmedabusada.atmos.domain.fakes.FakeWeatherRepository
import dev.ahmedabusada.atmos.domain.fakes.TestData
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Clock
import java.time.Duration
import java.time.ZoneOffset

/** One test per row of the table in [GetForecastUseCase]'s documentation. */
class GetForecastUseCaseTest {
    private val fresh = TestData.forecast(fetchedAt = TestData.NOW.minus(Duration.ofMinutes(5)))
    private val stale = TestData.forecast(fetchedAt = TestData.NOW.minus(Duration.ofHours(2)))
    private val downloaded = TestData.forecast(fetchedAt = TestData.NOW)

    private val repository = FakeWeatherRepository()
    private val getForecast = GetForecastUseCase(
        repository = repository,
        freshness = ForecastFreshness(Clock.fixed(TestData.NOW, ZoneOffset.UTC)),
    )

    @Test
    fun `given fresh cache, when loading, then returns it without a network call`() = runTest {
        repository.cached = fresh

        val result = getForecast(TestData.ISTANBUL)

        assertEquals(AppResult.Success(ForecastSnapshot(fresh)), result)
        assertEquals(0, repository.fetchCount)
    }

    @Test
    fun `given fresh cache, when pulling to refresh, then downloads anyway`() = runTest {
        repository.cached = fresh
        repository.fetchResult = AppResult.Success(downloaded)

        val result = getForecast(TestData.ISTANBUL, forceRefresh = true)

        assertEquals(AppResult.Success(ForecastSnapshot(downloaded)), result)
        assertEquals(1, repository.fetchCount)
    }

    @Test
    fun `given stale cache, when loading, then downloads a new forecast`() = runTest {
        repository.cached = stale
        repository.fetchResult = AppResult.Success(downloaded)

        val result = getForecast(TestData.ISTANBUL)

        assertEquals(AppResult.Success(ForecastSnapshot(downloaded)), result)
        assertEquals(1, repository.fetchCount)
    }

    @Test
    fun `given no cache, when loading, then downloads a new forecast`() = runTest {
        repository.fetchResult = AppResult.Success(downloaded)

        val result = getForecast(TestData.ISTANBUL)

        assertEquals(AppResult.Success(ForecastSnapshot(downloaded)), result)
    }

    @Test
    fun `given stale cache and no connection, then returns the cache marked outdated`() = runTest {
        repository.cached = stale
        repository.fetchResult = AppResult.Failure(DomainError.NoConnection)

        val snapshot = (getForecast(TestData.ISTANBUL) as AppResult.Success).data

        assertEquals(stale, snapshot.forecast)
        assertTrue(snapshot.isOutdated)
        assertEquals(DomainError.NoConnection, snapshot.refreshError)
    }

    @Test
    fun `given fresh cache and a failed pull to refresh, then keeps the cache marked outdated`() = runTest {
        repository.cached = fresh
        repository.fetchResult = AppResult.Failure(DomainError.Timeout)

        val snapshot = (getForecast(TestData.ISTANBUL, forceRefresh = true) as AppResult.Success).data

        assertEquals(fresh, snapshot.forecast)
        assertEquals(DomainError.Timeout, snapshot.refreshError)
    }

    @Test
    fun `given no cache and no connection, then fails with the error`() = runTest {
        repository.fetchResult = AppResult.Failure(DomainError.NoConnection)

        assertEquals(AppResult.Failure(DomainError.NoConnection), getForecast(TestData.ISTANBUL))
    }

    @Test
    fun `a downloaded forecast is never marked outdated`() = runTest {
        val snapshot = (getForecast(TestData.ISTANBUL) as AppResult.Success).data

        assertFalse(snapshot.isOutdated)
    }
}
