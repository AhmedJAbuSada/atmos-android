package dev.ahmedabusada.atmos.domain.settings

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject

/** Streams the user's settings; emits only when something actually changed. */
class ObserveSettingsUseCase @Inject constructor(
    private val repository: SettingsRepository,
) {
    operator fun invoke(): Flow<UserSettings> = repository.settings.distinctUntilChanged()
}

/** Changes one or more settings, for example `update { it.copy(theme = AppTheme.Dark) }`. */
class UpdateSettingsUseCase @Inject constructor(
    private val repository: SettingsRepository,
) {
    suspend operator fun invoke(transform: (UserSettings) -> UserSettings) = repository.update(transform)
}
