package dev.ahmedabusada.atmos.data.repository

import dev.ahmedabusada.atmos.data.local.ForecastCacheEntity
import dev.ahmedabusada.atmos.data.local.cacheKey
import dev.ahmedabusada.atmos.data.remote.ForecastApi
import dev.ahmedabusada.atmos.data.remote.RetryInterceptor
import dev.ahmedabusada.atmos.data.remote.createOkHttpClient
import dev.ahmedabusada.atmos.data.remote.createRetrofit
import dev.ahmedabusada.atmos.data.testing.Samples
import dev.ahmedabusada.atmos.data.testing.inMemoryDatabase
import dev.ahmedabusada.atmos.domain.common.AppResult
import dev.ahmedabusada.atmos.domain.common.DomainError
import dev.ahmedabusada.atmos.domain.location.Coordinates
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import okhttp3.OkHttpClient
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import retrofit2.create
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import java.util.concurrent.TimeUnit

/** The real repository, real Room, real Retrofit: only the server is local. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class WeatherRepositoryImplTest {
    private val now = Instant.parse("2026-11-11T07:15:00Z")
    private val istanbul = Coordinates(41.01, 28.97)
    private val server = MockWebServer().apply { start() }
    private val database = inMemoryDatabase()

    private fun repository(client: OkHttpClient = createOkHttpClient("test", RetryInterceptor(baseDelayMillis = 0))) =
        WeatherRepositoryImpl(
            api = createRetrofit(server.url("/").toString(), client).create<ForecastApi>(),
            cache = database.forecastCacheDao(),
            clock = Clock.fixed(now, ZoneOffset.UTC),
            io = Dispatchers.Unconfined,
        )

    @After
    fun tearDown() {
        server.close()
        database.close()
    }

    @Test
    fun `a successful fetch returns the forecast and caches it`() = runTest {
        server.enqueue(MockResponse(body = Samples.read("forecast_istanbul.json")))
        val repository = repository()

        val fetched = repository.fetchForecast(istanbul) as AppResult.Success
        val cached = repository.getCachedForecast(istanbul)

        assertEquals(now, fetched.data.fetchedAt)
        assertEquals(fetched.data, cached)
    }

    @Test
    fun `given nothing was fetched, then there is no cache`() = runTest {
        assertNull(repository().getCachedForecast(istanbul))
    }

    @Test
    fun `a server error is mapped and nothing is cached`() = runTest {
        repeat(3) { server.enqueue(MockResponse(code = 503)) }
        val repository = repository()

        assertEquals(AppResult.Failure(DomainError.Server(503)), repository.fetchForecast(istanbul))
        assertNull(repository.getCachedForecast(istanbul))
    }

    @Test
    fun `a response without current conditions is rejected and not cached`() = runTest {
        server.enqueue(MockResponse(body = """{"timezone":"Europe/Istanbul"}"""))
        val repository = repository()

        val result = repository.fetchForecast(istanbul)

        assertTrue((result as AppResult.Failure).error is DomainError.Unknown)
        assertNull(repository.getCachedForecast(istanbul))
    }

    @Test
    fun `a slow server is reported as a timeout`() = runTest {
        server.enqueue(MockResponse.Builder().headersDelay(2, TimeUnit.SECONDS).body("{}").build())
        val impatient = OkHttpClient.Builder().readTimeout(100, TimeUnit.MILLISECONDS).build()

        assertEquals(AppResult.Failure(DomainError.Timeout), repository(impatient).fetchForecast(istanbul))
    }

    @Test
    fun `an unreachable server is reported as no connection`() = runTest {
        server.close()

        assertEquals(AppResult.Failure(DomainError.NoConnection), repository().fetchForecast(istanbul))
    }

    @Test
    fun `a cache entry that no longer parses is treated as no cache`() = runTest {
        database.forecastCacheDao().upsert(ForecastCacheEntity(istanbul.cacheKey(), json = "not json", 0))

        assertNull(repository().getCachedForecast(istanbul))
    }

    @Test
    fun `a nearby position reuses the cache of the same place`() = runTest {
        server.enqueue(MockResponse(body = Samples.read("forecast_istanbul.json")))
        val repository = repository()
        repository.fetchForecast(istanbul)

        assertNotNull(repository.getCachedForecast(Coordinates(41.0149, 28.9701)))
    }
}
