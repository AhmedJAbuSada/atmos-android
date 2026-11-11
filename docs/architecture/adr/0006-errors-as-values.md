# ADR-0006: Errors as values (`AppResult`) instead of exceptions

- **Status:** Accepted
- **Date:** 2026-11-11

## Context
Network calls fail, the disk can be full, and the user can deny permission. If exceptions
travel up through the layers, any of them can crash the app, and the UI has to know about
`IOException` and `HttpException`, which are data-layer details.

## Decision
Functions that can fail return a domain type:

```kotlin
sealed interface AppResult<out T> {
    data class Success<T>(val data: T) : AppResult<T>
    data class Failure(val error: DomainError) : AppResult<Nothing>
}

sealed interface DomainError {
    data object NoConnection : DomainError
    data object Timeout : DomainError
    data class Server(val code: Int) : DomainError
    data object NotFound : DomainError
    data object LocationPermissionDenied : DomainError
    data object LocationUnavailable : DomainError
    data class Unknown(val cause: Throwable? = null) : DomainError
}
```

The data layer catches exceptions **at the boundary** and maps them to `DomainError`. The
presentation layer maps `DomainError` to a localized message.

## Alternatives considered
| Option | Why not |
|---|---|
| Throw and `try/catch` in ViewModels | Error types leak across layers; easy to forget a catch. |
| `kotlin.Result` | Holds a `Throwable`, not a typed domain error; `when` cannot check all cases. |
| Arrow `Either` | Powerful, but a big dependency for one type. |

## Consequences
- ✅ `when (error)` is exhaustive: the compiler reminds you to handle every case.
- ✅ The UI never sees an exception class.
- ⚠️ `CancellationException` must **always** be rethrown, never mapped, or coroutines
  break. A shared helper (`runCatchingDomain`) does this correctly.
