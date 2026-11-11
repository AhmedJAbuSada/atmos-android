package dev.ahmedabusada.atmos.domain.location

import dev.ahmedabusada.atmos.domain.common.AppResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Searches cities by name (US-04).
 *
 * Business rules live here, not in the UI: the query is trimmed, and fewer than
 * [MIN_QUERY_LENGTH] characters returns an empty list **without** a network call.
 * Debouncing (waiting 300 ms after typing stops) is a UI concern and lives in the
 * ViewModel.
 */
class SearchCitiesUseCase @Inject constructor(private val repository: CityRepository) {
    suspend operator fun invoke(query: String, languageTag: String): AppResult<List<City>> {
        val trimmed = query.trim()
        if (trimmed.length < MIN_QUERY_LENGTH) return AppResult.Success(emptyList())
        return repository.search(trimmed, languageTag)
    }

    companion object {
        const val MIN_QUERY_LENGTH = 2
    }
}

/** Outcome of [SaveCityUseCase]: the UI shows a different message for each. */
enum class SaveCityResult { Saved, AlreadySaved }

/** Saves a city to the favourites list. Saving the same city twice does nothing (US-05). */
class SaveCityUseCase @Inject constructor(private val repository: CityRepository) {
    suspend operator fun invoke(city: City): SaveCityResult {
        val alreadySaved = repository.observeSavedCities().first().any { it.id == city.id }
        if (alreadySaved) return SaveCityResult.AlreadySaved
        repository.save(city)
        return SaveCityResult.Saved
    }
}

/** Removes a saved city. [SaveCityUseCase] with the same city is the "Undo" (US-05). */
class RemoveCityUseCase @Inject constructor(private val repository: CityRepository) {
    suspend operator fun invoke(cityId: Long) = repository.remove(cityId)
}

/** The places list: current location first, then saved cities (US-05). */
class ObservePlacesUseCase @Inject constructor(private val repository: CityRepository) {
    operator fun invoke(): Flow<List<Place>> = repository.observeSavedCities().map { cities ->
        listOf(Place.CurrentLocation) + cities.map(Place::Saved)
    }
}
