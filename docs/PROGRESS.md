# Progress — hand-over note

> Read this first at the start of every work session (human or AI). Update it at the end.

**Current step:** 04, data layer ✅ done
**Next up:** step 05, presentation / UI (`step-05-presentation-ui`)
**Last updated:** 2026-11-11

## Status

| Step | Status | Branch | Tag |
|---|---|---|---|
| 01 Planning | ✅ Done | `step-01-planning` | `v0.1.0` |
| 02 Project structure | ✅ Done | `step-02-project-structure` | `v0.2.0` |
| 03 Domain layer | ✅ Done | `step-03-domain-layer` | `v0.3.0` |
| 04 Data layer | ✅ Done | `step-04-data-layer` | `v0.4.0` |
| 05 Presentation / UI | ⏳ Next | `step-05-presentation-ui` | — |
| 06 Integration | ⬜ | — | — |
| 07 Quality & release | ⬜ | — | — |

## Next up: step 05 plan
Everything in `:app`. Screens use **fake data** (preview providers) in this step; wiring to
the real use cases is step 06.
1. **Design system** (`ui/theme/`): colour schemes (light, dark, dynamic colour on
   Android 12+), typography, spacing tokens, shapes; `AtmosTheme`.
2. **Formatting** (`ui/format/`): temperature, wind, percentage, hour and day names with
   the **app locale** (decimal comma in Turkish, 12/24 h from the device); unit tests in
   en, tr and ar.
3. **Condition UI** (`ui/weather/`): `WeatherCondition` → localized label + icon, with a
   night variant for clear skies.
4. **Error UI**: `DomainError` → localized message.
5. **Screens** (stateless, `state` + `onEvent`): Home (current, hourly row, daily list,
   outdated banner, skeleton loading, error with retry), Cities (search field, results,
   saved list with swipe-to-remove and Undo), Settings, About.
6. **ViewModels** with one `StateFlow<UiState>` each, tested with fake use cases and
   Turbine; search debounce 300 ms in `CitiesViewModel`.
7. **Strings** for every screen in `values`, `values-tr`, `values-ar`.
8. **Previews**: light, dark, Arabic RTL and font scale 2× for every screen.
9. `docs/steps/05-presentation-ui.md`, prompts file, update this file.

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
