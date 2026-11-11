package dev.ahmedabusada.atmos.domain.settings

import app.cash.turbine.test
import dev.ahmedabusada.atmos.domain.fakes.FakeSettingsRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class SettingsUseCasesTest {
    private val repository = FakeSettingsRepository()
    private val observe = ObserveSettingsUseCase(repository)
    private val update = UpdateSettingsUseCase(repository)

    @Test
    fun `first run uses metric units and follows the system`() = runTest {
        observe().test {
            assertEquals(
                UserSettings(
                    temperatureUnit = TemperatureUnit.Celsius,
                    windSpeedUnit = WindSpeedUnit.KilometersPerHour,
                    language = AppLanguage.System,
                    theme = AppTheme.System,
                ),
                awaitItem(),
            )
        }
    }

    @Test
    fun `when the unit changes, then observers receive the new settings`() = runTest {
        observe().test {
            awaitItem()

            update { it.copy(temperatureUnit = TemperatureUnit.Fahrenheit) }

            assertEquals(TemperatureUnit.Fahrenheit, awaitItem().temperatureUnit)
        }
    }

    @Test
    fun `when an update changes nothing, then observers are not notified again`() = runTest {
        observe().test {
            awaitItem()

            update { it }
            update { it.copy(language = AppLanguage.Turkish) }

            assertEquals(AppLanguage.Turkish, awaitItem().language)
            expectNoEvents()
        }
    }

    @Test
    fun `every language except system has a BCP 47 tag`() {
        assertEquals(listOf(null, "en", "tr", "ar"), AppLanguage.entries.map { it.tag })
    }
}
