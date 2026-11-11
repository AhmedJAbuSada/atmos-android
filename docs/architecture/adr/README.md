# Architecture Decision Records (ADRs)

An ADR is a short note that records **one** important decision: the context, the choice,
and the consequences. They are never edited after acceptance. If a decision changes, a new
ADR *supersedes* the old one, so the history of *why* is never lost.

> Six months from now someone (maybe you, maybe an AI agent) will ask "why didn't they
> use X?". The ADR answers in two minutes instead of a meeting.

| # | Title | Status |
|---|---|---|
| [0001](0001-clean-architecture-with-gradle-modules.md) | Clean Architecture enforced by Gradle modules | Accepted |
| [0002](0002-open-meteo-as-data-source.md) | Open-Meteo as the weather data source | Accepted |
| [0003](0003-jetpack-compose-and-mvvm-udf.md) | Jetpack Compose with MVVM and unidirectional data flow | Accepted |
| [0004](0004-hilt-retrofit-room-datastore.md) | Hilt, Retrofit + kotlinx.serialization, Room, DataStore | Accepted |
| [0005](0005-localization-en-tr-ar.md) | Localization: English, Turkish, Arabic (RTL) | Accepted |
| [0006](0006-errors-as-values.md) | Errors as values (`AppResult`) instead of exceptions | Accepted |

Template: copy [`template.md`](template.md).
