package dev.ahmedabusada.atmos.domain.fakes

import dev.ahmedabusada.atmos.domain.settings.SettingsRepository
import dev.ahmedabusada.atmos.domain.settings.UserSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

/**
 * In-memory [SettingsRepository]. Fakes are preferred over mocks in this project: they
 * behave like the real thing, so tests check outcomes ("the setting changed") instead of
 * interactions ("method X was called").
 */
class FakeSettingsRepository(initial: UserSettings = UserSettings()) : SettingsRepository {
    private val state = MutableStateFlow(initial)

    override val settings: StateFlow<UserSettings> = state

    override suspend fun update(transform: (UserSettings) -> UserSettings) = state.update(transform)
}
