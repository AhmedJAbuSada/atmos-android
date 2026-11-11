package dev.ahmedabusada.atmos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.ahmedabusada.atmos.domain.settings.ObserveSettingsUseCase
import dev.ahmedabusada.atmos.domain.settings.UserSettings
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/** App-wide settings for the activity. `null` until DataStore has been read once. */
@HiltViewModel
class MainViewModel @Inject constructor(observeSettings: ObserveSettingsUseCase) : ViewModel() {
    val settings: StateFlow<UserSettings?> = observeSettings()
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)
}
