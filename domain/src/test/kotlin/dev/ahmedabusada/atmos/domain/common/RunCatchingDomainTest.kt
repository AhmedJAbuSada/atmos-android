package dev.ahmedabusada.atmos.domain.common

import kotlinx.coroutines.CancellationException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

class RunCatchingDomainTest {
    @Test
    fun `given the block succeeds, then returns success`() {
        assertEquals(AppResult.Success(42), runCatchingDomain { 42 })
    }

    @Test
    fun `given the block throws, then maps the exception to a domain error`() {
        val result = runCatchingDomain(mapError = { DomainError.NoConnection }) {
            throw IOException("offline")
        }

        assertEquals(AppResult.Failure(DomainError.NoConnection), result)
    }

    @Test
    fun `given no mapper, then wraps the exception as unknown`() {
        val boom = IllegalStateException("boom")

        val result = runCatchingDomain { throw boom }

        assertEquals(AppResult.Failure(DomainError.Unknown(boom)), result)
    }

    @Test
    fun `given the coroutine is cancelled, then rethrows instead of returning a failure`() {
        var rethrown = false
        try {
            runCatchingDomain { throw CancellationException("screen closed") }
        } catch (expected: CancellationException) {
            rethrown = true
        }

        assertTrue("CancellationException must never be swallowed", rethrown)
    }
}
