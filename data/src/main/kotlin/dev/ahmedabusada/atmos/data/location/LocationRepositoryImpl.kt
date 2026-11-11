package dev.ahmedabusada.atmos.data.location

import android.annotation.SuppressLint
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import dev.ahmedabusada.atmos.domain.common.AppResult
import dev.ahmedabusada.atmos.domain.common.DomainError
import dev.ahmedabusada.atmos.domain.common.runCatchingDomain
import dev.ahmedabusada.atmos.domain.location.Coordinates
import dev.ahmedabusada.atmos.domain.location.LocationRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * [LocationRepository] on the fused location provider.
 *
 * Uses **coarse** location (about 1–3 km): plenty for weather, and kinder to the user's
 * privacy and battery (NF-06). Asking for permission is the UI's job (step 06); this class
 * only checks it, so it never shows a dialog by itself.
 */
internal class LocationRepositoryImpl @Inject constructor(
    private val permission: LocationPermission,
    private val client: FusedLocationProviderClient,
) : LocationRepository {
    // The permission is checked on the line above each call, which lint cannot see.
    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): AppResult<Coordinates> {
        if (!permission.isGranted()) return AppResult.Failure(DomainError.LocationPermissionDenied)

        return runCatchingDomain(mapError = { DomainError.LocationUnavailable }) {
            val location = client.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null).await()
                ?: client.lastLocation.await()
                ?: return AppResult.Failure(DomainError.LocationUnavailable)
            Coordinates(location.latitude, location.longitude)
        }
    }
}
