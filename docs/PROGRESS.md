# Progress — hand-over note

> Read this first at the start of every work session (human or AI). Update it at the end.

**Current step:** 05, presentation / UI ✅ done
**Next up:** step 06, integration (`step-06-integration`)
**Last updated:** 2026-11-11

## Status

| Step | Status | Branch | Tag |
|---|---|---|---|
| 01 Planning | ✅ Done | `step-01-planning` | `v0.1.0` |
| 02 Project structure | ✅ Done | `step-02-project-structure` | `v0.2.0` |
| 03 Domain layer | ✅ Done | `step-03-domain-layer` | `v0.3.0` |
| 04 Data layer | ✅ Done | `step-04-data-layer` | `v0.4.0` |
| 05 Presentation / UI | ✅ Done | `step-05-presentation-ui` | `v0.5.0` |
| 06 Integration | ⏳ Next | `step-06-integration` | — |
| 07 Quality & release | ⬜ | — | — |

## Next up: step 06 plan
Connect the step-05 screens to the step-03 use cases (step-04 implementations via Hilt).
1. **Test fixtures:** move the domain fakes to `domain/src/testFixtures` (`java-test-fixtures`)
   so ViewModel tests in `:app` reuse them.
2. **ViewModels** (`@HiltViewModel`, one `StateFlow<UiState>`, events in, one-off effects
   out through a `Channel`):
   - `HomeViewModel`: selected place (current location or a saved city), load on start,
     refresh, retry, outdated banner from `ForecastSnapshot.refreshError` + age,
     settings flow combined in.
   - `CitiesViewModel`: query with 300 ms debounce (`debounce` + `mapLatest`), search in
     the app language, add / remove with Undo snackbar effect, places list with cached
     temperatures.
   - `SettingsViewModel`: observe and update settings.
3. **Selected place** shared between Home and Cities (a small `SelectedPlaceStore` in
   `:app`, in memory; the current location by default).
4. **Language:** apply `AppLanguage` with `AppCompatDelegate.setApplicationLocales`
   (needs `androidx.appcompat`), and resolve `System` to the configuration locale for
   search.
5. **Location permission** with `rememberLauncherForActivityResult`, rationale first
   (US-01), `NeedsLocation` when denied.
6. **Navigation Compose** with type-safe routes: Home → Cities, Home → Settings → About.
7. **Theme** from settings in `MainActivity`; links opened with a Custom Tab / browser
   intent.
8. **Tests:** ViewModel tests with Turbine and fakes for every acceptance criterion that
   lives in the presentation layer (debounce, undo, refresh state).
9. `docs/steps/06-integration.md`, prompts file, update this file.

## Open decisions
- None.

## Notes for the next session
- Real Open-Meteo responses are in `docs/api/samples/`; `:data` tests read them directly.
- Package name: `dev.ahmedabusada.atmos`.
- Toolchain: AGP 9.5.1 (built-in Kotlin), Kotlin 2.4.21, Gradle 9.9.1, JDK 21,
  compileSdk/targetSdk 37, minSdk 26.
- Cloud sessions: the "Atmos" environment allows `api.open-meteo.com`,
  `geocoding-api.open-meteo.com` and `dl.google.com`. The Android SDK is not
  pre-installed; install the command-line tools to `/opt/android-sdk` and create
  `local.properties` with `sdk.dir=/opt/android-sdk`. Use `--max-workers=2` if the proxy
  returns HTTP 429. If Maven Central keeps answering 429 for large jars (Robolectric), push
  the branch and let GitHub Actions run the tests: CI runs on `step-*` branches.
- Git tags cannot be pushed from cloud sessions; create them as GitHub releases instead.
