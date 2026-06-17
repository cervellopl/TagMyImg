# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run Commands

All commands use the Gradle wrapper from the project root:

```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Install on connected device/emulator
./gradlew installDebug

# Run unit tests
./gradlew test

# Run a single unit test class
./gradlew test --tests "com.cervellopl.tagmyimg.ExampleUnitTest"

# Run instrumented (on-device) tests
./gradlew connectedAndroidTest

# Run a single instrumented test class
./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.cervellopl.tagmyimg.ExampleInstrumentedTest

# Lint
./gradlew lint
```

On Windows, use `gradlew.bat` instead of `./gradlew`.

## Project Structure

Single-module Android app (`app/`) with package `com.cervellopl.tagmyimg`.

- **minSdk 31** (Android 12), **targetSdk/compileSdk 36**
- Kotlin 2.0.21, AGP 8.13.2, Compose BOM 2024.09.00
- All dependency versions are managed in `gradle/libs.versions.toml` (version catalog)

## Architecture

This is an early-stage Android app built with **Jetpack Compose** and **Material3**. The current entry point is `MainActivity.kt`, which uses `setContent` with the `TagMyImgTheme` wrapper.

### Theme system (`ui/theme/`)
- `Theme.kt` — `TagMyImgTheme` composable; supports dynamic color (Android 12+), dark/light modes
- `Color.kt` — fallback color palette (purple/pink scheme)
- `Type.kt` — typography definitions

New screens/composables should be added under `app/src/main/java/com/cervellopl/tagmyimg/` and wrapped in `TagMyImgTheme`.
