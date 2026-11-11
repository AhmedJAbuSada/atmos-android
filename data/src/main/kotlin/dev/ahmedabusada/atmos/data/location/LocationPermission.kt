package dev.ahmedabusada.atmos.data.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * "May we read the location?" behind a one-method interface, so [LocationRepositoryImpl]
 * can be tested on the plain JVM without an Android `Context`.
 */
internal fun interface LocationPermission {
    fun isGranted(): Boolean
}

internal class AndroidLocationPermission @Inject constructor(@param:ApplicationContext private val context: Context) :
    LocationPermission {
    override fun isGranted(): Boolean =
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED
}
