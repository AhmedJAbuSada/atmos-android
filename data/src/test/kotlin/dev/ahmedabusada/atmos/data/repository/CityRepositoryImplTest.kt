package dev.ahmedabusada.atmos.data.repository

import dev.ahmedabusada.atmos.data.remote.GeocodingApi
import dev.ahmedabusada.atmos.data.remote.RetryInterceptor
import dev.ahmedabusada.atmos.data.remote.createOkHttpClient
import dev.ahmedabusada.atmos.data.remote.createRetrofit
import dev.ahmedabusada.atmos.data.testing.Samples
import dev.ahmedabusada.atmos.data.testing.inMemoryDatabase
import dev.ahmedabusada.atmos.domain.common.AppResult
import dev.ahmedabusada.atmos.domain.common.DomainError
import dev.ahmedabusada.atmos.domain.location.City
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import retrofit2.create
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class CityRepositoryImplTest {
    private val server = MockWebServer().apply { start() }
    private val database = inMemoryDatabase()
    private val repository = CityRepositoryImpl(
        api = createRetrofit(
            server.url("/").toString(),
            createOkHttpClient("test", RetryInterceptor(baseDelayMillis = 0)),
        )
            .create<GeocodingApi>(),
        dao = database.savedCityDao(),
        clock = Clock.fixed(Instant.parse("2026-11-11T07:15:00Z"), ZoneOffset.UTC),
        io = Dispatchers.Unconfined,
    )

    @After
    fun tearDown() {
        server.close()
        database.close()
    }

    @Test
    fun `search returns cities in the requested language`() = runTest {
        server.enqueue(MockResponse(body = Samples.read("geocoding_istanbul_ar.json")))

        val cities = (repository.search("Istanbul", "ar") as AppResult.Success).data

        assertEquals("اسطنبول", cities.first().name)
        assertEquals("ar", server.takeRequest().url.queryParameter("language"))
    }

    @Test
    fun `search with no matches returns an empty list`() = runTest {
        server.enqueue(MockResponse(body = Samples.read("geocoding_empty.json")))

        assertEquals(AppResult.Success(emptyList<City>()), repository.search("zzqqxx", "en"))
    }

    @Test
    fun `search errors are mapped to domain errors`() = runTest {
        server.close()

        assertEquals(AppResult.Failure(DomainError.NoConnection), repository.search("ista", "en"))
    }

    @Test
    fun `a saved city survives the round trip through the database unchanged`() = runTest {
        server.enqueue(MockResponse(body = Samples.read("geocoding_istanbul_tr.json")))
        val istanbul = (repository.search("ista", "tr") as AppResult.Success).data.first()

        repository.save(istanbul)

        assertEquals(listOf(istanbul), repository.observeSavedCities().first())
    }

    @Test
    fun `a removed city is no longer saved`() = runTest {
        server.enqueue(MockResponse(body = Samples.read("geocoding_istanbul_en.json")))
        val city = (repository.search("ista", "en") as AppResult.Success).data.first()
        repository.save(city)

        repository.remove(city.id)

        assertEquals(emptyList<City>(), repository.observeSavedCities().first())
    }
}
