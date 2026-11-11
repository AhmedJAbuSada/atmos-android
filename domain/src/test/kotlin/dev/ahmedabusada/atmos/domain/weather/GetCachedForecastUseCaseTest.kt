package dev.ahmedabusada.atmos.domain.weather

import dev.ahmedabusada.atmos.domain.fakes.FakeWeatherRepository
import dev.ahmedabusada.atmos.domain.fakes.TestData
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class GetCachedForecastUseCaseTest {
    private val repository = FakeWeatherRepository()
    private val getCached = GetCachedForecastUseCase(repository)

    @Test
    fun `returns the cached forecast without a network call`() = runTest {
        repository.cached = TestData.forecast()

        assertEquals(TestData.forecast(), getCached(TestData.ISTANBUL))
        assertEquals(0, repository.fetchCount)
    }

    @Test
    fun `returns null when nothing is cached`() = runTest {
        assertNull(getCached(TestData.ISTANBUL))
    }
}
