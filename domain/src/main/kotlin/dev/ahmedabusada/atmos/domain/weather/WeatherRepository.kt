package dev.ahmedabusada.atmos.domain.weather

import dev.ahmedabusada.atmos.domain.common.AppResult
import dev.ahmedabusada.atmos.domain.location.Coordinates

/**
 * Source of forecasts. The interface lives in the domain; the implementation (Retrofit +
 * Room) lives in :data. That inversion is what keeps the domain free of frameworks.
 *
 * The repository only *stores and fetches*. Deciding *when* to fetch is a business rule,
 * so it lives in [GetForecastUseCase].
 */
interface WeatherRepository {
    /** The last saved forecast for [coordinates], or `null` if there is none. */
    suspend fun getCachedForecast(coordinates: Coordinates): Forecast?

    /** Downloads a new forecast and saves it to the cache. */
    suspend fun fetchForecast(coordinates: Coordinates): AppResult<Forecast>
}
