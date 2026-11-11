# Progress — hand-over note

> Read this first at the start of every work session (human or AI). Update it at the end.

**Current step:** 02, project structure ✅ done
**Next up:** step 03, domain layer (`step-03-domain-layer`)
**Last updated:** 2026-11-11

## Status

| Step | Status | Branch | Tag |
|---|---|---|---|
| 01 Planning | ✅ Done | `step-01-planning` | `v0.1.0` |
| 02 Project structure | ✅ Done | `step-02-project-structure` | `v0.2.0` |
| 03 Domain layer | ⏳ Next | `step-03-domain-layer` | — |
| 04 Data layer | ⬜ | — | — |
| 05 Presentation / UI | ⬜ | — | — |
| 06 Integration | ⬜ | — | — |
| 07 Quality & release | ⬜ | — | — |

## Next up: step 03 plan
Everything in `:domain`, pure Kotlin, tests first (one test per acceptance criterion where
it applies to the domain).
1. `common/`: `AppResult`, `DomainError`, `runCatchingDomain` (rethrows
   `CancellationException`), `Clock` interface.
2. `weather/`: `Forecast`, `CurrentWeather`, `HourlyForecast`, `DailyForecast`,
   `WeatherCondition` (+ WMO code mapping), `WeatherRepository` interface,
   `GetForecastUseCase` (cache freshness 30 min).
3. `location/`: `Coordinates`, `City`, `LocationRepository`, `CityRepository`,
   `SearchCitiesUseCase` (min 2 chars), `SaveCityUseCase` (no duplicates),
   `RemoveCityUseCase`, `ObserveSavedCitiesUseCase`.
4. `settings/`: `UserSettings`, `TemperatureUnit`, `WindSpeedUnit`, `AppLanguage`,
   `AppTheme`, `SettingsRepository`, unit conversion functions.
5. Tests with JUnit + coroutines-test + Turbine; Kover coverage ≥ 90% on `:domain`.
6. `docs/steps/03-domain-layer.md`, prompts file, update this file.

## Open decisions
- None.

## Notes for the next session
- Real Open-Meteo responses are in `docs/api/samples/`; use them as test fixtures in step 04.
- Package name: `dev.ahmedabusada.atmos`.
- Toolchain: AGP 9.5.1 (built-in Kotlin), Kotlin 2.4.21, Gradle 9.9.1, JDK 21,
  compileSdk/targetSdk 37, minSdk 26.
- Cloud sessions: the "Atmos" environment allows `api.open-meteo.com`,
  `geocoding-api.open-meteo.com` and `dl.google.com`. The Android SDK is not
  pre-installed; install the command-line tools to `/opt/android-sdk` and create
  `local.properties` with `sdk.dir=/opt/android-sdk`. Use `--max-workers=2` if the proxy
  returns HTTP 429.
- Git tags cannot be pushed from cloud sessions; create them as GitHub releases instead.
