# ADR-0001: Clean Architecture enforced by Gradle modules

- **Status:** Accepted
- **Date:** 2026-11-11

## Context
The app is small, but it is a reference codebase and will grow (more features, four
platforms). We need business rules that are testable without a device, and boundaries that
stay intact when many people, and AI agents, contribute.

## Decision
Use three layers (domain, data, presentation) as **separate Gradle modules**: `:domain`
(pure Kotlin/JVM), `:data` (Android library), `:app` (Android application). Inside each
module, packages are organised by feature.

## Alternatives considered
| Option | Why not |
|---|---|
| Single module, layers as packages | Nothing enforces the dependency rule; a stray import compiles fine. |
| One module per feature *and* layer (`:feature:weather:domain`…) | Excellent for large teams; too much ceremony for four screens. It is the natural next step if the app grows. |
| No layers ("just ViewModel + Retrofit") | Fast to start, hard to test and change; not what we want to teach. |

## Consequences
- ✅ The compiler enforces the architecture. A Room import in `:domain` fails the build.
- ✅ Domain tests run on the JVM in milliseconds.
- ✅ Gradle builds modules in parallel and caches unchanged ones.
- ⚠️ More files (DTO + entity + mapper). We accept this cost for clear boundaries.
