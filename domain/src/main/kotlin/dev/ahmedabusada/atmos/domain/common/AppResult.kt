package dev.ahmedabusada.atmos.domain.common

/**
 * The result of an operation that can fail, with a typed [DomainError] instead of an
 * exception (ADR-0006).
 *
 * Callers handle both cases with an exhaustive `when`:
 * ```
 * when (val result = getForecast(coordinates)) {
 *     is AppResult.Success -> show(result.data)
 *     is AppResult.Failure -> showError(result.error)
 * }
 * ```
 */
sealed interface AppResult<out T> {
    data class Success<out T>(val data: T) : AppResult<T>

    data class Failure(val error: DomainError) : AppResult<Nothing>
}

/** Transforms the success value and keeps a failure unchanged. */
inline fun <T, R> AppResult<T>.map(transform: (T) -> R): AppResult<R> = when (this) {
    is AppResult.Success -> AppResult.Success(transform(data))
    is AppResult.Failure -> this
}

/** The success value, or `null` for a failure. */
fun <T> AppResult<T>.getOrNull(): T? = (this as? AppResult.Success)?.data

/** The error, or `null` for a success. */
fun AppResult<*>.errorOrNull(): DomainError? = (this as? AppResult.Failure)?.error
