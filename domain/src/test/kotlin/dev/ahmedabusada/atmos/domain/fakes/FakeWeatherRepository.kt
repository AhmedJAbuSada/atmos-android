package dev.ahmedabusada.atmos.domain.fakes

import dev.ahmedabusada.atmos.domain.common.AppResult
import dev.ahmedabusada.atmos.domain.location.Coordinates
import dev.ahmedabusada.atmos.domain.weather.Forecast
import dev.ahmedabusada.atmos.domain.weather.WeatherRepository

/** Configurable in-memory [WeatherRepository] that also counts network calls. */
class FakeWeatherRepository(
    var cached: Forecast? = null,
    var fetchResult: AppResult<Forecast> = AppResult.Success(TestData.forecast()),
) : WeatherRepository {
    var fetchCount = 0
        private set

    override suspend fun getCachedForecast(coordinates: Coordinates): Forecast? = cached

    override suspend fun fetchForecast(coordinates: Coordinates): AppResult<Forecast> {
        fetchCount++
        (fetchResult as? AppResult.Success)?.let { cached = it.data }
        return fetchResult
    }
}
