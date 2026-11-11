# Progress — hand-over note

> Read this first at the start of every work session (human or AI). Update it at the end.

**Current step:** 01, planning ✅ done
**Next up:** step 02, project structure (`step-02-project-structure`)
**Last updated:** 2026-11-11

## Status

| Step | Status | Branch | Tag |
|---|---|---|---|
| 01 Planning | ✅ Done | `step-01-planning` | `v0.1.0` |
| 02 Project structure | ⏳ Next | `step-02-project-structure` | — |
| 03 Domain layer | ⬜ | — | — |
| 04 Data layer | ⬜ | — | — |
| 05 Presentation / UI | ⬜ | — | — |
| 06 Integration | ⬜ | — | — |
| 07 Quality & release | ⬜ | — | — |

## Next up: step 02 plan
1. Gradle wrapper, `settings.gradle.kts`, version catalog `gradle/libs.versions.toml`.
2. Modules `:domain` (Kotlin/JVM), `:data` (Android library), `:app` (Android app,
   Compose), with the dependency rule from ADR-0001.
3. Hilt set up in `:app`; `@HiltAndroidApp` application class; an empty `MainActivity`
   with a Compose "Hello Atmos".
4. Spotless (ktlint), detekt (with a rule forbidding locale-less `uppercase`/`lowercase`),
   Android Lint.
5. GitHub Actions: `spotlessCheck detekt lint test assembleDebug` on every PR.
6. `docs/steps/02-project-structure.md` and `docs/ai/prompts/step-02-project-structure.md`.

## Open decisions
- None.

## Notes for the next session
- Real Open-Meteo responses are in `docs/api/samples/`; use them as test fixtures.
- Package name: `dev.ahmedabusada.atmos`.
- The cloud environment "Atmos" allows `api.open-meteo.com`,
  `geocoding-api.open-meteo.com` and `dl.google.com` (Android SDK).
