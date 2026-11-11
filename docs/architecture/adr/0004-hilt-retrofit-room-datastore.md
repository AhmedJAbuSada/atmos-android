# ADR-0004: Hilt, Retrofit + kotlinx.serialization, Room, DataStore

- **Status:** Accepted
- **Date:** 2026-11-11

## Context
We need dependency injection, an HTTP client, a cache for forecasts and saved cities, and
storage for settings.

## Decision
| Need | Choice |
|---|---|
| Dependency injection | **Hilt** |
| HTTP | **Retrofit** + **OkHttp** + **kotlinx.serialization** |
| Cache and saved cities | **Room** |
| Settings | **Jetpack DataStore** (Preferences) |
| Location | **FusedLocationProviderClient** (Play Services) behind a domain interface |

## Alternatives considered
| Option | Why not |
|---|---|
| Koin | Simpler, but resolves at runtime; Hilt catches missing bindings at compile time. |
| Ktor client | Great (and multiplatform), but Retrofit is still what most Android teams use. |
| Gson / Moshi | kotlinx.serialization is Kotlin-first, null-safe and reflection-free. |
| SharedPreferences | Synchronous API on the main thread; DataStore is its official replacement. |

## Consequences
- ✅ Every choice is the mainstream Android choice, so readers learn transferable skills.
- ✅ All of them sit behind domain interfaces; replacing one touches only `:data`.
- ⚠️ Hilt adds annotation processing (KSP) to build time.
