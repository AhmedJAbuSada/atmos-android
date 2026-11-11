package dev.ahmedabusada.atmos.core

import dev.ahmedabusada.atmos.domain.location.City
import dev.ahmedabusada.atmos.domain.location.Place
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Which place the Home screen shows. Cities writes it, Home reads it: a tiny piece of
 * shared **UI state**, so it lives in :app, not in the domain. It is in memory on purpose:
 * the app always opens on the current location.
 */
@Singleton
class SelectedPlaceStore @Inject constructor() {
    private val selected = MutableStateFlow<Place>(Place.CurrentLocation)

    val place: StateFlow<Place> = selected.asStateFlow()

    fun select(place: Place) {
        selected.value = place
    }

    /** If the removed city was on screen, fall back to the current location. */
    fun onCityRemoved(city: City) = selected.update { current ->
        if (current is Place.Saved && current.city.id == city.id) Place.CurrentLocation else current
    }
}
