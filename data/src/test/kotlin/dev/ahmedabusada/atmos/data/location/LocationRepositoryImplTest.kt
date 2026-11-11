package dev.ahmedabusada.atmos.data.location

import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.tasks.Tasks
import dev.ahmedabusada.atmos.domain.common.AppResult
import dev.ahmedabusada.atmos.domain.common.DomainError
import dev.ahmedabusada.atmos.domain.location.Coordinates
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Google Play services cannot run on the JVM, so here, unlike elsewhere, we use a mock
 * for the location client: it is a third-party boundary we do not own. The permission is
 * a one-method interface, so no Android `Context` is needed.
 */
class LocationRepositoryImplTest {
    private var granted = false
    private val client = mockk<FusedLocationProviderClient>()
    private val repository = LocationRepositoryImpl(permission = { granted }, client = client)

    private fun location(lat: Double, lon: Double) = mockk<Location> {
        every { latitude } returns lat
        every { longitude } returns lon
    }

    @Test
    fun `without permission, returns permission denied and never touches the location client`() = runTest {
        assertEquals(AppResult.Failure(DomainError.LocationPermissionDenied), repository.getCurrentLocation())
        verify(exactly = 0) { client.getCurrentLocation(any<Int>(), any()) }
    }

    @Test
    fun `with permission, returns the current position`() = runTest {
        granted = true
        every { client.getCurrentLocation(any<Int>(), any()) } returns Tasks.forResult(location(41.01, 28.97))

        assertEquals(AppResult.Success(Coordinates(41.01, 28.97)), repository.getCurrentLocation())
    }

    @Test
    fun `given no current fix, falls back to the last known position`() = runTest {
        granted = true
        every { client.getCurrentLocation(any<Int>(), any()) } returns Tasks.forResult(null)
        every { client.lastLocation } returns Tasks.forResult(location(39.92, 32.85))

        assertEquals(AppResult.Success(Coordinates(39.92, 32.85)), repository.getCurrentLocation())
    }

    @Test
    fun `given no position at all, returns location unavailable`() = runTest {
        granted = true
        every { client.getCurrentLocation(any<Int>(), any()) } returns Tasks.forResult(null)
        every { client.lastLocation } returns Tasks.forResult(null)

        assertEquals(AppResult.Failure(DomainError.LocationUnavailable), repository.getCurrentLocation())
    }

    @Test
    fun `given the location service fails, returns location unavailable`() = runTest {
        granted = true
        every { client.getCurrentLocation(any<Int>(), any()) } returns Tasks.forException(IllegalStateException())

        assertEquals(AppResult.Failure(DomainError.LocationUnavailable), repository.getCurrentLocation())
    }
}
