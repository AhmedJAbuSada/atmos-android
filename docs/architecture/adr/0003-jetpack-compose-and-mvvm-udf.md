# ADR-0003: Jetpack Compose with MVVM and unidirectional data flow

- **Status:** Accepted
- **Date:** 2026-11-11

## Context
We need a UI toolkit and a presentation pattern that are modern, testable, and familiar to
Android developers reading this code.

## Decision
- **UI:** Jetpack Compose with Material 3.
- **Pattern:** MVVM with unidirectional data flow (UDF). Each screen has one ViewModel,
  which exposes a single `StateFlow<UiState>` and receives `UiEvent`s. One-off effects
  (for example, "show the Undo snackbar") go through a `Channel` exposed as a `Flow`.
- **Navigation:** Navigation Compose with type-safe routes.

## Alternatives considered
| Option | Why not |
|---|---|
| XML views | Legacy for new apps; more boilerplate. |
| MVI library (Orbit, Mavericks) | UDF with plain `StateFlow` gives the same benefits without a dependency to learn. |
| Multiple `StateFlow`s per screen | State can become inconsistent; one state object is easier to reason about and test. |

## Consequences
- ✅ The screen is a pure function of state, which makes previews and tests simple.
- ✅ Same mental model as SwiftUI, React and Flutter, so the four repos compare well.
- ⚠️ Recomposition performance needs care (stable types, `key`s in lists), covered in
  step 07.
