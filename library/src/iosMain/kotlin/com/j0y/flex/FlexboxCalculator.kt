package com.j0y.flex

/**
 * Describes a flex item for use from Swift when implementing SwiftUI's `Layout` protocol.
 *
 * The [preferredWidth] and [preferredHeight] should be obtained from SwiftUI's
 * `subview.sizeThatFits(.unspecified)` inside a `Layout.placeSubviews` call.
 *
 * @property style           Flex item layout properties.
 * @property preferredWidth  Natural width in points; obtained from `subview.sizeThatFits(.unspecified).width`.
 * @property preferredHeight Natural height in points; obtained from `subview.sizeThatFits(.unspecified).height`.
 */
data class FlexboxSwiftItem(
    val style: FlexItemStyle,
    val preferredWidth: Float,
    val preferredHeight: Float,
)

/**
 * Kotlin/Native entry point for iOS consumers that want to use the Flex
 * flexbox algorithm from SwiftUI's `Layout` protocol.
 *
 * @property containerStyle The flex container configuration applied to every [calculate] call.
 *
 * Swift usage example:
 * ```swift
 * import Flex
 *
 * @available(iOS 16.0, *)
 * struct FlexLayout: Layout {
 *     let containerStyle: FlexContainerStyle
 *
 *     func sizeThatFits(
 *         proposal: ProposedViewSize,
 *         subviews: Subviews,
 *         cache: inout Void
 *     ) -> CGSize {
 *         CGSize(
 *             width:  proposal.width  ?? .infinity,
 *             height: proposal.height ?? .infinity
 *         )
 *     }
 *
 *     func placeSubviews(
 *         in bounds: CGRect,
 *         proposal: ProposedViewSize,
 *         subviews: Subviews,
 *         cache: inout Void
 *     ) {
 *         let calculator = FlexboxCalculator(containerStyle: containerStyle)
 *         let items = subviews.map { subview -> FlexboxSwiftItem in
 *             let size = subview.sizeThatFits(.unspecified)
 *             return FlexboxSwiftItem(
 *                 style: FlexItemStyle(),
 *                 preferredWidth:  Float(size.width),
 *                 preferredHeight: Float(size.height)
 *             )
 *         }
 *         let layouts = calculator.calculate(
 *             containerWidth:  Float(bounds.width),
 *             containerHeight: Float(bounds.height),
 *             items: items
 *         )
 *         for (i, layout) in layouts.enumerated() {
 *             subviews[i].place(
 *                 at: CGPoint(
 *                     x: Double(layout.x) + bounds.minX,
 *                     y: Double(layout.y) + bounds.minY
 *                 ),
 *                 proposal: ProposedViewSize(
 *                     width:  CGFloat(layout.width),
 *                     height: CGFloat(layout.height)
 *                 )
 *             )
 *         }
 *     }
 * }
 * ```
 */
class FlexboxCalculator(val containerStyle: FlexContainerStyle) {

    /**
     * Calculates the layout for [items] within a container of the given dimensions.
     *
     * @param containerWidth  container width in points
     * @param containerHeight container height in points
     * @param items           flex items in source order, each with a preferred size
     * @return layout results in the same order as [items]
     */
    fun calculate(
        containerWidth: Float,
        containerHeight: Float,
        items: List<FlexboxSwiftItem>,
    ): List<FlexItemLayout> {
        val inputs = items.map { item ->
            // Fixed-size measurer: return the provided preferred dimensions regardless of constraints
            FlexItemInput(item.style) { _, _ ->
                Pair(item.preferredWidth, item.preferredHeight)
            }
        }
        return FlexboxEngine.calculateLayout(
            container = containerStyle,
            containerWidth = containerWidth,
            containerHeight = containerHeight,
            items = inputs
        )
    }
}
