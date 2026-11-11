package dev.ahmedabusada.atmos.data.remote

import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import mockwebserver3.SocketEffect
import okhttp3.OkHttpClient
import okhttp3.Request
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class RetryInterceptorTest {
    private val server = MockWebServer()
    private val delays = mutableListOf<Long>()

    @Before
    fun setUp() = server.start()

    @After
    fun tearDown() = server.close()

    private fun call(): Int {
        val client = OkHttpClient.Builder()
            .retryOnConnectionFailure(false)
            .addInterceptor(RetryInterceptor(maxRetries = 2, baseDelayMillis = 1_000, sleep = { delays += it }))
            .build()
        return client.newCall(Request(server.url("/"))).execute().use { it.code }
    }

    @Test
    fun `back-off doubles between attempts`() {
        server.enqueue(MockResponse(code = 500))
        server.enqueue(MockResponse(code = 500))
        server.enqueue(MockResponse(code = 200))

        assertEquals(200, call())
        assertEquals(listOf(1_000L, 2_000L), delays)
    }

    @Test
    fun `a network failure is retried`() {
        server.enqueue(MockResponse.Builder().onRequestStart(SocketEffect.CloseSocket()).build())
        server.enqueue(MockResponse(code = 200))

        assertEquals(200, call())
        assertEquals(listOf(1_000L), delays)
    }

    @Test
    fun `a success is returned at once`() {
        server.enqueue(MockResponse(code = 200))

        assertEquals(200, call())
        assertEquals(emptyList<Long>(), delays)
    }
}
