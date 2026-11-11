package dev.ahmedabusada.atmos.feature.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SettingsRoute(onBack: () -> Unit, onOpenAbout: () -> Unit, viewModel: SettingsViewModel = hiltViewModel()) {
    val settings by viewModel.state.collectAsStateWithLifecycle()
    SettingsScreen(
        settings = settings,
        onEvent = { event ->
            when (event) {
                SettingsUiEvent.Back -> onBack()
                SettingsUiEvent.OpenAbout -> onOpenAbout()
                else -> viewModel.onEvent(event)
            }
        },
    )
}
