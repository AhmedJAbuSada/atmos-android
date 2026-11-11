package dev.ahmedabusada.atmos.feature.home

import dev.ahmedabusada.atmos.core.SelectedPlaceStore
import dev.ahmedabusada.atmos.domain.common.AppResult
import dev.ahmedabusada.atmos.domain.common.DomainError
import dev.ahmedabusada.atmos.domain.fakes.FakeLocationRepository
import dev.ahmedabusada.atmos.domain.fakes.FakeSettingsRepository
import dev.ahmedabusada.atmos.domain.fakes.FakeWeatherRepository
import dev.ahmedabusada.atmos.domain.fakes.TestData
import dev.ahmedabusada.atmos.domain.location.GetCurrentLocationForecastUseCase
import dev.ahmedabusada.atmos.domain.location.Place
import dev.ahmedabusada.atmos.domain.settings.ObserveSettingsUseCase
import dev.ahmedabusada.atmos.domain.settings.TemperatureUnit
import dev.ahmedabusada.atmos.domain.weather.ForecastFreshness
import dev.ahmedabusada.atmos.domain.weather.GetForecastUseCase
import dev.ahmedabusada.atmos.testing.MainDispatcherRule
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.time.Clock
import java.time.Duration
import java.time.ZoneOffset

class HomeViewModelTest {
    @get:Rule
    val main = MainDispatcherRule()

    private val weather = FakeWeatherRepository()
    private val location = FakeLocationRepository()
    private val settings = FakeSettingsRepository()
    private val places = SelectedPlaceStore()
    private val freshness = ForecastFreshness(Clock.fixed(TestData.NOW, ZoneOffset.UTC))
    private val getForecast = GetForecastUseCase(weather, freshness)

    private fun TestScope.viewModel(): HomeViewModel {
        val viewModel = HomeViewModel(
            getCurrentLocationForecast = GetCurrentLocationForecastUseCase(location, getForecast),
            getForecast = getForecast,
            freshness = freshness,
            selectedPlace = places,
            observeSettings = ObserveSettingsUseCase(settings),
        )
        backgroundScope.launch(main.dispatcher) { viewModel.state.collect {} } // a screen is watching
        return viewModel
    }

    @Test
    fun `starts with the skeleton, then shows the weather where the user is`() = runTest(main.dispatcher) {
        val viewModel = viewModel()
        assertEquals(HomeUiState.Loading, viewModel.state.value)

        advanceUntilIdle()

        val content = viewModel.state.value as HomeUiState.Content
        assertEquals(null, content.placeName) // shown as "Current location"
        assertEquals(TestData.forecast(), content.forecast)
    }

    @Test
    fun `given location permission is denied, then offers location or search instead of an error`() =
        runTest(main.dispatcher) {
            location.result = AppResult.Failure(DomainError.LocationPermissionDenied)
            val viewModel = viewModel()

            advanceUntilIdle()

            assertEquals(HomeUiState.NeedsLocation, viewModel.state.value)
        }

    @Test
    fun `when permission is granted after the dialog, then loads the weather`() = runTest(main.dispatcher) {
        location.result = AppResult.Failure(DomainError.LocationPermissionDenied)
        val viewModel = viewModel()
        advanceUntilIdle()

        location.result = AppResult.Success(TestData.ISTANBUL)
        viewModel.onLocationPermissionResult(granted = true)
        advanceUntilIdle()

        assertTrue(viewModel.state.value is HomeUiState.Content)
    }

    @Test
    fun `given no cache and no connection, then shows the error, and retry recovers`() = runTest(main.dispatcher) {
        weather.fetchResult = AppResult.Failure(DomainError.NoConnection)
        val viewModel = viewModel()
        advanceUntilIdle()
        assertEquals(HomeUiState.Error(DomainError.NoConnection), viewModel.state.value)

        weather.fetchResult = AppResult.Success(TestData.forecast())
        viewModel.onEvent(HomeUiEvent.Retry)
        advanceUntilIdle()

        assertTrue(viewModel.state.value is HomeUiState.Content)
    }

    @Test
    fun `given stale cache and no connection, then shows the cache with how old it is`() = runTest(main.dispatcher) {
        weather.cached = TestData.forecast(fetchedAt = TestData.NOW.minus(Duration.ofHours(2)))
        weather.fetchResult = AppResult.Failure(DomainError.NoConnection)
        val viewModel = viewModel()

        advanceUntilIdle()

        assertEquals(Duration.ofHours(2), (viewModel.state.value as HomeUiState.Content).outdatedBy)
    }

    @Test
    fun `pull to refresh downloads even when the cache is fresh`() = runTest(main.dispatcher) {
        val viewModel = viewModel()
        advanceUntilIdle()
        val before = weather.fetchCount

        viewModel.onEvent(HomeUiEvent.Refresh)
        advanceUntilIdle()

        assertEquals(before + 1, weather.fetchCount)
        assertEquals(false, (viewModel.state.value as HomeUiState.Content).isRefreshing)
    }

    @Test
    fun `a failed refresh keeps the weather on screen`() = runTest(main.dispatcher) {
        val viewModel = viewModel()
        advanceUntilIdle()

        location.result = AppResult.Failure(DomainError.LocationUnavailable)
        viewModel.onEvent(HomeUiEvent.Refresh)
        advanceUntilIdle()

        assertTrue(viewModel.state.value is HomeUiState.Content)
    }

    @Test
    fun `changing the unit updates the screen without a network call`() = runTest(main.dispatcher) {
        val viewModel = viewModel()
        advanceUntilIdle()
        val before = weather.fetchCount

        settings.update { it.copy(temperatureUnit = TemperatureUnit.Fahrenheit) }
        advanceUntilIdle()

        val content = viewModel.state.value as HomeUiState.Content
        assertEquals(TemperatureUnit.Fahrenheit, content.settings.temperatureUnit)
        assertEquals(before, weather.fetchCount)
    }

    @Test
    fun `choosing a saved city shows that city`() = runTest(main.dispatcher) {
        val viewModel = viewModel()
        advanceUntilIdle()

        places.select(Place.Saved(TestData.city(1, "Ankara")))
        advanceUntilIdle()

        assertEquals("Ankara", (viewModel.state.value as HomeUiState.Content).placeName)
    }
}
