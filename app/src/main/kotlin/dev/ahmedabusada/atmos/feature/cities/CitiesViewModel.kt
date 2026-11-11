package dev.ahmedabusada.atmos.feature.cities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.ahmedabusada.atmos.core.CurrentLanguage
import dev.ahmedabusada.atmos.core.SelectedPlaceStore
import dev.ahmedabusada.atmos.domain.common.AppResult
import dev.ahmedabusada.atmos.domain.location.City
import dev.ahmedabusada.atmos.domain.location.ObservePlacesUseCase
import dev.ahmedabusada.atmos.domain.location.Place
import dev.ahmedabusada.atmos.domain.location.RemoveCityUseCase
import dev.ahmedabusada.atmos.domain.location.SaveCityResult
import dev.ahmedabusada.atmos.domain.location.SaveCityUseCase
import dev.ahmedabusada.atmos.domain.location.SearchCitiesUseCase
import dev.ahmedabusada.atmos.domain.settings.ObserveSettingsUseCase
import dev.ahmedabusada.atmos.domain.weather.GetCachedForecastUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

/** One-off things the screen must do once, not "be in" (US-05). */
sealed interface CitiesEffect {
    data class ShowRemoved(val city: City) : CitiesEffect

    data class ShowAlreadySaved(val city: City) : CitiesEffect

    /** A place was chosen: go back to Home, which now shows it. */
    data object Close : CitiesEffect
}

@OptIn(ExperimentalCoroutinesApi::class) // transformLatest
@HiltViewModel
class CitiesViewModel @Inject constructor(
    private val searchCities: SearchCitiesUseCase,
    private val saveCity: SaveCityUseCase,
    private val removeCity: RemoveCityUseCase,
    private val getCachedForecast: GetCachedForecastUseCase,
    private val selectedPlace: SelectedPlaceStore,
    private val language: CurrentLanguage,
    observePlaces: ObservePlacesUseCase,
    observeSettings: ObserveSettingsUseCase,
) : ViewModel() {
    private val query = MutableStateFlow("")

    /** Bumped by "Try again", so the same query is searched again. */
    private val retry = MutableStateFlow(0)

    private val effects = Channel<CitiesEffect>(Channel.BUFFERED)

    /** Effects are consumed once by the route (snackbar, navigation). */
    val effect: Flow<CitiesEffect> = effects.receiveAsFlow()

    /**
     * Search as the user types (US-04): wait [DEBOUNCE_MILLIS] after the last keystroke,
     * and cancel the previous search when a new letter arrives (`transformLatest`).
     * Fewer than 2 characters goes straight back to the saved list.
     */
    private val search: Flow<SearchState> = combine(query, retry) { text, _ -> text.trim() }
        .transformLatest { text ->
            if (text.length < SearchCitiesUseCase.MIN_QUERY_LENGTH) {
                emit(SearchState.Idle)
                return@transformLatest
            }
            delay(DEBOUNCE_MILLIS)
            emit(SearchState.Searching)
            emit(
                when (val result = searchCities(text, language.tag())) {
                    is AppResult.Success ->
                        if (result.data.isEmpty()) SearchState.NoResults else SearchState.Results(result.data)

                    is AppResult.Failure -> SearchState.Failed(result.error)
                },
            )
        }

    /** Saved places with the last known weather of each; no download (wireframe 2). */
    private val places: Flow<List<PlaceItem>> = observePlaces().map { list ->
        list.map { place ->
            val cached = (place as? Place.Saved)?.let { getCachedForecast(it.city.coordinates) }
            PlaceItem(place, cached?.current?.temperature, cached?.current?.condition)
        }
    }

    val state: StateFlow<CitiesUiState> = combine(query, search, places, observeSettings()) {
            text,
            search,
            places,
            settings,
        ->
        CitiesUiState(query = text, search = search, places = places, temperatureUnit = settings.temperatureUnit)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), CitiesUiState())

    fun onEvent(event: CitiesUiEvent) {
        when (event) {
            is CitiesUiEvent.QueryChanged -> query.value = event.query

            CitiesUiEvent.ClearQuery -> query.value = ""

            CitiesUiEvent.RetrySearch -> retry.value++

            is CitiesUiEvent.Add -> add(event.city)

            is CitiesUiEvent.Remove -> remove(event.city)

            is CitiesUiEvent.Select -> {
                selectedPlace.select(event.place)
                effects.trySend(CitiesEffect.Close)
            }

            CitiesUiEvent.Back -> effects.trySend(CitiesEffect.Close)
        }
    }

    /** "Undo" on the snackbar: saving the city again restores it (US-05). */
    fun undoRemove(city: City) = add(city, announceDuplicate = false)

    private fun add(city: City, announceDuplicate: Boolean = true) = viewModelScope.launch {
        when (saveCity(city)) {
            SaveCityResult.Saved -> query.value = ""
            SaveCityResult.AlreadySaved -> if (announceDuplicate) effects.send(CitiesEffect.ShowAlreadySaved(city))
        }
    }

    private fun remove(city: City) = viewModelScope.launch {
        removeCity(city.id)
        selectedPlace.onCityRemoved(city)
        effects.send(CitiesEffect.ShowRemoved(city))
    }

    companion object {
        const val DEBOUNCE_MILLIS = 300L
        private const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
