#!/usr/bin/env bash
# Installs the R8-shrunk release APK on a connected emulator and checks that it starts and
# keeps running. R8 can remove classes that are only reached by reflection (JSON, DI,
# navigation routes); this catches that before users do.
#
# The release APK is signed here with a throw-away key generated on the spot, only to make
# it installable on the emulator. Real releases are signed with the keystore from
# ATMOS_KEYSTORE_PATH (see docs/process/RELEASE_CHECKLIST.md).
set -euo pipefail

./gradlew --no-configuration-cache :app:assembleRelease

apk=app/build/outputs/apk/release/app-release-unsigned.apk
signed=app/build/outputs/apk/release/app-release-smoke.apk
keystore=build/smoke-test.keystore
mkdir -p build
[ -f "$keystore" ] || keytool -genkeypair -keystore "$keystore" -storepass smoketest -keypass smoketest \
  -alias smoke -keyalg RSA -keysize 2048 -validity 1 -dname "CN=Atmos smoke test" >/dev/null 2>&1
build_tools=$(ls -d "$ANDROID_HOME"/build-tools/* | sort -V | tail -1)
"$build_tools/apksigner" sign --ks "$keystore" --ks-pass pass:smoketest \
  --key-pass pass:smoketest --ks-key-alias smoke --out "$signed" "$apk"

adb uninstall dev.ahmedabusada.atmos >/dev/null 2>&1 || true
adb install -r "$signed"
adb logcat -c
adb shell am start -W -n dev.ahmedabusada.atmos/.MainActivity
sleep 15

if adb logcat -d -b crash | grep -q "dev.ahmedabusada.atmos"; then
  echo "The release build crashed:"
  adb logcat -d -b crash
  exit 1
fi
adb shell pidof dev.ahmedabusada.atmos >/dev/null || { echo "The release build is not running."; exit 1; }
echo "Release build started and kept running."
