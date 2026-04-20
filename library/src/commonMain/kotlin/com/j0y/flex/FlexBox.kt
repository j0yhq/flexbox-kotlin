package com.j0y.flex

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth

/**
 * Scope for [FlexBox] content — provides [Modifier.flexItem] to configure
 * per-child flex properties.
 */
class FlexBoxScope {
    /**
     * Attaches [style] to this composable so that the parent [FlexBox] can
     * read it during layout.
     */
    fun Modifier.flexItem(style: FlexItemStyle = FlexItemStyle()): Modifier =
        this then FlexItemParentDataModifier(style)
}

private class FlexItemParentDataModifier(val style: FlexItemStyle) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?): Any = style
}

private val FlexBoxScopeInstance = FlexBoxScope()

/**
 * A composable that lays out its [content] children following CSS Flexbox rules.
 *
 * Uses the low-level [Layout] composable. Each child's flex item properties are
 * configured via [FlexBoxScope.flexItem] inside the [content] lambda.
 *
 * Children are measured using intrinsic measurements — [Measurable.maxIntrinsicWidth]
 * and [Measurable.maxIntrinsicHeight] — so that each child is measured exactly
 * once with the flex-resolved constraints.
 *
 * Example:
 * ```kotlin
 * FlexBox(
 *     containerStyle = FlexContainerStyle(
 *         justifyContent = JustifyContent.SpaceBetween,
 *         flexWrap = FlexWrap.Wrap,
 *     )
 * ) {
 *     Box(Modifier.flexItem(FlexItemStyle(flexGrow = 1f)).height(48.dp))
 *     Box(Modifier.flexItem(FlexItemStyle(flexGrow = 2f)).height(48.dp))
 * }
 * ```
 *
 * @param modifier       Applied to the flex container itself.
 * @param containerStyle Flex container configuration (direction, wrap, alignment, gap).
 * @param content        Children. Use [FlexBoxScope.flexItem] on each child to set
 *                       its [FlexItemStyle].
 */
@Composable
fun FlexBox(
    modifier: Modifier = Modifier,
    containerStyle: FlexContainerStyle = FlexContainerStyle(),
    content: @Composable FlexBoxScope.() -> Unit,
) {
    val scrollState = rememberScrollState()  // always called — Compose rules
    val isRow = containerStyle.flexDirection == FlexDirection.Row ||
            containerStyle.flexDirection == FlexDirection.RowReverse
    val isScrollable = containerStyle.overflow == Overflow.Scroll ||
            containerStyle.overflow == Overflow.Auto

    val effectiveModifier = when {
        isScrollable && isRow -> modifier.horizontalScroll(scrollState)
        isScrollable -> modifier.verticalScroll(scrollState)
        containerStyle.overflow == Overflow.Hidden ||
                containerStyle.overflow == Overflow.Clip ->
            modifier.graphicsLayer { clip = true }

        else -> modifier
    }

    Layout(
        content = { FlexBoxScopeInstance.content() },
        modifier = effectiveModifier,
        measurePolicy = flexMeasurePolicy(containerStyle),
    )
}

/**
 * Overload of [FlexBox] that accepts [FlexContainerStyle] parameters directly instead of
 * a pre-built [FlexContainerStyle] instance.
 */
@Composable
fun FlexBox(
    modifier: Modifier = Modifier,
    flexDirection: FlexDirection = FlexDirection.Row,
    flexWrap: FlexWrap = FlexWrap.NoWrap,
    justifyContent: JustifyContent = JustifyContent.FlexStart,
    alignItems: AlignItems = AlignItems.Stretch,
    alignContent: AlignContent = AlignContent.Stretch,
    rowGap: Float = 0f,
    columnGap: Float = 0f,
    overflow: Overflow = Overflow.Visible,
    content: @Composable FlexBoxScope.() -> Unit,
) = FlexBox(
    modifier = modifier,
    containerStyle = FlexContainerStyle(
        flexDirection = flexDirection,
        flexWrap = flexWrap,
        justifyContent = justifyContent,
        alignItems = alignItems,
        alignContent = alignContent,
        rowGap = rowGap,
        columnGap = columnGap,
        overflow = overflow,
    ),
    content = content,
)

