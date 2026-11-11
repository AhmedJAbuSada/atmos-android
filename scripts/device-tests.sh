#!/usr/bin/env bash
# Everything that needs a running emulator, in one script so it runs in bash (the
# emulator action runs its `script` lines with plain `sh`). Output is also saved to
# device.log so CI can report a failure.
set -euo pipefail

{
  ./gradlew connectedDebugAndroidTest
  ./scripts/release-smoke-test.sh
} 2>&1 | tee device.log
