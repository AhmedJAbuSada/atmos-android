# Localization review (ADR-0005)

The English, Turkish and Arabic strings were written with an AI agent. Before a release, a
**native speaker** checks each language with this list.

## For each language
- [ ] Every string in `values-tr/strings.xml` / `values-ar/strings.xml` reads naturally,
      not like a translation.
- [ ] Weather terms match what local weather services use (for example `Sağanak yağış`,
      `زخات مطر`).
- [ ] Plurals: Turkish (`one`, `other`), Arabic (`zero`, `one`, `two`, `few`, `many`,
      `other`). Check "Updated N hours ago" with N = 0, 1, 2, 3, 11 and 100.
- [ ] Numbers: Turkish decimal comma (`6,9 m/s`) and leading percent (`%69`); Arabic with
      Latin digits by product decision.
- [ ] Day names and times look right in the hourly and daily lists.
- [ ] City search returns names in the language (`İstanbul`, `اسطنبول`).

## Arabic layout
- [ ] Everything with a direction is mirrored: back arrow, lists, chevrons.
- [ ] Numbers, temperatures and weather icons are **not** mirrored.

## Tools
- Debug builds include pseudo-locales: **English (XA)** makes every string longer to show
  truncation; **Arabic (XB)** mirrors the layout with Latin text. Pick them in the device
  language settings (Developer options → enable pseudo-locales on some devices).
