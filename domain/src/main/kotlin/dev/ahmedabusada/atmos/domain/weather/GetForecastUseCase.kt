package dev.ahmedabusada.atmos.domain.weather

import dev.ahmedabusada.atmos.domain.common.AppResult
import dev.ahmedabusada.atmos.domain.common.DomainError
import dev.ahmedabusada.atmos.domain.location.Coordinates
import javax.inject.Inject

/**
 * What the screen gets: the forecast, plus whether it is outdated because a refresh
 * failed. [refreshError] tells the UI *why* (for example, offline), so it can show
 * "Last updated 2 h ago" instead of an error screen (F-08).
 */
data class ForecastSnapshot(val forecast: Forecast, val refreshError: DomainError? = null) {
    val isOutdated: Boolean get() = refreshError != null
}

/**
 * Gets the forecast for a place, cache first (US-06, US-07).
 *
 * | Cache       | forceRefresh | Network  | Result                                  |
 * |-------------|--------------|----------|-----------------------------------------|
 * | fresh       | false        | not used | cached                                  |
 * | fresh       | true         | ok       | new                                     |
 * | stale/none  | any          | ok       | new                                     |
 * | stale/fresh | any          | fails    | cached, marked outdated with the error  |
 * | none        | any          | fails    | failure                                 |
 */
class GetForecastUseCase @Inject constructor(
    private val repository: WeatherRepository,
    private val freshness: ForecastFreshness,
) {
    suspend operator fun invoke(coordinates: Coordinates, forceRefresh: Boolean = false): AppResult<ForecastSnapshot> {
        val cached = repository.getCachedForecast(coordinates)
        if (!forceRefresh && cached != null && freshness.isFresh(cached)) {
            return AppResult.Success(ForecastSnapshot(cached))
        }

        return when (val fetched = repository.fetchForecast(coordinates)) {
            is AppResult.Success -> AppResult.Success(ForecastSnapshot(fetched.data))

            is AppResult.Failure ->
                if (cached != null) {
                    AppResult.Success(ForecastSnapshot(cached, refreshError = fetched.error))
                } else {
                    fetched
                }
        }
    }
}
