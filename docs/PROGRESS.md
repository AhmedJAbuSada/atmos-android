# Progress — hand-over note

> Read this first at the start of every work session (human or AI). Update it at the end.

**Current step:** 06, integration ✅ done
**Next up:** step 07, quality & release (`step-07-quality-release`)
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
| 07 Quality & release | ⏳ Next | `step-07-quality-release` | — |

## Next up: step 07 plan
1. **Screenshot tests** (Roborazzi on Robolectric) for every screen state in light, dark,
   Arabic RTL and Turkish 200% font; images committed under `app/src/test/screenshots`
   and shown in the README.
2. **UI tests** (Compose UI test on Robolectric) for the key flows: search → add → undo,
   settings unit change, NeedsLocation actions.
3. **Accessibility:** automated checks in the UI tests (touch targets, content
   descriptions); manual TalkBack pass checklist.
4. **Performance:** Baseline Profile for startup and scrolling; check recomposition
   counts of the hourly row; R8 full mode in release.
5. **Localization review:** native-speaker review checklist for tr and ar; pseudo-locale
   (`en-XA`, `ar-XB`) builds enabled for debug.
6. **Release build:** signing config read from environment variables (never committed),
   `bundleRelease` in CI, version 1.0.0, changelog.
7. **Room migration test** setup for future schema versions.
8. Merge to `main` as v1.0.0; update README with screenshots and the final numbers.
9. `docs/steps/07-quality-release.md`, prompts file, update this file.

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
