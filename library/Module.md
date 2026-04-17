# Module FlowCompose

FlowCompose is a **Kotlin Multiplatform** library that brings the full CSS Flexbox layout model to
Compose-based UIs across Android, iOS, JVM, and Linux.

**Status:** Pre-release — core API, layout engine, and Compose integration are implemented and
tested. The first stable artifact will be cut on the first release tag.

## Installation

```kotlin
dependencies {
    implementation("io.joy.flowcompose:flowcompose:<version>")
}
```

## Entry points

- [io.joy.flowcompose.FlexBox] — the `@Composable` flex container
- [io.joy.flowcompose.FlexContainerStyle] / [io.joy.flowcompose.FlexItemStyle] — layout configuration
- [io.joy.flowcompose.FlexboxEngine] — platform-agnostic headless algorithm (no Compose dependency)
- [io.joy.flowcompose.FlexboxCalculator] — Swift/iOS convenience wrapper via Kotlin/Native

## Quick start

```kotlin
FlexBox(
    containerStyle = FlexContainerStyle(
        flexDirection = FlexDirection.Row,
        justifyContent = JustifyContent.SpaceBetween,
        alignItems = AlignItems.Center,
        columnGap = 8f,
    )
) {
    Text("Left",   Modifier.flexItem())
    Text("Center", Modifier.flexItem())
    Text("Right",  Modifier.flexItem())
}
```

## Headless usage (no Compose)

[io.joy.flowcompose.FlexboxEngine] is a pure-Kotlin layout engine with no UI runtime dependency —
useful for server-side layout, PDF rendering, or unit testing.

```kotlin
val layouts = FlexboxEngine.calculateLayout(
    container = FlexContainerStyle(
        flexWrap = FlexWrap.Wrap,
        justifyContent = JustifyContent.SpaceBetween,
    ),
    containerWidth = 300f,
    containerHeight = Float.MAX_VALUE,
    items = listOf(
        FlexItemInput(FlexItemStyle(flexGrow = 1f)) { _, _ -> Pair(100f, 40f) },
        FlexItemInput(FlexItemStyle(flexGrow = 2f)) { _, _ -> Pair(100f, 40f) },
    ),
)
// layouts[i].x, .y, .width, .height — all in pixels
```

# Package io.joy.flowcompose
