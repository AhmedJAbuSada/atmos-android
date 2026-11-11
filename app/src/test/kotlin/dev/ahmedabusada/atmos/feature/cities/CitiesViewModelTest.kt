package dev.ahmedabusada.atmos.feature.cities

import app.cash.turbine.test
import dev.ahmedabusada.atmos.core.SelectedPlaceStore
import dev.ahmedabusada.atmos.domain.common.AppResult
import dev.ahmedabusada.atmos.domain.common.DomainError
import dev.ahmedabusada.atmos.domain.fakes.FakeCityRepository
import dev.ahmedabusada.atmos.domain.fakes.FakeSettingsRepository
import dev.ahmedabusada.atmos.domain.fakes.FakeWeatherRepository
import dev.ahmedabusada.atmos.domain.fakes.TestData
import dev.ahmedabusada.atmos.domain.location.ObservePlacesUseCase
import dev.ahmedabusada.atmos.domain.location.Place
import dev.ahmedabusada.atmos.domain.location.RemoveCityUseCase
import dev.ahmedabusada.atmos.domain.location.SaveCityUseCase
import dev.ahmedabusada.atmos.domain.location.SearchCitiesUseCase
import dev.ahmedabusada.atmos.domain.settings.ObserveSettingsUseCase
import dev.ahmedabusada.atmos.domain.weather.GetCachedForecastUseCase
import dev.ahmedabusada.atmos.testing.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/** The acceptance criteria of US-04 and US-05 that live in the presentation layer. */
@OptIn(ExperimentalCoroutinesApi::class) // advanceTimeBy: virtual time for the debounce
class CitiesViewModelTest {
    @get:Rule
    val main = MainDispatcherRule()

    private val cities = FakeCityRepository()
    private val weather = FakeWeatherRepository()
    private val places = SelectedPlaceStore()
    private val istanbul = TestData.city(745044, "İstanbul")
    private val ankara = TestData.city(1, "Ankara")

    private fun TestScope.viewModel(): CitiesViewModel {
        val viewModel = CitiesViewModel(
            searchCities = SearchCitiesUseCase(cities),
            saveCity = SaveCityUseCase(cities),
            removeCity = RemoveCityUseCase(cities),
            getCachedForecast = GetCachedForecastUseCase(weather),
            selectedPlace = places,
            language = { "tr" },
            observePlaces = ObservePlacesUseCase(cities),
            observeSettings = ObserveSettingsUseCase(FakeSettingsRepository()),
        )
        backgroundScope.launch(main.dispatcher) { viewModel.state.collect {} }
        return viewModel
    }

    private fun CitiesViewModel.type(text: String) = onEvent(CitiesUiEvent.QueryChanged(text))

    @Test
    fun `typing quickly searches once, 300 ms after the last letter`() = runTest(main.dispatcher) {
        val viewModel = viewModel()

        viewModel.type("is")
        advanceTimeBy(100)
        viewModel.type("ist")
        advanceTimeBy(100)
        viewModel.type("ista")
        advanceTimeBy(CitiesViewModel.DEBOUNCE_MILLIS + 1)

        assertEquals(listOf("ista" to "tr"), cities.searches)
    }

    @Test
    fun `fewer than 2 characters shows the saved list and searches nothing`() = runTest(main.dispatcher) {
        val viewModel = viewModel()

        viewModel.type("i")
        advanceUntilIdle()

        assertEquals(SearchState.Idle, viewModel.state.value.search)
        assertTrue(cities.searches.isEmpty())
    }

    @Test
    fun `results are shown, and searched in the app language`() = runTest(main.dispatcher) {
        cities.searchResult = AppResult.Success(listOf(istanbul))
        val viewModel = viewModel()

        viewModel.type("ista")
        advanceUntilIdle()

        assertEquals(SearchState.Results(listOf(istanbul)), viewModel.state.value.search)
        assertEquals("tr", cities.searches.single().second)
    }

    @Test
    fun `no match shows the empty message, not an error`() = runTest(main.dispatcher) {
        val viewModel = viewModel()

        viewModel.type("zzqqxx")
        advanceUntilIdle()

        assertEquals(SearchState.NoResults, viewModel.state.value.search)
    }

    @Test
    fun `a network failure shows an error, and try again searches again`() = runTest(main.dispatcher) {
        cities.searchResult = AppResult.Failure(DomainError.NoConnection)
        val viewModel = viewModel()
        viewModel.type("ista")
        advanceUntilIdle()
        assertEquals(SearchState.Failed(DomainError.NoConnection), viewModel.state.value.search)

        cities.searchResult = AppResult.Success(listOf(istanbul))
        viewModel.onEvent(CitiesUiEvent.RetrySearch)
        advanceUntilIdle()

        assertEquals(SearchState.Results(listOf(istanbul)), viewModel.state.value.search)
        assertEquals(2, cities.searches.size)
    }

    @Test
    fun `adding a city saves it and returns to the list`() = runTest(main.dispatcher) {
        val viewModel = viewModel()
        viewModel.type("ista")

        viewModel.onEvent(CitiesUiEvent.Add(istanbul))
        advanceUntilIdle()

        assertEquals(listOf(istanbul), cities.observeSavedCities().first())
        assertEquals("", viewModel.state.value.query)
    }

    @Test
    fun `adding a saved city again only tells the user`() = runTest(main.dispatcher) {
        val viewModel = viewModel()
        cities.save(istanbul)

        viewModel.effect.test {
            viewModel.onEvent(CitiesUiEvent.Add(istanbul))
            assertEquals(CitiesEffect.ShowAlreadySaved(istanbul), awaitItem())
        }
        assertEquals(listOf(istanbul), cities.observeSavedCities().first())
    }

    @Test
    fun `removing a city offers undo, and undo restores it`() = runTest(main.dispatcher) {
        val viewModel = viewModel()
        cities.save(ankara)

        viewModel.effect.test {
            viewModel.onEvent(CitiesUiEvent.Remove(ankara))
            assertEquals(CitiesEffect.ShowRemoved(ankara), awaitItem())
        }
        assertEquals(emptyList<Any>(), cities.observeSavedCities().first())

        viewModel.undoRemove(ankara)
        advanceUntilIdle()

        assertEquals(listOf(ankara), cities.observeSavedCities().first())
    }

    @Test
    fun `removing the city on the Home screen switches Home back to the current location`() = runTest(main.dispatcher) {
        val viewModel = viewModel()
        cities.save(ankara)
        places.select(Place.Saved(ankara))

        viewModel.onEvent(CitiesUiEvent.Remove(ankara))
        advanceUntilIdle()

        assertEquals(Place.CurrentLocation, places.place.value)
    }

    @Test
    fun `choosing a place shows it on Home and closes the screen`() = runTest(main.dispatcher) {
        val viewModel = viewModel()

        viewModel.effect.test {
            viewModel.onEvent(CitiesUiEvent.Select(Place.Saved(ankara)))
            assertEquals(CitiesEffect.Close, awaitItem())
        }
        assertEquals(Place.Saved(ankara), places.place.value)
    }

    @Test
    fun `saved cities show their last known temperature`() = runTest(main.dispatcher) {
        weather.cached = TestData.forecast()
        cities.save(ankara)
        val viewModel = viewModel()

        advanceUntilIdle()

        val saved = viewModel.state.value.places.single { it.place is Place.Saved }
        assertEquals(TestData.forecast().current.temperature, saved.temperature)
    }
}
