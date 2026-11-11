# Progress — hand-over note

> Read this first at the start of every work session (human or AI). Update it at the end.

**Current step:** 07, quality & release ✅ done. **Android v1.0.0 is complete.**
**Next up:** the same seven steps for iOS ([atmos-ios](https://github.com/AhmedJAbuSada/atmos-ios))
**Last updated:** 2026-11-11

## Status

| Step | Status | Branch | Tag |
|---|---|---|---|
| 01 Planning | ✅ Done | `step-01-planning` | `v0.1.0` |
| 02 Project structure | ✅ Done | `step-02-project-structure` | `v0.2.0` |
| 03 Domain layer | ✅ Done | `step-03-domain-layer` | `v0.3.0` |
| 04 Data layer | ✅ Done | `step-04-data-layer` | `v0.4.0` |
| 05 Presentation / UI | ✅ Done | `step-05-presentation-ui` | `v0.5.0` |
| 06 Integration | ✅ Done | `step-06-integration` | `v0.6.0` |
| 07 Quality & release | ✅ Done | `step-07-quality-release` | `v1.0.0` |

## After 1.0 (optional, not planned)
- Baseline Profiles (needs a physical or managed device).
- Roborazzi verify mode: fail CI on unexpected screenshot changes.
- Room migration tests when the schema reaches version 2.
- Native-speaker review of tr and ar; TalkBack pass on a real device
  (see `docs/process/`).

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
