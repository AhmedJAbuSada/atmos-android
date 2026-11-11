# Changelog

All notable changes to Atmos. The format follows [Keep a Changelog](https://keepachangelog.com/),
and versions follow [Semantic Versioning](https://semver.org/). Each tutorial step is one
minor version until 1.0.0.

## [1.0.0] — step 07, quality & release
### Added
- End-to-end device test of the city-search journey against the real API.
- Release smoke test: the R8-shrunk release build is installed on an emulator and must
  start and keep running.
- Screenshot rendering of every main screen (Roborazzi) and images in `docs/screenshots`.
- Release signing from environment variables; pseudo-locales in debug builds.
- Release, accessibility and localization checklists.

## [0.6.0] — step 06, integration
### Added
- ViewModels for Home, Cities and Settings; navigation; location permission flow.
- Debounced city search, add / remove with Undo, offline banner.
### Fixed
- The app language now has one source of truth, so a choice made in Android's system
  settings is never undone.

## [0.5.0] — step 05, presentation
### Added
- Design system, stateless screens with previews in light, dark, Arabic RTL and Turkish
  at 200% font; strings in English, Turkish and Arabic; locale-aware formatting.
- Tap a day to see sunrise and sunset (US-03).

## [0.4.0] — step 04, data layer
### Added
- Open-Meteo forecast and geocoding clients, mappers tested against recorded responses,
  retry and error mapping, Room cache and saved cities, DataStore settings, location.

## [0.3.0] — step 03, domain layer
### Added
- Entities, use cases and repository interfaces in plain Kotlin; 90% coverage gate.

## [0.2.0] — step 02, project structure
### Added
- `:domain`, `:data` and `:app` modules, version catalog, Hilt, Spotless, strict Lint,
  locale-safe casing check, CI and Dependabot.

## [0.1.0] — step 01, planning
### Added
- PRD, user stories, wireframes, API contract, architecture guide, ADRs, team process,
  AI agent rules.
