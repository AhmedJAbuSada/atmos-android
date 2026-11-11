# Accessibility checklist (NF-03)

Automated checks catch some problems; people find the rest. Do this pass before every
release, on a real device.

## TalkBack
- [ ] Every screen can be used from start to end with TalkBack only.
- [ ] The big temperature, condition and details are read in a sensible order.
- [ ] Each hourly card and daily row is read as **one** sentence.
- [ ] Section titles ("Next 24 hours") are announced as headings.
- [ ] Removing a city works without the swipe gesture (the delete button).
- [ ] A daily row announces "expanded" / "collapsed".

## Text and touch
- [ ] Font size at maximum (200%): nothing is cut off or overlaps (previews cover this).
- [ ] Every tappable element is at least 48 × 48 dp.

## Colour
- [ ] Text contrast is at least 4.5:1 in light and dark (the theme colours were chosen
      for this; recheck after any colour change).
- [ ] Nothing is communicated by colour alone.

## Motion
- [ ] With "Remove animations" on, the app still works (the skeleton just stops pulsing).
