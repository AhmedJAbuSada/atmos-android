# ADR-0007: Static analysis — ktlint, Android Lint and a custom casing check, no detekt (for now)

- **Status:** Accepted
- **Date:** 2026-11-11

## Context
The step-01 coding standards planned **detekt** for code smells and for one rule we care
about a lot: forbidding `uppercase()`/`lowercase()` without a `Locale` (ADR-0005). While
setting up step 02, we checked the current versions:

- the latest **stable** detekt (1.23.x) is built against an old Kotlin compiler and does
  not support the Kotlin 2.4 / AGP 9 toolchain this project uses;
- detekt **2.0**, which supports it, is still an **alpha**.

We do not want an alpha tool in a reference project's quality gate.

## Decision
- **Formatting and style:** ktlint, through Spotless.
- **Android-specific checks:** Android Lint with `warningsAsErrors = true`.
- **Locale-safe casing:** a ~25-line Gradle task, `checkLocaleSafeCasing`, in the root
  `build.gradle.kts`. It scans `src/main` for casing calls without a `Locale` and fails
  the build. It runs as part of every module's `check` and in CI.
- Revisit detekt when 2.0 is stable (a new ADR will supersede this one).

## Alternatives considered
| Option | Why not |
|---|---|
| detekt 1.23.x | Incompatible with the Kotlin 2.4 toolchain. |
| detekt 2.0 alpha | Not stable; a quality gate must be reliable. |
| Custom Android Lint rule | The "proper" way, but needs a separate lint module and much more code than the problem deserves. |
| Rely on code review | Humans and AI agents both miss it. A rule a machine can check should be checked by a machine. |

## Consequences
- ✅ Every check in the pipeline is stable and fast.
- ✅ Shows that a small, well-placed script can enforce a team rule.
- ⚠️ The casing check is text-based: it can be fooled (for example by a method reference
  `String::uppercase`). It is a safety net, not a proof; code review still applies.
- ⚠️ We lose detekt's complexity rules for now; reviews cover them.

## How this ADR came about
This decision changed a plan from step 01, which is normal: plans meet reality. The step-01
documents were updated to point here, and the history stays visible in Git. This is
exactly what ADRs are for.
