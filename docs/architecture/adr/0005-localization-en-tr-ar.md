# ADR-0005: Localization — English, Turkish, Arabic (RTL)

- **Status:** Accepted
- **Date:** 2026-11-11

## Context
The audience includes Turkish and Arabic speakers. Turkish and Arabic each break common
assumptions: Turkish has special casing rules and a decimal comma; Arabic is right to
left.

## Decision
1. Ship `values/` (English, default), `values-tr/` and `values-ar/` string resources.
   **No hard-coded user-facing text.**
2. The language can follow the system or be chosen in the app, using the
   **per-app language API** (`AppCompatDelegate.setApplicationLocales`), declared in
   `locales_config.xml`.
3. **Unit conversion is a domain concern; formatting is a presentation concern.** The
   domain returns numbers in the user's unit; the UI formats them with the app locale
   (`12,5°` in Turkish, `12.5°` in English).
4. **Never** call `uppercase()`, `lowercase()` or `capitalize` without an explicit
   `Locale`. In Turkish, `"i".uppercase()` must give `"İ"`, not `"I"`. A lint rule and
   tests guard this.
5. Use `start`/`end`, never `left`/`right`, in layouts and padding. Directional icons use
   `autoMirrored` versions.
6. City names come translated from the geocoding API (`language=tr|ar|en`).
7. Arabic uses Western digits (0–9) by default, as most Arabic weather apps do. This is a
   product choice and easy to change.

## Consequences
- ✅ A real-world i18n example few portfolios show.
- ⚠️ Every UI PR needs an RTL check (in the PR template and previews).
- ⚠️ Translations need a native-speaker review before release (step 07).
