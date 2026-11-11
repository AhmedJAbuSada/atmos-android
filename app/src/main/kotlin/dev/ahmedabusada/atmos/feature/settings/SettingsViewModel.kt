package dev.ahmedabusada.atmos.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.ahmedabusada.atmos.core.AppLanguageController
import dev.ahmedabusada.atmos.domain.settings.ObserveSettingsUseCase
import dev.ahmedabusada.atmos.domain.settings.UpdateSettingsUseCase
import dev.ahmedabusada.atmos.domain.settings.UserSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Settings (US-08). Changes are saved at once; every screen observes the same settings
 * flow, so they all update without being told.
 *
 * The language is the exception: it is read from and written to the platform's per-app
 * locale ([AppLanguageController]), its single source of truth.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    observeSettings: ObserveSettingsUseCase,
    private val updateSettings: UpdateSettingsUseCase,
    private val languages: AppLanguageController,
) : ViewModel() {
    private val language = MutableStateFlow(languages.current())

    val state: StateFlow<UserSettings> = combine(observeSettings(), language) { settings, language ->
        settings.copy(language = language)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), UserSettings())

    fun onEvent(event: SettingsUiEvent) {
        val change: ((UserSettings) -> UserSettings)? = when (event) {
            is SettingsUiEvent.SetTemperatureUnit -> { settings -> settings.copy(temperatureUnit = event.unit) }

            is SettingsUiEvent.SetWindSpeedUnit -> { settings -> settings.copy(windSpeedUnit = event.unit) }

            is SettingsUiEvent.SetLanguage -> {
                language.value = event.language
                languages.apply(event.language) // recreates the activity in the new language
                null
            }

            is SettingsUiEvent.SetTheme -> { settings -> settings.copy(theme = event.theme) }

            SettingsUiEvent.OpenAbout, SettingsUiEvent.Back -> null // navigation, handled by the route
        }
        change?.let { viewModelScope.launch { updateSettings(it) } }
    }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
