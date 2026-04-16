# Module FlowCompose

FlowCompose is a Kotlin Multiplatform library that implements the CSS Flexbox layout
algorithm for Compose Multiplatform (Android, iOS, JVM, Desktop).

The core entry points are:

- [io.joy.flowcompose.FlexBox] — the `@Composable` flex container
- [io.joy.flowcompose.FlexContainerStyle] / [io.joy.flowcompose.FlexItemStyle] — layout configuration
- [io.joy.flowcompose.FlexboxEngine] — platform-agnostic algorithm (use directly on non-Compose targets)
- [io.joy.flowcompose.FlexboxCalculator] — Swift/iOS convenience wrapper via Kotlin/Native

# Package io.joy.flowcompose
