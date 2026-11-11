# Coding standards (Kotlin / Android)

These rules are checked by tools wherever possible (step 02 sets up the tools). A rule a
machine can check should never need a human reviewer.

## Tooling

| Tool | Checks | Runs |
|---|---|---|
| **ktlint** (via Spotless) | Formatting | `./gradlew spotlessCheck`; auto-fix with `spotlessApply` |
| **detekt** | Code smells, complexity, forbidden calls | `./gradlew detekt` |
| **Android Lint** | Android-specific issues, hard-coded strings, RTL | `./gradlew lint` |
| **Unit tests** | Behaviour | `./gradlew test` |

All four run in CI on every pull request.

## Naming

| Thing | Convention | Example |
|---|---|---|
| Use case | `VerbNounUseCase` | `GetForecastUseCase` |
| Repository | `NounRepository` / `NounRepositoryImpl` | `WeatherRepository` |
| DTO | `NounDto` | `ForecastDto` |
| Room entity | `NounEntity` | `SavedCityEntity` |
| UI state / event | `ScreenUiState` / `ScreenUiEvent` | `HomeUiState` |
| Composable screen | `NounScreen` | `HomeScreen` |
| Test | `` `given X, when Y, then Z` `` | `` `given stale cache, when loading, then fetches from network` `` |

## Kotlin rules

- Prefer `val` and immutable collections (`List`, not `MutableList`) in public APIs.
- No `!!`. Handle `null` explicitly.
- Default visibility in `:data` is `internal`.
- Use sealed interfaces for closed sets of states and errors, and exhaustive `when`.
- Coroutines: never use `GlobalScope`. Inject dispatchers; never hard-code
  `Dispatchers.IO` inside a class without a way to replace it in tests.
- Always rethrow `CancellationException`.
- Text casing always takes a locale: `uppercase(locale)`. **Never** the no-argument
  version (Turkish `i` bug; detekt rule).
- No magic numbers: `const val CACHE_TTL_MINUTES = 30`.
- Functions under ~30 lines; classes with one responsibility.

## Compose rules

- Screen composables receive `state` and an `onEvent` lambda; they do not receive the
  ViewModel. (A thin `HomeRoute` connects the two.)
- Every reusable composable takes a `modifier: Modifier = Modifier` as its first optional
  parameter.
- Every screen and component has `@Preview`s for light, dark, and Arabic RTL.
- Use `stringResource`, `start`/`end` paddings, and `Icons.AutoMirrored` for directional
  icons.
- Content descriptions for every meaningful icon; `null` for purely decorative ones.

## Comments

Code says *what*; comments say *why*. Write a comment when the reason is not obvious
(for example, "Open-Meteo omits `results` when empty, so default to an empty list"). Do
not comment the obvious.
