package dev.ahmedabusada.atmos.data.location

import android.Manifest
import android.app.Application
import android.location.Location
import androidx.test.core.app.ApplicationProvider
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
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

/**
 * Google Play services cannot run on the JVM, so here, unlike elsewhere, we use a mock
 * for the location client: it is a third-party boundary we do not own.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class LocationRepositoryImplTest {
    private val application = ApplicationProvider.getApplicationContext<Application>()
    private val client = mockk<FusedLocationProviderClient>()
    private val repository = LocationRepositoryImpl(application, client)

    private fun grantPermission() = shadowOf(application).grantPermissions(Manifest.permission.ACCESS_COARSE_LOCATION)

    private fun location(lat: Double, lon: Double) = Location("test").apply {
        latitude = lat
        longitude = lon
    }

    @Test
    fun `without permission, returns permission denied and never touches the location client`() = runTest {
        assertEquals(AppResult.Failure(DomainError.LocationPermissionDenied), repository.getCurrentLocation())
        verify(exactly = 0) { client.getCurrentLocation(any<Int>(), any()) }
    }

    @Test
    fun `with permission, returns the current position`() = runTest {
        grantPermission()
        every { client.getCurrentLocation(any<Int>(), any()) } returns Tasks.forResult(location(41.01, 28.97))

        assertEquals(AppResult.Success(Coordinates(41.01, 28.97)), repository.getCurrentLocation())
    }

    @Test
    fun `given no current fix, falls back to the last known position`() = runTest {
        grantPermission()
        every { client.getCurrentLocation(any<Int>(), any()) } returns Tasks.forResult(null)
        every { client.lastLocation } returns Tasks.forResult(location(39.92, 32.85))

        assertEquals(AppResult.Success(Coordinates(39.92, 32.85)), repository.getCurrentLocation())
    }

    @Test
    fun `given no position at all, returns location unavailable`() = runTest {
        grantPermission()
        every { client.getCurrentLocation(any<Int>(), any()) } returns Tasks.forResult(null)
        every { client.lastLocation } returns Tasks.forResult(null)

        assertEquals(AppResult.Failure(DomainError.LocationUnavailable), repository.getCurrentLocation())
    }

    @Test
    fun `given the location service fails, returns location unavailable`() = runTest {
        grantPermission()
        every { client.getCurrentLocation(any<Int>(), any()) } returns Tasks.forException(IllegalStateException())

        assertEquals(AppResult.Failure(DomainError.LocationUnavailable), repository.getCurrentLocation())
    }
}
