package dev.ahmedabusada.atmos.feature.settings

import dev.ahmedabusada.atmos.core.AppLanguageController
import dev.ahmedabusada.atmos.domain.fakes.FakeSettingsRepository
import dev.ahmedabusada.atmos.domain.settings.AppLanguage
import dev.ahmedabusada.atmos.domain.settings.AppTheme
import dev.ahmedabusada.atmos.domain.settings.ObserveSettingsUseCase
import dev.ahmedabusada.atmos.domain.settings.TemperatureUnit
import dev.ahmedabusada.atmos.domain.settings.UpdateSettingsUseCase
import dev.ahmedabusada.atmos.domain.settings.WindSpeedUnit
import dev.ahmedabusada.atmos.testing.MainDispatcherRule
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class SettingsViewModelTest {
    @get:Rule
    val main = MainDispatcherRule()

    private val repository = FakeSettingsRepository()

    /** Stands in for the platform per-app locale. */
    private val languages = object : AppLanguageController {
        var applied = AppLanguage.System

        override fun current() = applied

        override fun apply(language: AppLanguage) {
            applied = language
        }
    }

    private fun TestScope.viewModel(): SettingsViewModel {
        val viewModel =
            SettingsViewModel(ObserveSettingsUseCase(repository), UpdateSettingsUseCase(repository), languages)
        backgroundScope.launch(main.dispatcher) { viewModel.state.collect {} }
        return viewModel
    }

    @Test
    fun `units and theme are saved and shown`() = runTest(main.dispatcher) {
        val viewModel = viewModel()

        viewModel.onEvent(SettingsUiEvent.SetTemperatureUnit(TemperatureUnit.Fahrenheit))
        viewModel.onEvent(SettingsUiEvent.SetWindSpeedUnit(WindSpeedUnit.MetersPerSecond))
        viewModel.onEvent(SettingsUiEvent.SetTheme(AppTheme.Dark))
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(TemperatureUnit.Fahrenheit, state.temperatureUnit)
        assertEquals(WindSpeedUnit.MetersPerSecond, state.windSpeedUnit)
        assertEquals(AppTheme.Dark, state.theme)
        assertEquals(state.copy(language = AppLanguage.System), repository.settings.value)
    }

    @Test
    fun `the language goes to the platform per-app locale and is shown`() = runTest(main.dispatcher) {
        val viewModel = viewModel()

        viewModel.onEvent(SettingsUiEvent.SetLanguage(AppLanguage.Arabic))
        advanceUntilIdle()

        assertEquals(AppLanguage.Arabic, languages.applied)
        assertEquals(AppLanguage.Arabic, viewModel.state.value.language)
    }

    @Test
    fun `a language chosen in the system settings is shown`() = runTest(main.dispatcher) {
        languages.applied = AppLanguage.Turkish

        val viewModel = viewModel()
        advanceUntilIdle()

        assertEquals(AppLanguage.Turkish, viewModel.state.value.language)
    }
}
