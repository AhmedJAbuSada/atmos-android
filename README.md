# Atmos — Android

**A weather app built step by step, as a public tutorial on professional Android
development with Clean Architecture and an AI coding agent.**

<!-- step-banner:start -->
## 📍 You are on Step 05 of 7 — Presentation: the views, before any data

`step-05-presentation-ui` · [← Step 04](https://github.com/AhmedJAbuSada/atmos-android/tree/step-04-data-layer) · [Step 06 →](https://github.com/AhmedJAbuSada/atmos-android/tree/step-06-integration) · [All steps](docs/ROADMAP.md)

**What this step is.** Every screen as a **stateless function of a UI state**, with realistic fake data and no ViewModel yet: a design system, Home/Cities/Settings/About in Compose, a locale-aware formatter, hand-made vector icons, strings in English/Turkish/Arabic, and previews of every state in light, dark, Arabic RTL and Turkish at 200% font.

**Why it matters.** When a screen can only show what it is given, it can be designed, previewed and reviewed on its own, in every language, before the plumbing exists. Building the UI apart from the data is how teams work in parallel.

**What you will find in this branch**

| Where | What |
|---|---|
| `app/…/ui/theme/` | Colours, type, spacing tokens |
| `app/…/ui/format/WeatherFormatter.kt` | `%69` in Turkish, Latin digits in Arabic |
| `app/…/feature/*/…Screen.kt` | Stateless screens + previews |
| `app/src/main/res/values*/strings.xml` | en, tr, ar (Arabic has 6 plural forms) |

**How to study this step**

1. **Read the full guide:** [docs/steps/05-presentation-ui.md](docs/steps/05-presentation-ui.md): key ideas, diagrams, and *how the AI agent helped* (including what went wrong).
2. **See exactly what this step changed:** [compare with step 04](https://github.com/AhmedJAbuSada/atmos-android/compare/step-04-data-layer...step-05-presentation-ui).
3. **Run it:**
   ```bash
   ./gradlew :app:testDebugUnitTest
   ./gradlew :app:installDebug   # opens on demo data
   ```
4. **Try the exercise:** Preview the Home screen at 320 dp width in Arabic. What breaks first?

> This README shows the project *as it was at this step*. The finished app is on
> [`main`](https://github.com/AhmedJAbuSada/atmos-android).

---
<!-- step-banner:end -->

![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?logo=jetpackcompose&logoColor=white)
![Clean Architecture](https://img.shields.io/badge/Clean%20Architecture-3%20layers-success)
![Languages](https://img.shields.io/badge/i18n-EN%20%7C%20TR%20%7C%20AR%20(RTL)-blue)
![License](https://img.shields.io/badge/license-MIT-green)

[![CI](https://github.com/AhmedJAbuSada/atmos-android/actions/workflows/ci.yml/badge.svg)](https://github.com/AhmedJAbuSada/atmos-android/actions/workflows/ci.yml)

> 🚧 **In progress:** steps 01–05 of 7 are done. Follow along in [PROGRESS.md](docs/PROGRESS.md).

## Why this repository exists

Most sample apps show only the finished code. This one shows **how a professional
project grows**, one level per branch, from documents to a released app, with each step
explained:

| Step | Branch | What you learn |
|---|---|---|
| 01 | [`step-01-planning`](../../tree/step-01-planning) | PRD, user stories, API contract, architecture, ADRs, team process, AI agent rules |
| 02 | [`step-02-project-structure`](../../tree/step-02-project-structure) | Gradle modules that enforce Clean Architecture, DI, linting, CI |
| 03 | [`step-03-domain-layer`](../../tree/step-03-domain-layer) | Entities, use cases, errors as values, pure unit tests |
| 04 | [`step-04-data-layer`](../../tree/step-04-data-layer) | Retrofit, mappers, Room cache, DataStore, cache-first repositories |
| 05 | [`step-05-presentation-ui`](../../tree/step-05-presentation-ui) | Design system, stateless Compose screens, previews (dark, RTL, large font) |
| 06 | `step-06-integration` | ViewModels, real data, location, search, offline mode, navigation |
| 07 | `step-07-quality-release` | Localization, accessibility, performance, UI tests, release |
| — | `main` | The finished app |

Full details: [ROADMAP.md](docs/ROADMAP.md).

## The same app on four platforms

| Platform | Repository |
|---|---|
| **Android** (Kotlin, Compose) | you are here |
| iOS (Swift, SwiftUI) | [atmos-ios](https://github.com/AhmedJAbuSada/atmos-ios) |
| React Native (TypeScript) | [atmos-react-native](https://github.com/AhmedJAbuSada/atmos-react-native) |
| Flutter (Dart) | [atmos-flutter](https://github.com/AhmedJAbuSada/atmos-flutter) |

Same features, same architecture, same step-by-step plan. Compare how each platform
solves the same problem.

## Features (v1.0)

- 📍 Current weather at your location
- 🕐 Next 24 hours and 🗓 next 7 days
- 🔍 City search with names in your language, ⭐ saved cities
- 📴 Works offline with the last known data
- 🌡 °C / °F, km/h / m/s / mph
- 🌙 Dark mode
- 🌍 **English, Türkçe, العربية** (full right-to-left layout)

Weather data by [Open-Meteo.com](https://open-meteo.com/). No API key is needed:
clone and run.

## Architecture at a glance

```mermaid
flowchart LR
    app[":app<br/>Compose UI + ViewModels"] --> domain[":domain<br/>pure Kotlin<br/>entities, use cases"]
    data[":data<br/>Retrofit, Room, DataStore"] --> domain
    app --> data
```

The domain depends on nothing. The compiler enforces it, because each layer is its own
Gradle module. Read [ARCHITECTURE.md](docs/architecture/ARCHITECTURE.md).

## 🤖 Built with an AI agent, the professional way

This project is built by a developer working with **Claude Code**. The repository shows
the whole method, not just the result:

- [**AI_WORKFLOW.md**](docs/AI_WORKFLOW.md): the spec → plan → review → build → verify →
  review loop, what to delegate and what not, and how to verify AI output.
- [**AGENTS.md**](AGENTS.md): the rules file every AI session loads, so it follows the
  architecture.
- [**docs/ai/prompts/**](docs/ai/prompts/): the real prompts used at each step.
- Every step guide ends with **"How the AI agent helped in this step"**, including what
  went wrong.
- Commits written with the agent carry a `Co-Authored-By: Claude` line.

## Run it

Requirements: Android Studio (latest stable), or JDK 21 plus the Android SDK.

```bash
git clone https://github.com/AhmedJAbuSada/atmos-android.git
cd atmos-android
./gradlew assembleDebug
```

Quality checks, the same ones CI runs:

```bash
./gradlew spotlessCheck checkLocaleSafeCasing lint test
```

## Start reading

1. [Step 01 guide](docs/steps/01-planning.md)
2. [PRD](docs/product/PRD.md) → [User stories](docs/product/USER_STORIES.md)
3. [Architecture](docs/architecture/ARCHITECTURE.md) → [ADRs](docs/architecture/adr/)
4. [AI workflow](docs/AI_WORKFLOW.md)

## Author

**Ahmed J. AbuSada**, senior mobile and front-end developer.
GitHub: [@AhmedJAbuSada](https://github.com/AhmedJAbuSada)

## License

[MIT](LICENSE). Weather data © Open-Meteo.com, [CC BY 4.0](https://creativecommons.org/licenses/by/4.0/).
