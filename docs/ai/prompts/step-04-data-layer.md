# Prompts — step 04: data layer

## 1. The task (human → agent)

> Continue from `docs/PROGRESS.md`: build step 04, the data layer. Implement every
> repository interface from `:domain`. Follow `docs/api/API_CONTRACT.md` exactly. Tests
> must use the recorded samples in `docs/api/samples`, never hand-written JSON for the
> happy path. Keep everything `internal` except the Hilt bindings.

## 2. Sub-prompts

| Task | Instruction |
|---|---|
| DTOs | "Mirror the JSON field by field. Everything nullable with a default. `results` must default to an empty list: the API omits it." |
| Mapper | "Use the requested coordinates, not the response's. Zip parallel arrays by index and skip incomplete entries. Missing current conditions = invalid response." |
| Errors | "One function maps exceptions to `DomainError`. Check `SocketTimeoutException` before `IOException`. Test each branch." |
| HTTP client | "Timeouts 10 s / 15 s, User-Agent without personal data, retry network errors and 5xx twice with exponential back-off, never 4xx. Make the delay injectable for tests." |
| Cache | "Store the raw JSON and the fetch time keyed by rounded coordinates. Read it back through the same mapper. A corrupt entry is 'no cache'." |
| Cache key | "Format the key with `Locale.ROOT` and prove with a test that it is identical under tr, ar and en default locales." |
| Location | "Coarse only. Check permission, never request it. Fall back to the last known location." |
| Tests | "MockWebServer for the API, in-memory Room through Robolectric, real DataStore on a temp file. Mock only the Play services client." |
| Self-review | "Before committing, review the diff for anything added only to silence a warning." (This removed the `documentedTypes` workaround.) |

## 3. What the human decided
- Cache the raw JSON instead of normalized tables (simplicity over flexibility; the
  forecast is never queried by field).
- Keep the forecast cache inside the backed-up database: restoring stale data is harmless.
