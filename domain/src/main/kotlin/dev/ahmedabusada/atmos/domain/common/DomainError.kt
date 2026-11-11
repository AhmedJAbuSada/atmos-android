package dev.ahmedabusada.atmos.domain.common

/**
 * Everything that can go wrong, in the language of the business, not of the libraries.
 *
 * The data layer maps exceptions (`IOException`, `HttpException`, …) to these values at
 * the boundary, so the UI never sees an exception class (ADR-0006). The presentation
 * layer maps each value to a localized, human message.
 */
sealed interface DomainError {
    /** The device is offline or the host cannot be reached. */
    data object NoConnection : DomainError

    /** The server did not answer in time. */
    data object Timeout : DomainError

    /** The server answered with an error status (HTTP 4xx or 5xx). */
    data class Server(val code: Int) : DomainError

    /** The requested data does not exist. */
    data object NotFound : DomainError

    /** The user has not granted location permission. */
    data object LocationPermissionDenied : DomainError

    /** Permission is granted, but the device cannot determine a location right now. */
    data object LocationUnavailable : DomainError

    /** Anything we did not anticipate. [cause] is kept for logging only. */
    data class Unknown(val cause: Throwable? = null) : DomainError
}
