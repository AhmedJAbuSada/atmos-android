package dev.ahmedabusada.atmos.domain.weather

import dev.ahmedabusada.atmos.domain.location.Coordinates
import javax.inject.Inject

/**
 * The last known forecast for a place, **without** a network call. The Cities list uses it
 * to show a temperature next to each saved city (wireframe 2) without downloading the
 * forecast for every city each time the list opens.
 */
class GetCachedForecastUseCase @Inject constructor(private val repository: WeatherRepository) {
    suspend operator fun invoke(coordinates: Coordinates): Forecast? = repository.getCachedForecast(coordinates)
}
