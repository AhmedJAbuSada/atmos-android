# Progress — hand-over note

> Read this first at the start of every work session (human or AI). Update it at the end.

**Current step:** 03, domain layer ✅ done
**Next up:** step 04, data layer (`step-04-data-layer`)
**Last updated:** 2026-11-11

## Status

| Step | Status | Branch | Tag |
|---|---|---|---|
| 01 Planning | ✅ Done | `step-01-planning` | `v0.1.0` |
| 02 Project structure | ✅ Done | `step-02-project-structure` | `v0.2.0` |
| 03 Domain layer | ✅ Done | `step-03-domain-layer` | `v0.3.0` |
| 04 Data layer | ⏳ Next | `step-04-data-layer` | — |
| 05 Presentation / UI | ⬜ | — | — |
| 06 Integration | ⬜ | — | — |
| 07 Quality & release | ⬜ | — | — |

## Next up: step 04 plan
Everything in `:data`; implements the interfaces from `:domain`. Tests use the recorded
samples in `docs/api/samples/` (copy them into `data/src/test/resources/`).
1. **Remote:** Retrofit `ForecastApi` and `GeocodingApi`, kotlinx.serialization DTOs (all
   fields nullable; `results` defaults to an empty list), OkHttp with the timeouts, retry
   rule and User-Agent from API_CONTRACT §4.
2. **Mappers:** `ForecastDto.toDomain(requested coordinates, fetchedAt)` (zip parallel
   arrays safely, parse local times, `is_day` 0/1), `CityDto.toDomain()`. Tests against
   every sample file.
3. **Errors:** map `IOException`, `SocketTimeoutException`, `HttpException` and
   serialization errors to `DomainError` with `runCatchingDomain`.
4. **Local:** Room database with `SavedCityEntity` and a forecast cache keyed by the
   *requested* coordinates (rounded); DataStore Preferences for `UserSettings`.
5. **Location:** `FusedLocationProviderClient` behind `LocationRepository`; permission
   check returns `LocationPermissionDenied`.
6. **Repositories:** `WeatherRepositoryImpl`, `CityRepositoryImpl`,
   `SettingsRepositoryImpl`, `LocationRepositoryImpl`, all switching to an injected IO
   dispatcher.
7. **DI:** Hilt modules binding interfaces to implementations; `Clock` and dispatchers
   provided once.
8. **Tests:** MockWebServer with the samples (success, empty search, 400, 500, timeout),
   Room and DataStore tests, coverage ≥ 80% on `:data`.
9. Exclude the cache database from backups (`backup_rules.xml`,
   `data_extraction_rules.xml`).
10. `docs/steps/04-data-layer.md`, prompts file, update this file.

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
