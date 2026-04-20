package com.j0y.flex

/**
 * Defines the direction flex items are placed in the flex container.
 * Mirrors CSS `flex-direction`.
 */
enum class FlexDirection {
    /** Items are placed left to right (default). Mirrors CSS `flex-direction: row`. */
    Row,

    /** Items are placed right to left. Mirrors CSS `flex-direction: row-reverse`. */
    RowReverse,

    /** Items are placed top to bottom. Mirrors CSS `flex-direction: column`. */
    Column,

    /** Items are placed bottom to top. Mirrors CSS `flex-direction: column-reverse`. */
    ColumnReverse,
}

/**
 * Controls whether flex items are forced onto a single line or can wrap.
 * Mirrors CSS `flex-wrap`.
 */
enum class FlexWrap {
    /** All items are placed on a single line; overflow is allowed. Mirrors CSS `flex-wrap: nowrap`. */
    NoWrap,

    /** Items wrap onto additional lines in the forward direction. Mirrors CSS `flex-wrap: wrap`. */
    Wrap,

    /** Items wrap onto additional lines in the reverse direction. Mirrors CSS `flex-wrap: wrap-reverse`. */
    WrapReverse,
}

/**
 * Aligns flex items along the main axis.
 * Mirrors CSS `justify-content`.
 */
enum class JustifyContent {
    /** Items are packed toward the start of the main axis. Mirrors CSS `justify-content: flex-start`. */
    FlexStart,

    /** Items are packed toward the end of the main axis. Mirrors CSS `justify-content: flex-end`. */
    FlexEnd,

    /** Items are centered along the main axis. Mirrors CSS `justify-content: center`. */
    Center,

    /** Items are evenly distributed; first item at the start, last at the end. Mirrors CSS `justify-content: space-between`. */
    SpaceBetween,

    /** Items are evenly distributed with half-size spaces at each end. Mirrors CSS `justify-content: space-around`. */
    SpaceAround,

    /** Items are evenly distributed with equal space between and around each item. Mirrors CSS `justify-content: space-evenly`. */
    SpaceEvenly,
}

/**
 * Aligns flex items along the cross axis.
 * Mirrors CSS `align-items`.
 */
enum class AlignItems {
    /** Items are aligned to the start of the cross axis. Mirrors CSS `align-items: flex-start`. */
    FlexStart,

    /** Items are aligned to the end of the cross axis. Mirrors CSS `align-items: flex-end`. */
    FlexEnd,

    /** Items are centered on the cross axis. Mirrors CSS `align-items: center`. */
    Center,

    /** Items are aligned to their text baseline. Mirrors CSS `align-items: baseline`. */
    Baseline,

    /** Items are stretched to fill the cross axis of their line. Mirrors CSS `align-items: stretch`. */
    Stretch,
}

/**
 * Overrides `AlignItems` for individual flex items.
 * Mirrors CSS `align-self`.
 */
enum class AlignSelf {
    /** Defers to the container's [AlignItems] value. Mirrors CSS `align-self: auto`. */
    Auto,

    /** Item is aligned to the start of the cross axis. Mirrors CSS `align-self: flex-start`. */
    FlexStart,

    /** Item is aligned to the end of the cross axis. Mirrors CSS `align-self: flex-end`. */
    FlexEnd,

    /** Item is centered on the cross axis. Mirrors CSS `align-self: center`. */
    Center,

    /** Item is aligned to its text baseline. Mirrors CSS `align-self: baseline`. */
    Baseline,

    /** Item is stretched to fill the cross axis of its line. Mirrors CSS `align-self: stretch`. */
    Stretch,
}

/**
 * Defines how a flex item is positioned within or relative to the flex container.
 * Mirrors CSS `position`.
 */
sealed interface Position {
    /**
     * The item participates in flex flow with no offset.
     * Mirrors CSS `position: static`.
     */
    data object Static : Position

