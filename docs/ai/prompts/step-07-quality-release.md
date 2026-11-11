# Prompts — step 07: quality & release

## 1. The task (human → agent)

> Continue from `docs/PROGRESS.md`: build step 07. Prove the app works the way a user
> will get it: on a real Android system, with real data, in the shrunk release build.
> Make the release process safe (no keys in the repository) and write the checklists a
> team needs before shipping. Only claim what was actually verified.

## 2. Sub-prompts

| Task | Instruction |
|---|---|
| Release build | "Enable R8 and resource shrinking. Signing from environment variables only; without them the build is unsigned. Pseudo-locales in debug." |
| Device test | "One end-to-end journey on an emulator against the real API, for the user without location permission. No fakes." |
| Release smoke | "Install the shrunk release APK on the emulator and fail if it crashes or stops within 15 seconds. R8 can remove reflection-only classes; catch that in CI." |
| Screenshots | "Render the real screens on the JVM with Roborazzi in light, dark, Arabic and Turkish. Record them in CI and commit them to `docs/screenshots` for the README." |
| Checklists | "Release, accessibility (TalkBack, 200% font, contrast, 48 dp) and localization review (plurals, number formats, RTL) checklists." |
| Honesty | "In the guide, separate what CI verified from what still needs a human: native-speaker review, TalkBack pass, a real-device test." |

## 3. What the human decided
- The emulator job runs on every push: slower CI, but every snapshot is proven on Android.
- Baseline Profiles are a documented next step, not part of v1.0 (they need a physical
  device or a managed device to generate meaningful profiles).
