package dev.ahmedabusada.atmos.domain.location

import dev.ahmedabusada.atmos.domain.common.AppResult
import kotlinx.coroutines.flow.Flow

/** The device location. Implemented with the fused location provider in :data. */
interface LocationRepository {
    /**
     * The current position, or [dev.ahmedabusada.atmos.domain.common.DomainError.LocationPermissionDenied]
     * / [dev.ahmedabusada.atmos.domain.common.DomainError.LocationUnavailable].
     */
    suspend fun getCurrentLocation(): AppResult<Coordinates>
}

/** City search (remote) and the saved-cities list (local). */
interface CityRepository {
    /** Searches by name. [languageTag] selects the language of the returned names. */
    suspend fun search(query: String, languageTag: String): AppResult<List<City>>

    /** Saved cities, oldest first. Emits again on every change. */
    fun observeSavedCities(): Flow<List<City>>

    /** Saves [city]. Saving a city with an id that is already saved replaces it. */
    suspend fun save(city: City)

    /** Removes the saved city with [cityId]. Does nothing if it is not saved. */
    suspend fun remove(cityId: Long)
}
