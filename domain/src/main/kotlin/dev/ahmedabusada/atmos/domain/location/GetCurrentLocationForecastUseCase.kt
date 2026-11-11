package dev.ahmedabusada.atmos.domain.location

import dev.ahmedabusada.atmos.domain.common.AppResult
import dev.ahmedabusada.atmos.domain.weather.ForecastSnapshot
import dev.ahmedabusada.atmos.domain.weather.GetForecastUseCase
import javax.inject.Inject

/**
 * The forecast where the user is now (US-01): locate the device, then get the forecast.
 *
 * Use cases can compose other use cases. A location error (for example, permission
 * denied) is returned as-is, so the UI can offer city search instead (F-02).
 */
class GetCurrentLocationForecastUseCase @Inject constructor(
    private val locationRepository: LocationRepository,
    private val getForecast: GetForecastUseCase,
) {
    suspend operator fun invoke(forceRefresh: Boolean = false): AppResult<ForecastSnapshot> =
        when (val location = locationRepository.getCurrentLocation()) {
            is AppResult.Success -> getForecast(location.data, forceRefresh)
            is AppResult.Failure -> location
        }
}
