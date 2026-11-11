package dev.ahmedabusada.atmos.domain.settings

import kotlinx.coroutines.flow.Flow

/** Stores [UserSettings]. Implemented with DataStore in :data (step 04). */
interface SettingsRepository {
    /** Emits the current settings, then every change, so the UI updates instantly (US-08). */
    val settings: Flow<UserSettings>

    /** Atomically replaces the settings with the result of [transform]. */
    suspend fun update(transform: (UserSettings) -> UserSettings)
}
