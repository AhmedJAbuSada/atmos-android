package dev.ahmedabusada.atmos.domain.location

import dev.ahmedabusada.atmos.domain.common.AppResult
import dev.ahmedabusada.atmos.domain.common.DomainError
import dev.ahmedabusada.atmos.domain.fakes.FakeLocationRepository
import dev.ahmedabusada.atmos.domain.fakes.FakeWeatherRepository
import dev.ahmedabusada.atmos.domain.fakes.TestData
import dev.ahmedabusada.atmos.domain.weather.ForecastFreshness
import dev.ahmedabusada.atmos.domain.weather.ForecastSnapshot
import dev.ahmedabusada.atmos.domain.weather.GetForecastUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Clock
import java.time.ZoneOffset

class GetCurrentLocationForecastUseCaseTest {
    private val location = FakeLocationRepository()
    private val weather = FakeWeatherRepository()
    private val useCase = GetCurrentLocationForecastUseCase(
        locationRepository = location,
        getForecast = GetForecastUseCase(weather, ForecastFreshness(Clock.fixed(TestData.NOW, ZoneOffset.UTC))),
    )

    @Test
    fun `given a location, then returns its forecast`() = runTest {
        val forecast = TestData.forecast()
        weather.fetchResult = AppResult.Success(forecast)

        assertEquals(AppResult.Success(ForecastSnapshot(forecast)), useCase())
    }

    @Test
    fun `given permission is denied, then returns that error without fetching weather`() = runTest {
        location.result = AppResult.Failure(DomainError.LocationPermissionDenied)

        assertEquals(AppResult.Failure(DomainError.LocationPermissionDenied), useCase())
        assertEquals(0, weather.fetchCount)
    }
}