    /**
     * The item participates in flex flow; [top], [left], [right], and [bottom] offsets
     * shift its painted position without affecting sibling layout.
     * Mirrors CSS `position: relative`.
     *
     * @property top    Pixels to shift down from the item's normal position; `null` = no shift.
     * @property left   Pixels to shift right from the item's normal position; `null` = no shift.
     * @property right  Pixels to shift left from the item's normal position; `null` = no shift.
     * @property bottom Pixels to shift up from the item's normal position; `null` = no shift.
     */
    data class Relative(
        val top: Float? = null,
        val left: Float? = null,
        val right: Float? = null,
        val bottom: Float? = null,
    ) : Position

    /**
     * The item is removed from flex flow and positioned relative to the flex container
     * using [top], [left], [right], and [bottom].
     * Mirrors CSS `position: absolute`.
     *
     * @property top    Distance in pixels from the container's top edge; `null` = unset.
     * @property left   Distance in pixels from the container's left edge; `null` = unset.
     * @property right  Distance in pixels from the container's right edge; `null` = unset.
     * @property bottom Distance in pixels from the container's bottom edge; `null` = unset.
     */
    data class Absolute(
        val top: Float? = null,
        val left: Float? = null,
        val right: Float? = null,
        val bottom: Float? = null,
    ) : Position
}

/**
 * Controls how content that overflows the flex container is handled.
 * Mirrors CSS `overflow`.
 */
enum class Overflow {
    /** Content is not clipped and may overflow the container. Mirrors CSS `overflow: visible`. */
    Visible,

    /** Content is clipped to container bounds; no scrolling. Mirrors CSS `overflow: hidden`. */
    Hidden,

    /** Content is clipped to container bounds without establishing a scroll container. Mirrors CSS `overflow: clip`. */
    Clip,

    /** Content is scrollable along the primary axis; clips on the cross axis. Mirrors CSS `overflow: scroll`. */
    Scroll,

    /** Same as [Scroll] — scrollable when content overflows. Mirrors CSS `overflow: auto`. */
    Auto,
}

/**
 * Aligns flex lines when there is extra space on the cross axis.
 * Only has effect when [FlexWrap] is not [FlexWrap.NoWrap] and there are multiple lines.
 * Mirrors CSS `align-content`.
 */
enum class AlignContent {
    /** Lines are packed toward the start of the cross axis. Mirrors CSS `align-content: flex-start`. */
    FlexStart,

    /** Lines are packed toward the end of the cross axis. Mirrors CSS `align-content: flex-end`. */
    FlexEnd,

    /** Lines are centered on the cross axis. Mirrors CSS `align-content: center`. */
    Center,

    /** Lines are evenly distributed; first line at the start, last at the end. Mirrors CSS `align-content: space-between`. */
    SpaceBetween,

    /** Lines are evenly distributed with half-size spaces at each end. Mirrors CSS `align-content: space-around`. */
    SpaceAround,

    /** Lines are evenly distributed with equal space between and around each line. Mirrors CSS `align-content: space-evenly`. */
    SpaceEvenly,

    /** Lines are stretched to fill the remaining cross-axis space. Mirrors CSS `align-content: stretch`. */
    Stretch,
}

/**
 * Holds the flex layout properties for a flex **container**.
 *
 * @property flexDirection  Main axis direction; default is [FlexDirection.Row].
 * @property flexWrap       Whether items can wrap onto multiple lines; default is [FlexWrap.NoWrap].
 * @property justifyContent Main-axis alignment of items within each line; default is [JustifyContent.FlexStart].
 * @property alignItems     Cross-axis alignment of items within each line; default is [AlignItems.Stretch].
 * @property alignContent   Cross-axis alignment of flex lines (multi-line only); default is [AlignContent.Stretch].
 * @property rowGap         Vertical gap between rows in pixels; default `0f`.
 * @property columnGap      Horizontal gap between columns in pixels; default `0f`.
 * @property overflow       How overflowing content is handled; default is [Overflow.Visible].
 */
