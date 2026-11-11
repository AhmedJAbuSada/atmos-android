package dev.ahmedabusada.atmos.data.local

import app.cash.turbine.test
import dev.ahmedabusada.atmos.data.testing.inMemoryDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class DaoTest {
    private val database = inMemoryDatabase()
    private val cities = database.savedCityDao()
    private val cache = database.forecastCacheDao()

    @After
    fun close() = database.close()

    private fun city(id: Long, addedAt: Long) = SavedCityEntity(
        id = id,
        name = "City $id",
        region = null,
        country = null,
        countryCode = null,
        latitude = 1.0,
        longitude = 1.0,
        addedAt = addedAt,
    )

    @Test
    fun `saved cities come back in the order they were added`() = runTest {
        cities.insert(city(id = 2, addedAt = 200))
        cities.insert(city(id = 1, addedAt = 100))

        assertEquals(listOf(1L, 2L), cities.observeAll().first().map { it.id })
    }

    @Test
    fun `saving a city again keeps its original position`() = runTest {
        cities.insert(city(id = 1, addedAt = 100))
        cities.insert(city(id = 2, addedAt = 200))

        cities.insert(city(id = 1, addedAt = 300))

        assertEquals(listOf(1L to 100L, 2L to 200L), cities.observeAll().first().map { it.id to it.addedAt })
    }

    @Test
    fun `observers see deletions`() = runTest {
        cities.insert(city(id = 1, addedAt = 100))

        cities.observeAll().test {
            assertEquals(1, awaitItem().size)
            cities.delete(1)
            assertEquals(0, awaitItem().size)
        }
    }

    @Test
    fun `the forecast cache keeps one entry per place and replaces it`() = runTest {
        cache.upsert(ForecastCacheEntity("41.01,28.97", json = "old", fetchedAtEpochMillis = 1))
        cache.upsert(ForecastCacheEntity("41.01,28.97", json = "new", fetchedAtEpochMillis = 2))

        assertEquals("new", cache.get("41.01,28.97")?.json)
        assertNull(cache.get("39.92,32.85"))
    }
}
