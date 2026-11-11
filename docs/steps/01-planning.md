# Step 01 — Planning: documents before code

**Branch:** `step-01-planning` · **Tag:** `v0.1.0` · **Next:** step 02, project structure

There is no code in this step, on purpose. A professional project starts by agreeing on
**what** to build, **why**, and **how the team will work**. An hour here saves a week of
rework later. This matters even more with an AI agent: it builds exactly what you
describe, so a vague description gets you vague software, fast.

## What this step adds

```
├── AGENTS.md                         ← rules for AI agents (and humans)
├── CLAUDE.md                         ← Claude Code entry point, imports AGENTS.md
├── .github/
│   ├── pull_request_template.md
│   └── ISSUE_TEMPLATE/ (feature.md, bug.md)
└── docs/
    ├── product/
    │   ├── PRD.md                    ← what and why: requirements with IDs
    │   └── USER_STORIES.md           ← Given/When/Then acceptance criteria
    ├── design/WIREFRAMES.md          ← screens and navigation
    ├── api/
    │   ├── API_CONTRACT.md           ← the boundary with Open-Meteo
    │   └── samples/*.json            ← REAL responses, used as test fixtures later
    ├── architecture/
    │   ├── ARCHITECTURE.md           ← Clean Architecture rules
    │   └── adr/0001…0006             ← one decision per file, with alternatives
    ├── process/
    │   ├── GIT_WORKFLOW.md
    │   ├── CODING_STANDARDS.md
    │   └── DEFINITION_OF_DONE.md
    ├── AI_WORKFLOW.md                ← how to work with an AI agent
    ├── ai/prompts/step-01-planning.md
    ├── ROADMAP.md
    ├── PROGRESS.md                   ← hand-over note between sessions
    └── steps/01-planning.md          ← you are here
```

## The documents, and the question each one answers

| Document | Question | Who reads it |
|---|---|---|
| [PRD](../product/PRD.md) | What are we building, for whom, and what are we *not* building? | Everyone |
| [User stories](../product/USER_STORIES.md) | How do we know a feature works? | Developers, testers, the AI agent |
| [Wireframes](../design/WIREFRAMES.md) | What is on each screen, and how do I move between them? | Developers, designers |
| [API contract](../api/API_CONTRACT.md) | What exactly does the server send, including the weird cases? | Data-layer developers |
| [Architecture](../architecture/ARCHITECTURE.md) | Where does each piece of code go, and what may it depend on? | Every developer, every PR review |
| [ADRs](../architecture/adr/) | Why did we choose X over Y? | Future you |
| [Git workflow](../process/GIT_WORKFLOW.md) | How do I branch, commit and get my code merged? | Every developer |
| [Coding standards](../process/CODING_STANDARDS.md) | What does good code look like here? | Every developer, the linters |
| [Definition of Done](../process/DEFINITION_OF_DONE.md) | When is a task really finished? | Every developer, reviewers |
| [AGENTS.md](../../AGENTS.md) | What must an AI agent always and never do here? | AI agents, new humans |

## Key ideas

### 1. Everything traces to an ID
Requirements have IDs (`F-05`, `NF-02`). User stories reference them (`US-04 (F-05)`).
Commits and PRs reference stories. When someone asks "why does search wait 300 ms?", you
can follow the chain: commit → US-04 → F-05.

### 2. Write down the non-goals
"No widgets, no alerts, no accounts in v1" prevents weeks of scope creep. It also stops an
AI agent from "helpfully" adding features nobody asked for.

### 3. Acceptance criteria become tests
> **Given** I type fewer than 2 characters, **then** no search is made.

becomes, in step 03:

```kotlin
@Test
fun `given query shorter than 2 characters, then no search is made`() = runTest { … }
```

### 4. Never trust an API you haven't called
We called Open-Meteo and saved the responses before writing a single line of the contract.
Two surprises appeared: an empty search has **no `results` key at all**, and the returned
coordinates are **snapped to a grid**. Both are now documented and will be covered by tests.

### 5. Decisions are recorded, not remembered
Six ADRs record the big choices, each with the alternatives we rejected and why. The
ADRs are never edited after acceptance; a new ADR replaces an old one.

### 6. Clean Architecture in one sentence
**Business rules in the middle depend on nothing; everything else depends on them.**
[ARCHITECTURE.md](../architecture/ARCHITECTURE.md) turns this into concrete rules, and step
02 makes the compiler enforce them.

## How a real team would run this step

1. **Kick-off meeting (1 h):** product owner presents the problem; the team asks questions.
2. **PRD draft (1–2 days):** product owner writes it, tech lead reviews feasibility.
3. **Story refinement (1 h per batch):** the team writes acceptance criteria together.
4. **Spike (½ day):** a developer calls the API and writes the contract.
5. **Architecture review:** the tech lead proposes the architecture and ADRs; the team
   comments in a pull request.
6. **Working agreement:** the team agrees on the Git workflow, standards and Definition of
   Done.

With an AI agent, steps 2, 4 and 5 go from days to hours, **but the review moments stay
human.**

## How the AI agent helped in this step

| The agent | The human |
|---|---|
| Proposed the API, the app name, the feature list and the seven-step plan | Chose the idea (weather), added Arabic and Turkish, approved the plan |
| Called the real Open-Meteo APIs and recorded the samples | — |
| Spotted the missing `results` key and the snapped coordinates in the real responses | — |
| Drafted the PRD, stories, wireframes, contract, architecture, ADRs, process docs | Reviewed the scope, non-goals and language decisions |
| Wrote `AGENTS.md` so future sessions follow the same rules | — |

**What went wrong, and what we learned:** the first requests to the API failed because
the cloud sandbox blocked the host. Instead of writing the contract from memory, the
agent asked for network access and then recorded real responses. **Lesson:** when the
agent cannot verify something, it should say so and fix the blocker, not guess.

The real prompts: [docs/ai/prompts/step-01-planning.md](../ai/prompts/step-01-planning.md).

## Exercise for you
1. Add a user story for a feature from the non-goals (for example, a home-screen widget).
   Which requirement ID would it need? Which ADR might it change?
2. Pick one ADR and argue for one of the rejected alternatives. Would you write a new ADR?
3. Try a vague and a specific prompt (see [AI_WORKFLOW.md §3](../AI_WORKFLOW.md)) with an AI
   agent and compare the results.

## Checklist: is step 01 done?
- [x] PRD with goals, non-goals and numbered requirements
- [x] User stories with Given/When/Then criteria
- [x] Wireframes and navigation
- [x] API contract written against real recorded responses
- [x] Architecture document and ADRs
- [x] Git workflow, coding standards, Definition of Done, PR/issue templates
- [x] `AGENTS.md`, `CLAUDE.md`, `AI_WORKFLOW.md`
- [x] `PROGRESS.md` updated

**Next:** [step 02, project structure](../ROADMAP.md): create the Gradle modules that make
the architecture a compile-time rule.
