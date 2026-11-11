package dev.ahmedabusada.atmos.data.remote

import dev.ahmedabusada.atmos.domain.common.DomainError
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

/**
 * Translates library exceptions into [DomainError]s. This is the **only** place in the app
 * that knows about `HttpException` or `SocketTimeoutException` (ADR-0006).
 *
 * Order matters: `SocketTimeoutException` is an `IOException`, so it is checked first.
 */
internal fun Throwable.toDomainError(): DomainError = when (this) {
    is SocketTimeoutException -> DomainError.Timeout

    is IOException -> DomainError.NoConnection

    is HttpException -> if (code() == HTTP_NOT_FOUND) DomainError.NotFound else DomainError.Server(code())

    // SerializationException, InvalidResponseException and anything unexpected.
    else -> DomainError.Unknown(this)
}

/** The server answered, but the response is missing something we cannot do without. */
internal class InvalidResponseException(message: String) : Exception(message)

private const val HTTP_NOT_FOUND = 404
