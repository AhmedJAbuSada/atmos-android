# Prompts — step 02: project structure

## 1. The task (human → agent)

> Continue from `docs/PROGRESS.md`. Build step 02, project structure, exactly as planned
> there. Follow `AGENTS.md`. Use the latest **stable** versions; look them up, don't use
> versions from memory. Prove each guard works. Commit in small logical pieces.

## 2. Sub-prompts the agent worked through

| Task | Instruction |
|---|---|
| Versions | "Query Google Maven, Maven Central and the Gradle Plugin Portal for the latest stable versions of AGP, Kotlin, KSP, Hilt, Compose BOM, Spotless and ktlint. Ignore alpha, beta and RC." |
| Modules | "Create `:domain` as a Kotlin/JVM module and `:data` as an Android library depending only on `:domain`. `:app` depends on both. No versions in module build files: use the catalog." |
| Guards | "Add Spotless with ktlint. Add a Gradle task that fails on `uppercase()`/`lowercase()` without a Locale. Make Android Lint treat warnings as errors." |
| Proof | "Temporarily add an Android import to `:domain` and a locale-less `uppercase()`. Run the build, capture the errors for the step guide, then remove them." |
| CI | "GitHub Actions running the same commands as local; cancel superseded runs; upload reports. Add Dependabot for Gradle and Actions." |
| Self-review | "Review your own diff before committing: unused code, comments that explain *what* instead of *why*, anything that breaks AGENTS.md." |

## 3. Decision the agent escalated

> detekt 1.23.x (latest stable) does not support Kotlin 2.4; detekt 2.0 is alpha. I
> propose ktlint + strict Android Lint + a small custom casing check, and an ADR recording
> why. Revisit detekt when 2.0 is stable.

→ Accepted as [ADR-0007](../../architecture/adr/0007-static-analysis-without-detekt.md).
