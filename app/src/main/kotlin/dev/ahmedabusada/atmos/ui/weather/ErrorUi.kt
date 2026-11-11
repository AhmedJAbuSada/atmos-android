package dev.ahmedabusada.atmos.ui.weather

import androidx.annotation.StringRes
import dev.ahmedabusada.atmos.R
import dev.ahmedabusada.atmos.domain.common.DomainError

/**
 * A human message for every [DomainError]. Because the error type is sealed, adding a new
 * error makes this `when` fail to compile until the new case has a message (ADR-0006).
 */
@StringRes
fun DomainError.messageRes(): Int = when (this) {
    DomainError.NoConnection -> R.string.error_no_connection
    DomainError.Timeout -> R.string.error_timeout
    is DomainError.Server -> R.string.error_server
    DomainError.NotFound -> R.string.error_not_found
    DomainError.LocationPermissionDenied -> R.string.error_location_denied
    DomainError.LocationUnavailable -> R.string.error_location_unavailable
    is DomainError.Unknown -> R.string.error_unknown
}
