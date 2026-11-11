# Prompts — step 01: planning

These are the real instructions given to the AI agent for this step, lightly cleaned up
(typos fixed). They are kept so you can see that **the human set the direction and the
agent filled in the detail**.

## 1. The idea (human)

> I need four public portfolio repos: Android, iOS, React Native and Flutter, all the same
> app. Use a free public API, a weather app. `main` has the full implementation, plus
> one branch per level of building a professional project: first documents and plan, then
> structure, then the "backend side" inside the mobile app, then the views, then
> connecting the web services. Every branch explains itself: how clean architecture must
> be, and how developers need to work, including with an AI agent helper.

## 2. Follow-up requirements (human)

> Add Arabic. Also add Turkish, because I'm based in Turkey.

> Write how you as an AI agent help. These repos should be a tutorial: anyone who reads
> the code should find it valuable and learn something.

## 3. What the agent proposed (and the human approved)

- **API:** Open-Meteo, because it needs no key, so there is no secret in a public repo.
- **Name:** Atmos. Repos `atmos-android`, `atmos-ios`, `atmos-react-native`,
  `atmos-flutter`.
- **Features:** current weather by location, city search, saved cities, 24-hour and
  7-day forecasts, °C/°F, offline cache, dark mode, English/Turkish/Arabic with RTL.
- **Seven step branches**, each cumulative, each with a guide in `docs/steps/`.
- **Android first**, end to end; the other platforms reuse the step-01 documents.

## 4. Agent tasks in this step

| Task | Instruction given to the agent |
|---|---|
| PRD | "Write a PRD with goals, non-goals, numbered functional and non-functional requirements, risks. Every later decision must trace to an ID." |
| User stories | "One story per functional requirement, with Given/When/Then criteria precise enough to become test names." |
| API contract | "Call the real Open-Meteo endpoints, save the responses as samples, and document the edge cases you find. Don't write the contract from memory." |
| Architecture | "Explain the dependency rule with a diagram, the module layout, per-layer rules, and the testing strategy. Write one ADR per major decision, with the alternatives rejected." |
| Process | "Git workflow with Conventional Commits, coding standards with tool enforcement, a Definition of Done, PR and issue templates with an RTL screenshot slot." |
| Agent rules | "Write `AGENTS.md` as the single source of truth for any agent; `CLAUDE.md` imports it." |

## 5. What the human reviewed
- Scope and non-goals (no widgets, no alerts, no accounts in v1).
- Language list and the Arabic digits decision (ADR-0005, point 7).
- That the plan can be repeated for all four platforms.
