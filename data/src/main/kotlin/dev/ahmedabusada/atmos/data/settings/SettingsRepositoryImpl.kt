package dev.ahmedabusada.atmos.data.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import dev.ahmedabusada.atmos.domain.settings.SettingsRepository
import dev.ahmedabusada.atmos.domain.settings.UserSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

/**
 * [SettingsRepository] on Jetpack DataStore (ADR-0004).
 *
 * Enums are stored by **name**. An unknown name (for example after renaming an enum
 * value) falls back to the default instead of crashing, so old data is always readable.
 */
internal class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : SettingsRepository {
    override val settings: Flow<UserSettings> = dataStore.data
        // A corrupted or unreadable file must not take the app down: use the defaults.
        .catch { error -> if (error is IOException) emit(emptyPreferences()) else throw error }
        .map { it.toUserSettings() }

    override suspend fun update(transform: (UserSettings) -> UserSettings) {
        dataStore.edit { preferences -> preferences.write(transform(preferences.toUserSettings())) }
    }

    private fun Preferences.toUserSettings(): UserSettings {
        val defaults = UserSettings()
        return UserSettings(
            temperatureUnit = enumOrDefault(this[Keys.TEMPERATURE_UNIT], defaults.temperatureUnit),
            windSpeedUnit = enumOrDefault(this[Keys.WIND_SPEED_UNIT], defaults.windSpeedUnit),
            language = enumOrDefault(this[Keys.LANGUAGE], defaults.language),
            theme = enumOrDefault(this[Keys.THEME], defaults.theme),
        )
    }

    private fun MutablePreferences.write(settings: UserSettings) {
        this[Keys.TEMPERATURE_UNIT] = settings.temperatureUnit.name
        this[Keys.WIND_SPEED_UNIT] = settings.windSpeedUnit.name
        this[Keys.LANGUAGE] = settings.language.name
        this[Keys.THEME] = settings.theme.name
    }

    private inline fun <reified E : Enum<E>> enumOrDefault(name: String?, default: E): E =
        enumValues<E>().firstOrNull { it.name == name } ?: default

    private object Keys {
        val TEMPERATURE_UNIT = stringPreferencesKey("temperature_unit")
        val WIND_SPEED_UNIT = stringPreferencesKey("wind_speed_unit")
        val LANGUAGE = stringPreferencesKey("language")
        val THEME = stringPreferencesKey("theme")
    }
}

