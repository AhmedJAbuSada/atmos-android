# Prompts — step 03: domain layer

## 1. The task (human → agent)

> Continue from `docs/PROGRESS.md`: build step 03, the domain layer. Pure Kotlin only.
> For each user story, find the acceptance criteria that are business rules (not UI) and
> write one test per criterion, using the criterion as the test name. Then implement.
> Run the tests and the coverage report and show me the numbers.

## 2. Sub-prompts

| Task | Instruction |
|---|---|
| Common types | "Implement `AppResult`, `DomainError` and `runCatchingDomain` exactly as in ADR-0006. Prove with a test that `CancellationException` is rethrown." |
| Freshness | "Put the 30-minute rule in its own class with an injected `java.time.Clock`. Test the boundary: 29 minutes fresh, exactly 30 stale." |
| Cache-first | "Write `GetForecastUseCase` with a decision table in its KDoc covering fresh/stale/none × forceRefresh × network ok/fails. One test per row." |
| Search | "US-04 criteria that belong to the domain: min 2 characters (after trimming), language passed through, empty result is not an error, network error is returned. Debounce stays in the UI." |
| Favourites | "US-05: no duplicates, current location always first, undo = save again." |
| Tests | "Use in-memory fakes, not mocks. Keep test data builders small so each test shows only what it cares about." |
| Gate | "Add a Kover rule: `:domain` fails below 90% line coverage. Add it to CI." |
| Self-review | "Review your diff for anything that does not belong in this project." (This caught the detekt `@Suppress` leftover.) |

## 3. What the human decided
- Offline with cached data shows the data plus a banner, not an error screen.
- Even trivial operations (remove a city, observe settings) get a use case, so the rule
  for ViewModels stays simple: they depend on use cases only, never on repositories.
