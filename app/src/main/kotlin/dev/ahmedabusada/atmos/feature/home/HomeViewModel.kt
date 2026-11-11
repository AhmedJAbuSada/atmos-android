package dev.ahmedabusada.atmos.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.ahmedabusada.atmos.core.SelectedPlaceStore
import dev.ahmedabusada.atmos.domain.common.AppResult
import dev.ahmedabusada.atmos.domain.common.DomainError
import dev.ahmedabusada.atmos.domain.location.GetCurrentLocationForecastUseCase
import dev.ahmedabusada.atmos.domain.location.Place
import dev.ahmedabusada.atmos.domain.settings.ObserveSettingsUseCase
import dev.ahmedabusada.atmos.domain.settings.UserSettings
import dev.ahmedabusada.atmos.domain.weather.ForecastFreshness
import dev.ahmedabusada.atmos.domain.weather.ForecastSnapshot
import dev.ahmedabusada.atmos.domain.weather.GetForecastUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Turns use-case results into [HomeUiState] (US-01, US-06, US-07).
 *
 * The ViewModel holds **presentation logic only**: which state to show, and when to load.
 * The business rules (cache freshness, offline fallback) stay in the use cases from
 * step 03. That is why this class is short.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getCurrentLocationForecast: GetCurrentLocationForecastUseCase,
    private val getForecast: GetForecastUseCase,
    private val freshness: ForecastFreshness,
    private val selectedPlace: SelectedPlaceStore,
    observeSettings: ObserveSettingsUseCase,
) : ViewModel() {

    /** The result of the last load, before settings are applied. */
    private val load = MutableStateFlow<LoadState>(LoadState.Loading)
    private var loadJob: Job? = null

    /** Settings are combined in last, so changing °C → °F updates the screen without a reload (US-08). */
    val state: StateFlow<HomeUiState> = combine(load, observeSettings()) { load, settings -> load.toUiState(settings) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), HomeUiState.Loading)

    init {
        viewModelScope.launch {
            // A new place (chosen on the Cities screen) loads at once; an older load is cancelled.
            selectedPlace.place.collect { load(forceRefresh = false) }
        }
    }

    fun onEvent(event: HomeUiEvent) {
        when (event) {
            HomeUiEvent.Refresh -> load(forceRefresh = true)

            HomeUiEvent.Retry -> load(forceRefresh = false)

            // Navigation and the permission dialog are handled by the route (HomeRoute).
            HomeUiEvent.RequestLocation, HomeUiEvent.OpenCities, HomeUiEvent.OpenSettings -> Unit
        }
    }

    /** Called by the route after the system permission dialog closes. */
    fun onLocationPermissionResult(granted: Boolean) {
        if (granted) load(forceRefresh = false)
    }

    private fun load(forceRefresh: Boolean) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            val place = selectedPlace.place.value
            load.update { current ->
                if (current is LoadState.Loaded) current.copy(isRefreshing = true) else LoadState.Loading
            }
            val result = when (place) {
                Place.CurrentLocation -> getCurrentLocationForecast(forceRefresh)
                is Place.Saved -> getForecast(place.city.coordinates, forceRefresh)
            }
            load.update { current ->
                when (result) {
                    is AppResult.Success -> LoadState.Loaded(place, result.data)

                    // A failed pull-to-refresh keeps what is on screen instead of replacing
                    // good data with an error; the use case already falls back to the cache.
                    is AppResult.Failure ->
                        if (current is LoadState.Loaded && current.place == place) {
                            current.copy(isRefreshing = false)
                        } else {
                            LoadState.Failed(result.error)
                        }
                }
            }
        }
    }

    private fun LoadState.toUiState(settings: UserSettings): HomeUiState = when (this) {
        LoadState.Loading -> HomeUiState.Loading

        is LoadState.Failed ->
            if (error == DomainError.LocationPermissionDenied) HomeUiState.NeedsLocation else HomeUiState.Error(error)

        is LoadState.Loaded -> HomeUiState.Content(
            placeName = (place as? Place.Saved)?.city?.name,
            forecast = snapshot.forecast,
            settings = settings,
            outdatedBy = if (snapshot.isOutdated) freshness.age(snapshot.forecast) else null,
            isRefreshing = isRefreshing,
        )
    }

    private sealed interface LoadState {
        data object Loading : LoadState

        data class Loaded(val place: Place, val snapshot: ForecastSnapshot, val isRefreshing: Boolean = false) :
            LoadState

        data class Failed(val error: DomainError) : LoadState
    }

    private companion object {
        /** Keep loading through a rotation (a few seconds), stop when the screen is really gone. */
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
