package dev.ahmedabusada.atmos.data.remote

import dev.ahmedabusada.atmos.domain.common.DomainError
import kotlinx.serialization.SerializationException
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class ErrorMappingTest {
    private fun http(code: Int) = HttpException(Response.error<Unit>(code, "".toResponseBody()))

    @Test
    fun `timeout maps to Timeout, even though it is an IOException`() {
        assertEquals(DomainError.Timeout, SocketTimeoutException().toDomainError())
    }

    @Test
    fun `no network maps to NoConnection`() {
        assertEquals(DomainError.NoConnection, UnknownHostException("api.open-meteo.com").toDomainError())
    }

    @Test
    fun `HTTP errors keep their status code`() {
        assertEquals(DomainError.Server(400), http(400).toDomainError())
        assertEquals(DomainError.Server(503), http(503).toDomainError())
        assertEquals(DomainError.NotFound, http(404).toDomainError())
    }

    @Test
    fun `a broken or incomplete response maps to Unknown`() {
        assertTrue(SerializationException("bad json").toDomainError() is DomainError.Unknown)
        assertTrue(InvalidResponseException("no current").toDomainError() is DomainError.Unknown)
    }
}
