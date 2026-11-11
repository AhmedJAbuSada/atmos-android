package dev.ahmedabusada.atmos.domain.common

import kotlin.coroutines.cancellation.CancellationException

/**
 * Runs [block] and wraps its outcome in an [AppResult], turning exceptions into
 * [DomainError]s with [mapError].
 *
 * Unlike `runCatching`, this **rethrows [CancellationException]**. Catching it would stop
 * coroutine cancellation from working: a screen that is closed would keep its network
 * request alive. This is the single most common coroutine bug, so it lives in one place.
 */
inline fun <T> runCatchingDomain(
    mapError: (Throwable) -> DomainError = { DomainError.Unknown(it) },
    block: () -> T,
): AppResult<T> = try {
    AppResult.Success(block())
} catch (cancellation: CancellationException) {
    throw cancellation
} catch (throwable: Throwable) {
    AppResult.Failure(mapError(throwable))
}