data class FlexContainerStyle(
    val flexDirection: FlexDirection = FlexDirection.Row,
    val flexWrap: FlexWrap = FlexWrap.NoWrap,
    val justifyContent: JustifyContent = JustifyContent.FlexStart,
    val alignItems: AlignItems = AlignItems.Stretch,
    val alignContent: AlignContent = AlignContent.Stretch,
    val rowGap: Float = 0f,
    val columnGap: Float = 0f,
    val overflow: Overflow = Overflow.Visible,
)

/**
 * Holds the flex layout properties for a flex **item**.
 *
 * @property order      Rendering order override; items with a lower value render first. Default `0`.
 * @property flexGrow   Ratio for distributing remaining main-axis space; `0f` means the item does not grow. Default `0f`.
 * @property flexShrink Ratio for absorbing main-axis overflow; `0f` means the item does not shrink. Default `1f`.
 * @property flexBasis  Initial main-axis size before grow/shrink is applied. Default [FlexBasis.Auto].
 * @property alignSelf  Per-item cross-axis alignment override; [AlignSelf.Auto] defers to the container's [AlignItems]. Default [AlignSelf.Auto].
 * @property position   Positioning mode for this item. Default [Position.Static] (participates in flex flow, no offset).
 */
data class FlexItemStyle(
    val order: Int = 0,
    val flexGrow: Float = 0f,
    val flexShrink: Float = 1f,
    val flexBasis: FlexBasis = FlexBasis.Auto,
    val alignSelf: AlignSelf = AlignSelf.Auto,
    val position: Position = Position.Static,
) {
    companion object {
        /**
         * Shorthand factory mirroring CSS `flex: <grow> [<shrink> [<basis>]]`.
         *
         * `FlexItemStyle.flex(1f)` produces `flexGrow=1, flexShrink=1, flexBasis=Size(0f)`,
         * equivalent to CSS `flex: 1`. The zero basis ensures items distribute space
         * proportionally from zero rather than from their natural content size.
         *
         * @param grow   Sets [flexGrow].
         * @param shrink Sets [flexShrink]; default `1f`.
         * @param basis  Sets [flexBasis]; default [FlexBasis.Size] of `0f`.
         */
        fun flex(
            grow: Float,
            shrink: Float = 1f,
            basis: FlexBasis = FlexBasis.Size(0f),
        ): FlexItemStyle = FlexItemStyle(flexGrow = grow, flexShrink = shrink, flexBasis = basis)
    }
}

/**
 * Returns a copy of this style with both [FlexContainerStyle.rowGap] and
 * [FlexContainerStyle.columnGap] set to [gap].
 * Mirrors CSS `gap: <length>`.
 */
fun FlexContainerStyle.withGap(gap: Float): FlexContainerStyle =
    copy(rowGap = gap, columnGap = gap)

/**
 * Represents the `flex-basis` value, which can be `auto`, an explicit size, or a percentage.
 */
sealed class FlexBasis {
    /** The item's natural (max-content) size along the main axis is used as the flex basis. Mirrors CSS `flex-basis: auto`. */
    data object Auto : FlexBasis()

    /**
     * An explicit pixel size used as the flex basis. Mirrors CSS `flex-basis: <length>`.
     *
     * @property value Size in pixels.
     */
    data class Size(val value: Float) : FlexBasis()

    /**
     * A fraction of the container's main axis size used as the flex basis.
     * E.g. `Percentage(0.5f)` = 50% of the container width (row) or height (column).
     * Falls back to [Auto] behaviour when the container is unconstrained.
     * Mirrors CSS `flex-basis: <percentage>`.
     *
     * @property fraction Value between `0.0` and `1.0`.
     */
    data class Percentage(val fraction: Float) : FlexBasis()
}
