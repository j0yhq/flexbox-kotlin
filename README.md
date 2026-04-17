# FlowCompose

A **Kotlin Multiplatform** library that brings the full CSS Flexbox layout model to Compose-based UIs across Android, iOS, JVM, and Linux.

## Status

**Pre-release** — core API, layout engine, and Compose integration are implemented and tested. Maven Central publishing is configured; the first stable artifact will be cut on the first release tag.

## Supported Platforms

| Platform              | Status      |
|-----------------------|-------------|
| Android               | Implemented |
| iOS (arm64 / x64 / simulatorArm64) | Implemented |
| JVM                   | Implemented |
| Linux x64             | Implemented |

## Installation

```kotlin
// build.gradle.kts
dependencies {
    implementation("io.joy.flowcompose:flowcompose:<version>")
}
```

## Usage

### Basic row layout

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

### Wrapping grid

```kotlin
FlexBox(
    containerStyle = FlexContainerStyle(
        flexWrap = FlexWrap.Wrap,
        justifyContent = JustifyContent.FlexStart,
    ).withGap(12f)   // sets both rowGap and columnGap
) {
    repeat(9) {
        Box(
            Modifier
                .flexItem(FlexItemStyle(flexBasis = FlexBasis.Percentage(0.3f)))
                .height(80.dp)
                .background(Color.LightGray)
        )
    }
}
```

### Growing and shrinking items

```kotlin
FlexBox(containerStyle = FlexContainerStyle()) {
    // Shorthand: flex(grow, shrink?, basis?) — mirrors CSS `flex: 1`
    Box(Modifier.flexItem(FlexItemStyle.flex(1f)).height(40.dp))
    Box(Modifier.flexItem(FlexItemStyle.flex(2f)).height(40.dp))   // twice as wide
    Box(Modifier.flexItem(FlexItemStyle(flexShrink = 0f,           // never shrinks
                                        flexBasis = FlexBasis.Size(120f))).height(40.dp))
}
```

### Per-item alignment override

```kotlin
FlexBox(
    containerStyle = FlexContainerStyle(
        alignItems = AlignItems.FlexStart,
        columnGap = 8f,
    )
) {
    Box(Modifier.flexItem().height(60.dp))
    Box(Modifier.flexItem(FlexItemStyle(alignSelf = AlignSelf.Center)).height(40.dp))
    Box(Modifier.flexItem(FlexItemStyle(alignSelf = AlignSelf.FlexEnd)).height(20.dp))
}
```

### Item ordering

```kotlin
FlexBox(containerStyle = FlexContainerStyle()) {
    Text("Third",  Modifier.flexItem(FlexItemStyle(order = 3)))
    Text("First",  Modifier.flexItem(FlexItemStyle(order = 1)))
    Text("Second", Modifier.flexItem(FlexItemStyle(order = 2)))
}
```

### Position — relative offset and absolute overlay

```kotlin
FlexBox(
    modifier = Modifier.size(200.dp),
    containerStyle = FlexContainerStyle(),
) {
    // Stays in flow but shifts 8px down without disturbing siblings
    Box(Modifier.flexItem(FlexItemStyle(position = Position.Relative(top = 8f))).size(40.dp))

    // Removed from flow; pinned to top-right corner
    Box(
        Modifier.flexItem(
            FlexItemStyle(position = Position.Absolute(top = 4f, right = 4f))
        ).size(24.dp)
    )
}
```

### Overflow and scrolling

```kotlin
// Horizontal scroll when content is wider than the container
FlexBox(
    modifier = Modifier.width(300.dp),
    containerStyle = FlexContainerStyle(overflow = Overflow.Scroll),
) {
    repeat(20) { Box(Modifier.flexItem().size(40.dp)) }
}

// Clip content to bounds without a scroll bar
FlexBox(
    modifier = Modifier.size(200.dp),
    containerStyle = FlexContainerStyle(overflow = Overflow.Hidden),
) { /* … */ }
```

## API Reference

### `FlexContainerStyle`