private fun flexMeasurePolicy(containerStyle: FlexContainerStyle): MeasurePolicy =
    MeasurePolicy { measurables, constraints ->
        // Treat Compose's unbounded axis (Constraints.Infinity) as unconstrained for the engine,
        // so flexGrow doesn't expand items to fill a ~1-billion-pixel container.
        val containerWidth =
            if (!constraints.hasBoundedWidth) Float.MAX_VALUE else constraints.maxWidth.toFloat()
        val containerHeight =
            if (!constraints.hasBoundedHeight) Float.MAX_VALUE else constraints.maxHeight.toFloat()

        // Build FlexItemInputs. The measurer uses intrinsic queries so that
        // Measurable.measure() is called exactly once below (with final dimensions).
        val inputs = measurables.map { measurable ->
            val style = measurable.parentData as? FlexItemStyle ?: FlexItemStyle()
            FlexItemInput(style) { maxW, maxH ->
                when {
                    maxW != null -> {
                        val w = maxW.toInt().coerceAtLeast(0)
                        Pair(maxW, measurable.maxIntrinsicHeight(w).toFloat())
                    }

                    maxH != null -> {
                        val h = maxH.toInt().coerceAtLeast(0)
                        Pair(measurable.maxIntrinsicWidth(h).toFloat(), maxH)
                    }

                    else -> {
                        val naturalW = measurable.maxIntrinsicWidth(Constraints.Infinity)
                        val naturalH = measurable.maxIntrinsicHeight(naturalW)
                        Pair(naturalW.toFloat(), naturalH.toFloat())
                    }
                }
            }
        }

        // Run the flexbox algorithm
        val layouts = FlexboxEngine.calculateLayout(
            container = containerStyle,
            containerWidth = containerWidth,
            containerHeight = containerHeight,
            items = inputs,
        )

        // Measure each child exactly once with the flex-resolved dimensions
        val placeables = measurables.mapIndexed { i, measurable ->
            val l = layouts[i]
            measurable.measure(
                Constraints.fixed(
                    l.width.toInt().coerceAtLeast(0),
                    l.height.toInt().coerceAtLeast(0),
                )
            )
        }

        // Final container size = bounding box of in-flow items only.
        // Absolutely-positioned items are out of flow and must not inflate the container.
        val contentWidth = layouts.mapIndexedNotNull { i, l ->
            if (inputs[i].style.position is Position.Absolute) null
            else (l.x + l.width).toInt()
        }.maxOrNull()?.coerceAtLeast(0) ?: 0
        val contentHeight = layouts.mapIndexedNotNull { i, l ->
            if (inputs[i].style.position is Position.Absolute) null
            else (l.y + l.height).toInt()
        }.maxOrNull()?.coerceAtLeast(0) ?: 0

        // hidden/clip: report the constrained size so the graphics-layer clip is tight
        val isClipping = containerStyle.overflow == Overflow.Hidden ||
                containerStyle.overflow == Overflow.Clip
        val reportWidth = when {
            isClipping && constraints.hasBoundedWidth -> constraints.maxWidth
            else -> constraints.constrainWidth(contentWidth)
        }
        val reportHeight = when {
            isClipping && constraints.hasBoundedHeight -> constraints.maxHeight
            else -> constraints.constrainHeight(contentHeight)
        }

        layout(width = reportWidth, height = reportHeight) {
            placeables.forEachIndexed { i, placeable ->
                placeable.placeRelative(
                    x = layouts[i].x.toInt(),
                    y = layouts[i].y.toInt(),
                )
            }
        }
    }
