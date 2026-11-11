package dev.ahmedabusada.atmos.domain.common

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Test

class AppResultTest {
    @Test
    fun `map transforms a success`() {
        val result: AppResult<Int> = AppResult.Success(2)

        assertEquals(AppResult.Success(4), result.map { it * 2 })
    }

    @Test
    fun `map keeps a failure unchanged`() {
        val failure: AppResult<Int> = AppResult.Failure(DomainError.Timeout)

        assertSame(failure, failure.map { it * 2 })
    }

    @Test
    fun `getOrNull and errorOrNull read the right side`() {
        val success: AppResult<String> = AppResult.Success("ok")
        val failure: AppResult<String> = AppResult.Failure(DomainError.NotFound)

        assertEquals("ok", success.getOrNull())
        assertNull(success.errorOrNull())
        assertNull(failure.getOrNull())
        assertEquals(DomainError.NotFound, failure.errorOrNull())
    }
}
