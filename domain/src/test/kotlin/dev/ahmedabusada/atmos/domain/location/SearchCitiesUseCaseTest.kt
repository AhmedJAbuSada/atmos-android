package dev.ahmedabusada.atmos.domain.location

import dev.ahmedabusada.atmos.domain.common.AppResult
import dev.ahmedabusada.atmos.domain.common.DomainError
import dev.ahmedabusada.atmos.domain.fakes.FakeCityRepository
import dev.ahmedabusada.atmos.domain.fakes.TestData
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** Acceptance criteria of US-04 that belong to the domain. */
class SearchCitiesUseCaseTest {
    private val repository = FakeCityRepository()
    private val search = SearchCitiesUseCase(repository)

    @Test
    fun `given fewer than 2 characters, then no search is made`() = runTest {
        assertEquals(AppResult.Success(emptyList<City>()), search("i", "tr"))
        assertTrue(repository.searches.isEmpty())
    }

    @Test
    fun `given only spaces around one character, then no search is made`() = runTest {
        search("  i  ", "tr")

        assertTrue(repository.searches.isEmpty())
    }

    @Test
    fun `given a valid query, then searches the trimmed text in the requested language`() = runTest {
        search("  ista ", "tr")

        assertEquals(listOf("ista" to "tr"), repository.searches)
    }

    @Test
    fun `given matches, then returns them`() = runTest {
        val istanbul = TestData.city(745044, "İstanbul")
        repository.searchResult = AppResult.Success(listOf(istanbul))

        assertEquals(AppResult.Success(listOf(istanbul)), search("ista", "tr"))
    }

    @Test
    fun `given no city matches, then returns an empty list, not an error`() = runTest {
        repository.searchResult = AppResult.Success(emptyList())

        assertEquals(AppResult.Success(emptyList<City>()), search("zzqqxx", "en"))
    }

    @Test
    fun `given the network fails, then returns the error`() = runTest {
        repository.searchResult = AppResult.Failure(DomainError.NoConnection)

        assertEquals(AppResult.Failure(DomainError.NoConnection), search("ista", "en"))
    }
}
