# FlowCompose

A **Kotlin Multiplatform** library that brings the CSS Flexbox layout model to Compose-based UIs across Android, iOS, JVM, and Linux.

## Overview

FlowCompose wraps the battle-tested **flexbox** model from the web world and exposes it as a clean, type-safe Kotlin API. The goal is to let developers use familiar flex concepts — `flex-direction`, `justify-content`, `align-items`, `flex-grow`, `flex-basis`, etc. — when composing multiplatform UIs, without having to translate mental models between platforms.

## Supported Platforms

| Platform          | Status          |
|-------------------|-----------------|
| Android           | Planned         |
| iOS (arm64/x64)   | Planned         |
| JVM               | Planned         |
| Linux x64         | Planned         |

## Core API (WIP)

The library models flexbox through two primary style holders:

```kotlin
// Container — controls how children are laid out
val container = FlexContainerStyle(
    flexDirection = FlexDirection.Row,
    flexWrap = FlexWrap.Wrap,
    justifyContent = JustifyContent.SpaceBetween,
    alignItems = AlignItems.Center,
    columnGap = 8f,
    rowGap = 4f,
)

// Item — controls how each child participates in the flex layout
val item = FlexItemStyle(
    flexGrow = 1f,
    flexShrink = 0f,
    flexBasis = FlexBasis.Size(100f),
    alignSelf = AlignSelf.FlexEnd,
)
```

The full layout engine and Compose integration are under active development.

## Flexbox Concepts

| CSS Property       | FlowCompose Type     |
|--------------------|----------------------|
| `flex-direction`   | `FlexDirection`      |
| `flex-wrap`        | `FlexWrap`           |
| `justify-content`  | `JustifyContent`     |
| `align-items`      | `AlignItems`         |
| `align-self`       | `AlignSelf`          |
| `align-content`    | `AlignContent`       |
| `flex-grow`        | `Float` (item)       |
| `flex-shrink`      | `Float` (item)       |
| `flex-basis`       | `FlexBasis`          |
| `order`            | `Int` (item)         |
| `gap` / `row-gap` / `column-gap` | `Float` (container) |

## Comparison with Jetpack Compose's built-in FlexBox

Jetpack Compose ships an experimental `FlexBox` composable (`@ExperimentalFlexBoxApi`) in the adaptive layouts artifact. FlowCompose differs in several ways:

| | Compose `FlexBox` | FlowCompose `FlexBox` |
|---|---|---|
| **API stability** | `@ExperimentalFlexBoxApi` — subject to change | Stable, versioned |
| **`flex-direction`** | `Row`, `Column` | `Row`, `RowReverse`, `Column`, `ColumnReverse` |
| **`flex-wrap`** | `Wrap`, `NoWrap` | `Wrap`, `NoWrap`, `WrapReverse` |
| **`align-items`** | `Stretch`, `Start`, `End`, `Center` | + `Baseline` |
| **`align-content`** | `Start`, `End`, `Center`, `SpaceBetween`, `SpaceAround`, `SpaceEvenly` | + `Stretch` |
| **`justify-content`** | Full | Full |
| **Per-item: `grow`, `shrink`, `basis`, `align-self`, `order`** | Yes | Yes |
| **Platforms** | Android / Compose only | Android, iOS, JVM, Linux |
| **Extra dependency** | Requires `androidx` adaptive layouts artifact | Only requires `androidx.compose.ui` |
| **Layout engine** | Compose-internal | Pure-Kotlin `FlexboxEngine` — testable without a UI runtime |

Use FlowCompose when you need the full CSS Flexbox spec, multiplatform support, or a stable API you control.

## Installation

> Maven publishing configuration is in place; release artifacts will be available on Maven Central once the first stable version ships.

```kotlin
// build.gradle.kts
dependencies {
    implementation("io.joy.flowcompose:flowcompose:<version>")
}
```

## Project ID

`io.joy.flowcompose`

## License

Apache-2.0
