package dev.ahmedabusada.atmos.domain.location

import app.cash.turbine.test
import dev.ahmedabusada.atmos.domain.fakes.FakeCityRepository
import dev.ahmedabusada.atmos.domain.fakes.TestData
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

/** Acceptance criteria of US-05 (favourite cities). */
class CityUseCasesTest {
    private val repository = FakeCityRepository()
    private val save = SaveCityUseCase(repository)
    private val remove = RemoveCityUseCase(repository)
    private val observePlaces = ObservePlacesUseCase(repository)

    private val ankara = TestData.city(1, "Ankara")
    private val london = TestData.city(2, "London")

    @Test
    fun `given no saved cities, then the list holds only the current location`() = runTest {
        assertEquals(listOf(Place.CurrentLocation), observePlaces().first())
    }

    @Test
    fun `when cities are saved, then they follow the current location in order`() = runTest {
        save(ankara)
        save(london)

        assertEquals(
            listOf(Place.CurrentLocation, Place.Saved(ankara), Place.Saved(london)),
            observePlaces().first(),
        )
    }

    @Test
    fun `when a city is saved twice, then nothing changes the second time`() = runTest {
        assertEquals(SaveCityResult.Saved, save(ankara))
        assertEquals(SaveCityResult.AlreadySaved, save(ankara))

        assertEquals(listOf(ankara), repository.observeSavedCities().first())
    }

    @Test
    fun `when a city is removed, then observers see it disappear`() = runTest {
        save(ankara)
        save(london)

        observePlaces().test {
            awaitItem()
            remove(ankara.id)
            assertEquals(listOf(Place.CurrentLocation, Place.Saved(london)), awaitItem())
        }
    }

    @Test
    fun `when a removal is undone by saving again, then the city is back`() = runTest {
        save(ankara)
        remove(ankara.id)

        save(ankara)

        assertEquals(listOf(ankara), repository.observeSavedCities().first())
    }
}
