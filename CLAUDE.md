# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Test Commands

```bash
# Build all targets
./gradlew build

# Run tests per platform (CI matrix targets)
./gradlew jvmTest
./gradlew linuxX64Test
./gradlew testAndroidHostTest
./gradlew iosSimulatorArm64Test   # macOS only

# Run a single test class (JVM)
./gradlew jvmTest --tests "io.joy.flowcompose.SomeTest"

# Publish to Maven Central (requires signing secrets)
./gradlew publishToMavenCentral --no-configuration-cache
```

## Architecture

This is a **Kotlin Multiplatform library** (`:library` module) targeting Android, iOS (arm64/x64/simulatorArm64), JVM, and Linux x64.

**Source layout:**
- `library/src/commonMain/` — shared public API; all current code lives here
- Platform-specific source sets (`androidMain`, `iosMain`, `jvmMain`, `linuxX64Main`) are scaffolded but empty — platform implementations are deferred to later stages

**Core model (`FlexStyle.kt`):**
- `FlexContainerStyle` — properties for the flex container (`flexDirection`, `flexWrap`, `justifyContent`, `alignItems`, `alignContent`, `rowGap`, `columnGap`)
- `FlexItemStyle` — properties for each child item (`order`, `flexGrow`, `flexShrink`, `flexBasis`, `alignSelf`)
- `FlexBasis` — sealed class: `Auto` or `Size(Float)`, mirroring CSS `flex-basis`
- All CSS enum values (`FlexDirection`, `FlexWrap`, `JustifyContent`, `AlignItems`, `AlignSelf`, `AlignContent`) are modeled as Kotlin enums in `commonMain`

**Publishing:**
- Group: `io.joy.flowcompose`, artifact: `flowcompose`, version: `0.1.0`
- Vanniktech Maven Publish plugin handles Maven Central upload + signing
- GitHub Actions: `gradle.yml` runs tests on push/PR; `publish.yml` triggers on GitHub release events
- Signing credentials are injected via `ORG_GRADLE_PROJECT_signingInMemoryKey*` environment variables (see `publish.yml`)
