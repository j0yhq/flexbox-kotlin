# Package io.joy.flex

Public API for the Flex flexbox layout library.

## Compose usage

Use [FlexBox] as a drop-in layout composable. Attach flex item properties to each child
with `Modifier.flexItem` inside the [FlexBoxScope]:

```kotlin
FlexBox(
    containerStyle = FlexContainerStyle(
        justifyContent = JustifyContent.SpaceBetween,
        flexWrap = FlexWrap.Wrap,
    )
) {
    Box(Modifier.flexItem(FlexItemStyle(flexGrow = 1f)).height(48.dp))
    Box(Modifier.flexItem(FlexItemStyle(flexGrow = 2f)).height(48.dp))
}
```

## Non-Compose / iOS usage

Call [FlexboxEngine.calculateLayout] directly with a list of [FlexItemInput] descriptors.
On iOS, the [FlexboxCalculator] convenience class wraps the engine for use from
SwiftUI's `Layout` protocol.

## Type overview

| Type                 | Role                                                                |
|----------------------|---------------------------------------------------------------------|
| [FlexBox]            | `@Composable` flex container                                        |
| [FlexBoxScope]       | Content scope — exposes `Modifier.flexItem`                         |
| [FlexContainerStyle] | Flex container properties (direction, wrap, gaps, alignment)        |
| [FlexItemStyle]      | Per-child flex item properties (grow, shrink, basis, order)         |
| [FlexBasis]          | Sealed class for `flex-basis`: [FlexBasis.Auto] or [FlexBasis.Size] |
| [FlexboxEngine]      | Pure-Kotlin layout engine; target-agnostic                          |
| [FlexMeasurer]       | Callback interface used by the engine to size items                 |
| [FlexItemInput]      | Input descriptor passed to [FlexboxEngine.calculateLayout]          |
| [FlexItemLayout]     | Layout result (x, y, width, height) returned per item               |
| [FlexboxCalculator]  | iOS/Swift bridge via Kotlin/Native                                  |
| [FlexboxSwiftItem]   | iOS item descriptor with preferred dimensions                       |
