# Release checklist

Copy this list into the release pull request and tick every box.

## Before
- [ ] `main` is green: format, casing, lint, unit tests, coverage gates, device tests,
      release smoke test.
- [ ] `versionCode` increased and `versionName` set in `app/build.gradle.kts`.
- [ ] `CHANGELOG.md` updated.
- [ ] Translations reviewed by native speakers ([LOCALIZATION_REVIEW.md](LOCALIZATION_REVIEW.md)).
- [ ] Accessibility pass done ([ACCESSIBILITY_CHECKLIST.md](ACCESSIBILITY_CHECKLIST.md)).
- [ ] Screenshots updated (commit with `[screenshots]` in the message).

## Build
The upload key is **never** in the repository. Set it in the shell or as CI secrets:

```bash
export ATMOS_KEYSTORE_PATH=/secure/place/atmos-upload.jks
export ATMOS_KEYSTORE_PASSWORD=…
export ATMOS_KEY_ALIAS=atmos
export ATMOS_KEY_PASSWORD=…
./gradlew :app:bundleRelease
```

- [ ] `app/build/outputs/bundle/release/app-release.aab` is signed (`jarsigner -verify`).
- [ ] Install from the bundle on a real device (`bundletool`) and run the manual smoke
      test: location granted, location denied, search, offline, Arabic, Turkish.

## After
- [ ] Tag the release (`v1.0.0`) or create a GitHub Release from `main`.
- [ ] Upload to the Play Console internal track first; promote after a day without crashes.
