# Prompts — step 05: presentation / UI

## 1. The task (human → agent)

> Continue from `docs/PROGRESS.md`: build step 05. Screens are stateless (`state` +
> `onEvent`), with previews for every state in light, dark, Arabic RTL and a large font.
> Follow the step-01 wireframes. No hard-coded strings; every string in en, tr and ar.

## 2. Sub-prompts

| Task | Instruction |
|---|---|
| Plan change | "Propose whether ViewModels belong in this step or the next. The user asked for 'views' and 'connect the services' as separate levels." → moved to step 06. |
| Design system | "Colour schemes with AA contrast in both themes, dynamic colour on 12+, a type scale with tabular digits for temperatures, spacing tokens." |
| Formatter | "Pure Kotlin, one instance per locale. Test the real locale behaviour in en, tr, ar: decimal comma, percent position, day names, Latin digits for Arabic via `nu-latn`." |
| UI state | "A sealed interface per screen covering loading, empty, error and content. Events as a sealed interface. No logic in the screen." |
| Icons | "Vector drawables, no icon library. Weather icons in fixed colours that work on light and dark. Directional UI icons auto-mirrored." |
| Strings | "Write en, tr and ar. Arabic plurals need zero, one, two, few, many and other." |
| Accessibility | "One content description per card or row, headings for section titles, a visible alternative to every swipe, 48 dp targets." |
| Previews | "A multi-preview annotation, plus a `PreviewParameterProvider` per screen listing every state." |

## 3. What the human decided
- Keep the screens free of ViewModels in this step.
- Arabic translations need a native-speaker review before v1.0.
