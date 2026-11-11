# Prompts — step 06: integration

## 1. The task (human → agent)

> Continue from `docs/PROGRESS.md`: build step 06. Connect the step-05 screens to the
> use cases through ViewModels. ViewModels depend only on use cases and small
> interfaces, never on Android. Android-only concerns (permission dialog, snackbars,
> intents, navigation) live in a route composable per screen.

## 2. Sub-prompts

| Task | Instruction |
|---|---|
| Fakes | "Move the domain fakes to test fixtures so ViewModel tests reuse them. Don't copy them." |
| Home | "One StateFlow. Load on start and when the selected place changes. Permission denied → NeedsLocation. Outdated banner from the snapshot's age. Settings combined last, so unit changes don't reload." |
| Cities | "Debounce 300 ms and cancel stale searches with `transformLatest`. Search in the app language. Undo through a one-off effect. Removing the city shown on Home resets Home." |
| Language | "Who else can change the app language? Make sure we never undo a choice made in the system settings." |
| Permission | "Explanation first (the NeedsLocation state), then the system dialog, then app settings after two denials." |
| Tests | "One test per presentation-level acceptance criterion. Use virtual time for the debounce. Run the real use cases on the shared fakes." |
| Self-review | "Re-read every ViewModel asking: what happens on rotation, on failure while content is shown, and when the value changes from outside the app?" |

## 3. What the human decided
- The selected place is in memory only: the app always opens on the current location.
- The platform per-app locale is the single source of truth for the language.
