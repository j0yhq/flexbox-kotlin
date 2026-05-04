# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Fixed

- **`FlexBox` no longer crashes with `IllegalStateException: Size out of range`
  when a parent layout queries its intrinsic dimensions** — for example, a
  `flexWrap = Wrap` row nested inside a flex-column parent. The engine now
  treats unbounded container axes as indefinite (CSS-correct), so
  `align-content`, `justify-content`, and `flex-grow` no longer distribute
  `Float.MAX_VALUE` of free space. ([#1])

[#1]: https://github.com/j0yhq/flexbox-kotlin/issues/1

## [0.1.0] — 2026-04-20

Initial public release of **Flex**, a Kotlin Multiplatform library bringing the
CSS Flexbox layout model to Compose-based UIs.

### Added

- **Kotlin Multiplatform targets:** Android, iOS (`arm64`, `x64`, `simulatorArm64`), JVM, and Linux x64.
- **`FlexBox` composable** (`com.j0y.flex.FlexBox`) — Compose entry point with `Modifier.flexItem()` for per-child styling, plus an overload that inlines `FlexContainerStyle` parameters.
- **Headless `FlexboxEngine`** — pure-Kotlin layout engine with no Compose runtime dependency, suitable for server-side layout, PDF rendering, and unit testing.
- **Container properties** (`FlexContainerStyle`): `flexDirection`, `flexWrap`, `justifyContent`, `alignItems`, `alignContent`, `rowGap`, `columnGap`, `overflow`.
- **Item properties** (`FlexItemStyle`): `order`, `flexGrow`, `flexShrink`, `flexBasis`, `alignSelf`, `position`, plus a `FlexItemStyle.flex(grow, shrink, basis)` shorthand mirroring CSS `flex: <n>`.
- **`flex-direction`** values: `Row`, `RowReverse`, `Column`, `ColumnReverse`.
- **`flex-wrap`** values: `NoWrap`, `Wrap`, `WrapReverse`.
- **`align-items` / `align-self`** values including `Baseline`.
- **`align-content`** values including `Stretch`.
- **`flex-basis`** variants: `Auto`, `Size(px)`, and `Percentage(fraction)`.
- **`position`** modes: `Static`, `Relative`, and `Absolute` with `top` / `left` / `right` / `bottom` offsets.
- **`overflow`** modes: `Visible`, `Hidden`, `Clip`, `Scroll`, `Auto`.
- **`FlexContainerStyle.withGap(gap)`** extension to set `rowGap` and `columnGap` in one call.
- **Maven Central publishing** via the Vanniktech plugin under coordinates `com.j0y.flex:flex:0.1.0`.
- **Dokka documentation** generation with source links to GitHub.

[0.1.0]: https://github.com/j0yhq/flexbox-kotlin/releases/tag/0.1.0
