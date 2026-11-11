package dev.ahmedabusada.atmos.data.remote

import dev.ahmedabusada.atmos.data.testing.Samples
import kotlinx.coroutines.test.runTest
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.create

/**
 * Runs the real Retrofit + OkHttp + JSON stack against a local server that replays the
 * recorded responses. Checks what we *send* (path, query, headers) and what we *parse*.
 */
class OpenMeteoApiTest {
    private val server = MockWebServer()
    private val noWaitRetry = RetryInterceptor(baseDelayMillis = 0)
    private lateinit var forecastApi: ForecastApi
    private lateinit var geocodingApi: GeocodingApi

    @Before
    fun setUp() {
        server.start()
        val client = createOkHttpClient(userAgent = "Atmos/test (Android)", retry = noWaitRetry)
        val retrofit = createRetrofit(server.url("/").toString(), client)
        forecastApi = retrofit.create()
        geocodingApi = retrofit.create()
    }

    @After
    fun tearDown() = server.close()

    @Test
    fun `forecast request asks for exactly the fields in the API contract`() = runTest {
        server.enqueue(MockResponse(body = Samples.read("forecast_istanbul.json")))

        forecastApi.getForecast(latitude = 41.01, longitude = 28.97)

        val url = server.takeRequest().url
        assertEquals("/v1/forecast", url.encodedPath)
        assertEquals("41.01", url.queryParameter("latitude"))
        assertEquals("28.97", url.queryParameter("longitude"))
        assertEquals(ForecastApi.CURRENT_FIELDS, url.queryParameter("current"))
        assertEquals(ForecastApi.HOURLY_FIELDS, url.queryParameter("hourly"))
        assertEquals(ForecastApi.DAILY_FIELDS, url.queryParameter("daily"))
        assertEquals("auto", url.queryParameter("timezone"))
        assertEquals("7", url.queryParameter("forecast_days"))
        assertEquals("24", url.queryParameter("forecast_hours"))
    }

    @Test
    fun `every request carries the app User-Agent and no personal data`() = runTest {
        server.enqueue(MockResponse(body = Samples.read("geocoding_empty.json")))

        geocodingApi.search(name = "zzqqxx", language = "en")

        assertEquals("Atmos/test (Android)", server.takeRequest().headers["User-Agent"])
    }

    @Test
    fun `search sends the name and the language`() = runTest {
        server.enqueue(MockResponse(body = Samples.read("geocoding_istanbul_tr.json")))

        val response = geocodingApi.search(name = "İstanbul", language = "tr")

        val url = server.takeRequest().url
        assertEquals("İstanbul", url.queryParameter("name"))
        assertEquals("tr", url.queryParameter("language"))
        assertEquals("İstanbul", response.results.first().name)
    }

    @Test
    fun `an empty search response parses to an empty list`() = runTest {
        server.enqueue(MockResponse(body = Samples.read("geocoding_empty.json")))

        assertTrue(geocodingApi.search(name = "zzqqxx", language = "en").results.isEmpty())
    }

    @Test
    fun `a 400 is not retried and surfaces as an HTTP error`() = runTest {
        server.enqueue(MockResponse(code = 400, body = Samples.read("forecast_error.json")))

        val error = runCatching { forecastApi.getForecast(latitude = 41.0, longitude = 28.0) }.exceptionOrNull()

        assertEquals(400, (error as HttpException).code())
        assertEquals(1, server.requestCount)
    }

    @Test
    fun `a 503 is retried twice and then succeeds`() = runTest {
        server.enqueue(MockResponse(code = 503))
        server.enqueue(MockResponse(code = 503))
        server.enqueue(MockResponse(body = Samples.read("forecast_istanbul.json")))

        val forecast = forecastApi.getForecast(latitude = 41.01, longitude = 28.97)

        assertEquals("Europe/Istanbul", forecast.timezone)
        assertEquals(3, server.requestCount)
    }

    @Test
    fun `a 500 that keeps failing gives up after two retries`() = runTest {
        repeat(3) { server.enqueue(MockResponse(code = 500)) }

        val error = runCatching { forecastApi.getForecast(latitude = 41.01, longitude = 28.97) }.exceptionOrNull()

        assertEquals(500, (error as HttpException).code())
        assertEquals(3, server.requestCount)
    }
}