| Property         | Type              | Default               | CSS equivalent        |
|------------------|-------------------|-----------------------|-----------------------|
| `flexDirection`  | `FlexDirection`   | `Row`                 | `flex-direction`      |
| `flexWrap`       | `FlexWrap`        | `NoWrap`              | `flex-wrap`           |
| `justifyContent` | `JustifyContent`  | `FlexStart`           | `justify-content`     |
| `alignItems`     | `AlignItems`      | `Stretch`             | `align-items`         |
| `alignContent`   | `AlignContent`    | `Stretch`             | `align-content`       |
| `rowGap`         | `Float`           | `0f`                  | `row-gap`             |
| `columnGap`      | `Float`           | `0f`                  | `column-gap`          |
| `overflow`       | `Overflow`        | `Visible`             | `overflow`            |

**Extension:** `FlexContainerStyle.withGap(gap: Float)` sets both `rowGap` and `columnGap` at once.

### `FlexItemStyle`

| Property     | Type         | Default        | CSS equivalent  |
|--------------|--------------|----------------|-----------------|
| `order`      | `Int`        | `0`            | `order`         |
| `flexGrow`   | `Float`      | `0f`           | `flex-grow`     |
| `flexShrink` | `Float`      | `1f`           | `flex-shrink`   |
| `flexBasis`  | `FlexBasis`  | `Auto`         | `flex-basis`    |
| `alignSelf`  | `AlignSelf`  | `Auto`         | `align-self`    |
| `position`   | `Position`   | `Static`       | `position`      |

**Shorthand:** `FlexItemStyle.flex(grow, shrink = 1f, basis = Size(0f))` mirrors CSS `flex: <grow>`.

### `FlexBasis`

| Variant              | Description                                      | CSS equivalent        |
|----------------------|--------------------------------------------------|-----------------------|
| `FlexBasis.Auto`     | Natural (max-content) size                       | `flex-basis: auto`    |
| `FlexBasis.Size(px)` | Fixed pixel size                                 | `flex-basis: <length>`|
| `FlexBasis.Percentage(fraction)` | Fraction of container (0.0–1.0)  | `flex-basis: <percent>`|

### `Position`

| Variant                                            | Description                                     |
|----------------------------------------------------|-------------------------------------------------|
| `Position.Static`                                  | Normal flex flow, no offset (default)           |
| `Position.Relative(top?, left?, right?, bottom?)`  | In-flow; visual offset without affecting siblings|
| `Position.Absolute(top?, left?, right?, bottom?)`  | Out of flow; positioned against the container   |

### `Overflow`

`Visible` · `Hidden` · `Clip` · `Scroll` · `Auto`

---

## Headless usage (no Compose)

`FlexboxEngine` is a pure-Kotlin layout engine with no UI runtime dependency — useful for server-side layout, PDF rendering, or unit testing.

```kotlin
val layouts = FlexboxEngine.calculateLayout(
    container = FlexContainerStyle(
        flexWrap = FlexWrap.Wrap,
        justifyContent = JustifyContent.SpaceBetween,
    ),
    containerWidth = 300f,
    containerHeight = Float.MAX_VALUE,   // unconstrained height
    items = listOf(
        FlexItemInput(FlexItemStyle(flexGrow = 1f)) { _, _ -> Pair(100f, 40f) },
        FlexItemInput(FlexItemStyle(flexGrow = 2f)) { _, _ -> Pair(100f, 40f) },
    ),
)
// layouts[i].x, .y, .width, .height — all in pixels
```

---

## Comparison with Jetpack Compose's built-in FlexBox

| | Compose `FlexBox` (`@ExperimentalFlexBoxApi`) | FlowCompose `FlexBox` |
|---|---|---|
| **API stability** | Experimental — subject to change | Stable, versioned |
| **`flex-direction`** | `Row`, `Column` | `Row`, `RowReverse`, `Column`, `ColumnReverse` |
| **`flex-wrap`** | `Wrap`, `NoWrap` | `Wrap`, `NoWrap`, `WrapReverse` |
| **`align-items`** | `Stretch`, `Start`, `End`, `Center` | + `Baseline` |
| **`align-content`** | 6 values | + `Stretch` |
| **Per-item: `grow`, `shrink`, `basis`, `align-self`, `order`** | Yes | Yes |
| **`position`** | No | `Static`, `Relative`, `Absolute` |
| **`overflow`** | No | `Visible`, `Hidden`, `Clip`, `Scroll`, `Auto` |
| **`flex-basis: <percent>`** | No | Yes (`FlexBasis.Percentage`) |
| **Platforms** | Android / Compose only | Android, iOS, JVM, Linux |
| **Headless layout engine** | No | Yes (`FlexboxEngine`) |

---

## License

MIT © 2026 Joyfill
