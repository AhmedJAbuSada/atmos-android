package dev.ahmedabusada.atmos.feature.cities

import dev.ahmedabusada.atmos.domain.common.DomainError
import dev.ahmedabusada.atmos.domain.location.City
import dev.ahmedabusada.atmos.domain.location.Place
import dev.ahmedabusada.atmos.domain.settings.Temperature
import dev.ahmedabusada.atmos.domain.settings.TemperatureUnit
import dev.ahmedabusada.atmos.domain.weather.WeatherCondition

/** The Cities screen (US-04, US-05): a search field on top of the saved places. */
data class CitiesUiState(
    val query: String = "",
    val search: SearchState = SearchState.Idle,
    val places: List<PlaceItem> = emptyList(),
    val temperatureUnit: TemperatureUnit = TemperatureUnit.Celsius,
)

/** The search area below the field. [Idle] means the saved list is shown instead. */
sealed interface SearchState {
    data object Idle : SearchState

    data object Searching : SearchState

    data class Results(val cities: List<City>) : SearchState

    data object NoResults : SearchState

    data class Failed(val error: DomainError) : SearchState
}

/** A row in the places list, with its last known weather when there is one. */
data class PlaceItem(val place: Place, val temperature: Temperature? = null, val condition: WeatherCondition? = null)

sealed interface CitiesUiEvent {
    data class QueryChanged(val query: String) : CitiesUiEvent

    data object ClearQuery : CitiesUiEvent

    data object RetrySearch : CitiesUiEvent

    data class Add(val city: City) : CitiesUiEvent

    data class Remove(val city: City) : CitiesUiEvent

    data class Select(val place: Place) : CitiesUiEvent

    data object Back : CitiesUiEvent
}
