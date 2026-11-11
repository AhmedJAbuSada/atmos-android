package dev.ahmedabusada.atmos.feature.home

import dev.ahmedabusada.atmos.domain.common.DomainError
import dev.ahmedabusada.atmos.domain.settings.UserSettings
import dev.ahmedabusada.atmos.domain.weather.Forecast
import java.time.Duration

/**
 * Everything the Home screen can show. The screen is a pure function of this value: give
 * it a state, get pixels. That is why previews and tests need no ViewModel.
 */
sealed interface HomeUiState {
    /** First load, nothing cached yet: show the skeleton (US-09). */
    data object Loading : HomeUiState

    /** No permission and no saved city: offer both ways forward (F-02). */
    data object NeedsLocation : HomeUiState

    /** Nothing to show and the load failed. */
    data class Error(val error: DomainError) : HomeUiState

    data class Content(
        /** The city name, or null for the current location. */
        val placeName: String?,
        val forecast: Forecast,
        val settings: UserSettings,
        /** Set when the data is outdated because a refresh failed (F-08). */
        val outdatedBy: Duration? = null,
        val isRefreshing: Boolean = false,
    ) : HomeUiState
}

/** What the user can do on the Home screen. The screen reports; the ViewModel decides. */
sealed interface HomeUiEvent {
    data object Refresh : HomeUiEvent

    data object Retry : HomeUiEvent

    data object RequestLocation : HomeUiEvent

    data object OpenCities : HomeUiEvent

    data object OpenSettings : HomeUiEvent
}
