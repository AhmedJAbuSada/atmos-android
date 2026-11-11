package dev.ahmedabusada.atmos.data.settings

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import dev.ahmedabusada.atmos.domain.settings.AppLanguage
import dev.ahmedabusada.atmos.domain.settings.AppTheme
import dev.ahmedabusada.atmos.domain.settings.TemperatureUnit
import dev.ahmedabusada.atmos.domain.settings.UserSettings
import dev.ahmedabusada.atmos.domain.settings.WindSpeedUnit
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

/** Uses a real DataStore writing to a temporary file: no Android needed. */
class SettingsRepositoryImplTest {
    @get:Rule
    val folder = TemporaryFolder()

    private fun TestScope.dataStore() = PreferenceDataStoreFactory.create(scope = backgroundScope) {
        folder.root.resolve("settings.preferences_pb")
    }

    @Test
    fun `first run returns the defaults`() = runTest {
        assertEquals(UserSettings(), SettingsRepositoryImpl(dataStore()).settings.first())
    }

    @Test
    fun `every setting is saved and read back`() = runTest {
        val repository = SettingsRepositoryImpl(dataStore())
        val changed = UserSettings(
            temperatureUnit = TemperatureUnit.Fahrenheit,
            windSpeedUnit = WindSpeedUnit.MilesPerHour,
            language = AppLanguage.Arabic,
            theme = AppTheme.Dark,
        )

        repository.update { changed }

        assertEquals(changed, repository.settings.first())
    }

    @Test
    fun `an update changes only what the transform changes`() = runTest {
        val repository = SettingsRepositoryImpl(dataStore())
        repository.update { it.copy(language = AppLanguage.Turkish) }

        repository.update { it.copy(theme = AppTheme.Light) }

        assertEquals(UserSettings(language = AppLanguage.Turkish, theme = AppTheme.Light), repository.settings.first())
    }

    @Test
    fun `an unknown stored value falls back to the default instead of crashing`() = runTest {
        val store = dataStore()
        store.edit { it[stringPreferencesKey("temperature_unit")] = "Kelvin" }

        assertEquals(TemperatureUnit.Celsius, SettingsRepositoryImpl(store).settings.first().temperatureUnit)
    }
}
