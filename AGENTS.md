# AGENTS.md — rules for AI coding agents

This file is read automatically by AI coding agents (Claude Code reads it through
`CLAUDE.md`; other tools read `AGENTS.md` directly). It is also a good one-page summary
for human contributors.

## Project
Atmos is an Android weather app (Kotlin, Jetpack Compose) built as a **tutorial**. Code
quality and clarity matter more than speed. Readers learn from every line.

## Before you write code
1. Read [`docs/PROGRESS.md`](docs/PROGRESS.md) to see the current step and what is next.
2. Read the step guide in [`docs/steps/`](docs/steps/) for the current step.
3. Find the user story in [`docs/product/USER_STORIES.md`](docs/product/USER_STORIES.md)
   that the task belongs to. If there is none, ask before building.
4. For anything non-trivial, **write a plan first** and wait for approval.

## Architecture (non-negotiable)
Full rules: [`docs/architecture/ARCHITECTURE.md`](docs/architecture/ARCHITECTURE.md).

- Modules: `:domain` (pure Kotlin) ← `:data` and `:app`. **`:domain` imports no Android,
  Retrofit, Room or Compose.**
- DTOs never leave `:data`. The UI never sees a DTO or an exception.
- Fallible functions return `AppResult<T>`; map exceptions to `DomainError` in `:data`;
  always rethrow `CancellationException`.
- One `StateFlow<UiState>` per ViewModel; screens are stateless and receive
  `state` + `onEvent`.
- Inject `Clock` and dispatchers; never call `System.currentTimeMillis()` or hard-code
  `Dispatchers.IO` in logic.

## Code rules
- Follow [`docs/process/CODING_STANDARDS.md`](docs/process/CODING_STANDARDS.md).
- No `!!`. No `GlobalScope`. `internal` by default in `:data`.
- **Never** `uppercase()`/`lowercase()` without a `Locale` (Turkish `i`).
- No hard-coded user-facing strings. Add every new string to **all three**:
  `values/strings.xml`, `values-tr/strings.xml`, `values-ar/strings.xml`.
- Use `start`/`end`, never `left`/`right`.

## Tests
- Write or update tests **with** the change, not after. Include at least one failure path.
- Data-layer tests use the recorded JSON in [`docs/api/samples/`](docs/api/samples/).
  Do not invent API responses.
- Run before saying "done":
  ```bash
  ./gradlew spotlessCheck checkLocaleSafeCasing lint test
  ```
  If you cannot run them, **say so**. Never claim tests pass without running them.

## Git
- Conventional Commits: `feat(search): …`, `fix(l10n): …`, `test(data): …`.
- Small commits, one logical change each.
- Update `docs/PROGRESS.md` at the end of every work session.

## Do not
- Add dependencies without an ADR or explicit approval.
- Add features that are not in the PRD (see its non-goals).
- Commit secrets, `local.properties` or keystores.
- Change an accepted ADR. Write a new one that supersedes it.
- Disable a lint rule or a test to make the build pass.

## When unsure
Stop and ask. A question costs one message; a wrong assumption costs a rewrite.
