package io.joy.flex

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Unit tests for [FlexboxEngine].
 *
 * Test cases are adapted from the Yoga layout engine test fixtures
 * (https://github.com/facebook/yoga) and the W3C CSS Flexbox conformance suite.
 *
 * Each test uses a fixed-size [FlexMeasurer] that always returns the same
 * (width, height) regardless of constraints — making the expected positions
 * fully deterministic.
 */
class FlexboxEngineTest {

    // ── Helpers ────────────────────────────────────────────────────────────────

    private fun item(
        w: Float,
        h: Float,
        style: FlexItemStyle = FlexItemStyle(),
    ): FlexItemInput = FlexItemInput(style) { _, _ -> Pair(w, h) }

    private fun layout(
        container: FlexContainerStyle,
        cw: Float,
        ch: Float,
        vararg items: FlexItemInput,
    ): List<FlexItemLayout> =
        FlexboxEngine.calculateLayout(container, cw, ch, items.toList())

    private fun assertLayout(
        actual: FlexItemLayout,
        x: Float, y: Float, w: Float, h: Float,
        tolerance: Float = 0.01f,
        label: String = "",
    ) {
        val prefix = if (label.isEmpty()) "" else "[$label] "
        assertEquals(x, actual.x, tolerance, "${prefix}x")
        assertEquals(y, actual.y, tolerance, "${prefix}y")
        assertEquals(w, actual.width, tolerance, "${prefix}width")
        assertEquals(h, actual.height, tolerance, "${prefix}height")
    }

    // ── Row / NoWrap / JustifyContent ─────────────────────────────────────────

    @Test
    fun row_flexStart_threeItems() {
        val r = layout(
            FlexContainerStyle(), 300f, 100f,
            item(50f, 30f), item(80f, 30f), item(60f, 30f)
        )
        assertLayout(r[0], 0f, 0f, 50f, 100f, label = "item0")    // Stretch fills height
        assertLayout(r[1], 50f, 0f, 80f, 100f, label = "item1")
        assertLayout(r[2], 130f, 0f, 60f, 100f, label = "item2")
    }

    @Test
    fun row_flexEnd_threeItems() {
        val r = layout(
            FlexContainerStyle(justifyContent = JustifyContent.FlexEnd), 300f, 100f,
            item(50f, 30f), item(80f, 30f), item(60f, 30f)
        )
        // total items = 190, free = 110 → all shifted right by 110
        assertLayout(r[0], 110f, 0f, 50f, 100f, label = "item0")
        assertLayout(r[1], 160f, 0f, 80f, 100f, label = "item1")
        assertLayout(r[2], 240f, 0f, 60f, 100f, label = "item2")
    }

    @Test
    fun row_center_threeItems() {
        val r = layout(
            FlexContainerStyle(justifyContent = JustifyContent.Center), 300f, 100f,
            item(50f, 30f), item(80f, 30f), item(60f, 30f)
        )
        // total = 190, free = 110, offset = 55
        assertLayout(r[0], 55f, 0f, 50f, 100f, label = "item0")
        assertLayout(r[1], 105f, 0f, 80f, 100f, label = "item1")
        assertLayout(r[2], 185f, 0f, 60f, 100f, label = "item2")
    }

    @Test
    fun row_spaceBetween_threeItems() {
        val r = layout(
            FlexContainerStyle(justifyContent = JustifyContent.SpaceBetween), 300f, 100f,
            item(50f, 30f), item(80f, 30f), item(60f, 30f)
        )
        // free = 110, spacing = 55 between pairs
        assertLayout(r[0], 0f, 0f, 50f, 100f, label = "item0")
        assertLayout(r[1], 105f, 0f, 80f, 100f, label = "item1")
        assertLayout(r[2], 240f, 0f, 60f, 100f, label = "item2")
    }

    @Test
    fun row_spaceAround_threeItems() {
        val r = layout(
            FlexContainerStyle(justifyContent = JustifyContent.SpaceAround), 300f, 100f,
            item(50f, 30f), item(80f, 30f), item(60f, 30f)
        )
        // free = 110, unit = 110/3 ≈ 36.67, start = 18.33, between = 36.67
        assertLayout(r[0], 110f / 3 / 2f, 0f, 50f, 100f, label = "item0")
    }

    @Test
    fun row_spaceEvenly_threeItems() {
        val r = layout(
            FlexContainerStyle(justifyContent = JustifyContent.SpaceEvenly), 300f, 100f,
            item(50f, 30f), item(80f, 30f), item(60f, 30f)
        )
        // free = 110, unit = 110/4 = 27.5
        val unit = 110f / 4f
        assertLayout(r[0], unit, 0f, 50f, 100f, label = "item0")
        assertLayout(r[1], unit + 50f + unit, 0f, 80f, 100f, label = "item1")
        assertLayout(r[2], unit + 50f + unit + 80f + unit, 0f, 60f, 100f, label = "item2")
    }

    @Test
    fun row_spaceBetween_singleItem() {
        val r = layout(
            FlexContainerStyle(justifyContent = JustifyContent.SpaceBetween), 200f, 100f,
            item(50f, 30f)
        )
        assertLayout(r[0], 0f, 0f, 50f, 100f)
    }

    // ── Column direction ──────────────────────────────────────────────────────

    @Test
    fun column_flexStart_threeItems() {
        val r = layout(
            FlexContainerStyle(flexDirection = FlexDirection.Column),
            100f, 300f,
            item(40f, 50f), item(40f, 80f), item(40f, 60f)
        )
        assertLayout(r[0], 0f, 0f, 100f, 50f, label = "item0")   // Stretch fills width
        assertLayout(r[1], 0f, 50f, 100f, 80f, label = "item1")
        assertLayout(r[2], 0f, 130f, 100f, 60f, label = "item2")
    }

    @Test
    fun column_spaceBetween_threeItems() {
        val r = layout(
            FlexContainerStyle(
                flexDirection = FlexDirection.Column,
                justifyContent = JustifyContent.SpaceBetween
            ),
            100f, 300f,
            item(40f, 50f), item(40f, 80f), item(40f, 60f)
        )
        // total = 190, free = 110, spacing = 55
        assertLayout(r[0], 0f, 0f, 100f, 50f, label = "item0")
        assertLayout(r[1], 0f, 105f, 100f, 80f, label = "item1")
        assertLayout(r[2], 0f, 240f, 100f, 60f, label = "item2")
    }

    // ── Reversed directions ────────────────────────────────────────────────────

    @Test
    fun rowReverse_flexStart_threeItems() {
        val r = layout(
            FlexContainerStyle(flexDirection = FlexDirection.RowReverse),
            300f, 100f,
            item(50f, 30f), item(80f, 30f), item(60f, 30f)
        )
        // RowReverse: item[0] is at the right (flex-start = right edge)
        // item[0] rightmost: x = 300 - 0 - 50 = 250
        // item[1]: x = 300 - 50 - 80 = 170
        // item[2]: x = 300 - 130 - 60 = 110
        assertLayout(r[0], 250f, 0f, 50f, 100f, label = "item0")
        assertLayout(r[1], 170f, 0f, 80f, 100f, label = "item1")
        assertLayout(r[2], 110f, 0f, 60f, 100f, label = "item2")
    }

    @Test
    fun rowReverse_spaceBetween_threeItems() {
        val r = layout(
            FlexContainerStyle(
                flexDirection = FlexDirection.RowReverse,
                justifyContent = JustifyContent.SpaceBetween
            ),
            300f, 100f,
            item(50f, 30f), item(80f, 30f), item(60f, 30f)
        )
        // free=110, spacing=55
        // item[0] at far right (main-axis pos 0 → visual x = 300 - 0 - 50 = 250)
        // item[1] at main-axis pos 50+55=105 → visual x = 300 - 105 - 80 = 115
        // item[2] at main-axis pos 105+80+55=240 → visual x = 300 - 240 - 60 = 0
        assertLayout(r[0], 250f, 0f, 50f, 100f, label = "item0")
        assertLayout(r[1], 115f, 0f, 80f, 100f, label = "item1")
        assertLayout(r[2], 0f, 0f, 60f, 100f, label = "item2")
    }

    @Test
    fun columnReverse_flexStart_twoItems() {
        val r = layout(
            FlexContainerStyle(flexDirection = FlexDirection.ColumnReverse),
            100f, 200f,
            item(40f, 60f), item(40f, 80f)
        )
        // item[0] at the bottom (flex-start = bottom in ColumnReverse)
        // item[0] visual y = 200 - 0 - 60 = 140
        // item[1] visual y = 200 - 60 - 80 = 60
        assertLayout(r[0], 0f, 140f, 100f, 60f, label = "item0")
        assertLayout(r[1], 0f, 60f, 100f, 80f, label = "item1")
    }

    // ── flexWrap ──────────────────────────────────────────────────────────────

    @Test
    fun wrap_fourItemsTwoLines() {
        val r = layout(
            FlexContainerStyle(
                flexWrap = FlexWrap.Wrap,
                alignItems = AlignItems.FlexStart,
                alignContent = AlignContent.FlexStart,  // prevent cross-axis stretching
            ),
            200f, 200f,
            item(80f, 40f), item(80f, 60f), item(80f, 50f), item(80f, 40f)
        )
        // Line 1: items 0, 1 (160 ≤ 200) — line cross = 60
        // Line 2: items 2, 3 (160 ≤ 200) — line cross = 50
        assertLayout(r[0], 0f, 0f, 80f, 40f, label = "item0")
        assertLayout(r[1], 80f, 0f, 80f, 60f, label = "item1")
        assertLayout(r[2], 0f, 60f, 80f, 50f, label = "item2")
        assertLayout(r[3], 80f, 60f, 80f, 40f, label = "item3")
    }

    @Test
    fun wrap_overflowToThreeLines() {
        val r = layout(
            FlexContainerStyle(
                flexWrap = FlexWrap.Wrap,
                alignItems = AlignItems.FlexStart,
                alignContent = AlignContent.FlexStart,  // prevent cross-axis stretching
            ),
            150f, 300f,
            item(100f, 40f), item(100f, 50f), item(100f, 30f)
        )
        // Each item is 100px > would overflow with two, so each on its own line
        assertLayout(r[0], 0f, 0f, 100f, 40f, label = "item0")
        assertLayout(r[1], 0f, 40f, 100f, 50f, label = "item1")
        assertLayout(r[2], 0f, 90f, 100f, 30f, label = "item2")
    }

    @Test
    fun wrapReverse_reversesLineOrder() {
        val r = layout(
            FlexContainerStyle(
                flexWrap = FlexWrap.WrapReverse,
                alignItems = AlignItems.FlexStart,
                alignContent = AlignContent.FlexStart,  // prevent cross-axis stretching
            ),
            200f, 200f,
            item(80f, 40f), item(80f, 60f), item(80f, 50f), item(80f, 40f)
        )
        // Lines in reversed order: line with items 2,3 is FIRST (top), line with 0,1 is SECOND
        // Line 1 (WrapReverse first = bottom in normal): items 2,3, crossSize=50
        // Line 2: items 0,1, crossSize=60
        // After reversal: line 0 = items[2,3] at y=0, line 1 = items[0,1] at y=50
        assertLayout(r[0], 0f, 50f, 80f, 40f, label = "item0")
        assertLayout(r[1], 80f, 50f, 80f, 60f, label = "item1")
        assertLayout(r[2], 0f, 0f, 80f, 50f, label = "item2")
        assertLayout(r[3], 80f, 0f, 80f, 40f, label = "item3")
    }

    @Test
    fun noWrap_itemsOverflowOnOneLine() {
        val r = layout(
            FlexContainerStyle(flexWrap = FlexWrap.NoWrap, alignItems = AlignItems.FlexStart),
            100f, 100f,
            item(80f, 30f, FlexItemStyle(flexShrink = 0f)),
            item(80f, 30f, FlexItemStyle(flexShrink = 0f)),
        )
        // Both on one line, flexShrink=0 so they do not shrink and overflow the container
        assertLayout(r[0], 0f, 0f, 80f, 30f, label = "item0")
        assertLayout(r[1], 80f, 0f, 80f, 30f, label = "item1")
    }

    // ── flexGrow ──────────────────────────────────────────────────────────────

    @Test
    fun flexGrow_equalGrowTwoItems() {
        val r = layout(
            FlexContainerStyle(alignItems = AlignItems.FlexStart),
            200f, 100f,
            item(50f, 30f, FlexItemStyle(flexGrow = 1f)),
            item(50f, 30f, FlexItemStyle(flexGrow = 1f)),
        )
        // free = 100, each grows by 50
        assertLayout(r[0], 0f, 0f, 100f, 30f, label = "item0")
        assertLayout(r[1], 100f, 0f, 100f, 30f, label = "item1")
    }

    @Test
    fun flexGrow_unequalGrow() {
        val r = layout(
            FlexContainerStyle(alignItems = AlignItems.FlexStart),
            200f, 100f,
            item(0f, 30f, FlexItemStyle(flexGrow = 1f)),
            item(0f, 30f, FlexItemStyle(flexGrow = 2f)),
        )
        // free = 200, item0 gets 1/3 ≈ 66.67, item1 gets 2/3 ≈ 133.33
        assertLayout(r[0], 0f, 0f, 200f / 3f, 30f, label = "item0")
        assertLayout(r[1], 200f / 3f, 0f, 400f / 3f, 30f, label = "item1")
    }

    @Test
    fun flexGrow_zeroGrowNoExpansion() {
        val r = layout(
            FlexContainerStyle(alignItems = AlignItems.FlexStart),
            300f, 100f,
            item(50f, 30f, FlexItemStyle(flexGrow = 0f)),
            item(50f, 30f, FlexItemStyle(flexGrow = 0f)),
        )
        assertLayout(r[0], 0f, 0f, 50f, 30f)
        assertLayout(r[1], 50f, 0f, 50f, 30f)
    }

    @Test
    fun flexGrow_mixedGrow() {
        val r = layout(
            FlexContainerStyle(alignItems = AlignItems.FlexStart),
            300f, 100f,
            item(100f, 30f, FlexItemStyle(flexGrow = 0f)),
            item(0f, 30f, FlexItemStyle(flexGrow = 1f)),
        )
        // free = 200, item1 gets all
        assertLayout(r[0], 0f, 0f, 100f, 30f, label = "item0")
        assertLayout(r[1], 100f, 0f, 200f, 30f, label = "item1")
    }

    // ── flexShrink ────────────────────────────────────────────────────────────

    @Test
    fun flexShrink_equalShrinkTwoItems() {
        val r = layout(
            FlexContainerStyle(alignItems = AlignItems.FlexStart),
            100f, 100f,
            item(80f, 30f, FlexItemStyle(flexShrink = 1f)),
            item(80f, 30f, FlexItemStyle(flexShrink = 1f)),
        )
        // overflow = 60, each shrinks by 30 (equal weight: 80*1 = 80*1)
        assertLayout(r[0], 0f, 0f, 50f, 30f, label = "item0")
        assertLayout(r[1], 50f, 0f, 50f, 30f, label = "item1")
    }

    @Test
    fun flexShrink_zeroShrinkNoReduction() {
        val r = layout(
            FlexContainerStyle(alignItems = AlignItems.FlexStart),
            100f, 100f,
            item(80f, 30f, FlexItemStyle(flexShrink = 0f)),
            item(80f, 30f, FlexItemStyle(flexShrink = 0f)),
        )
        // Neither shrinks — items overflow
        assertLayout(r[0], 0f, 0f, 80f, 30f, label = "item0")
        assertLayout(r[1], 80f, 0f, 80f, 30f, label = "item1")
    }

    @Test
    fun flexShrink_unequalShrink() {
        val r = layout(
            FlexContainerStyle(alignItems = AlignItems.FlexStart),
            100f, 100f,
            item(100f, 30f, FlexItemStyle(flexShrink = 1f)),
            item(100f, 30f, FlexItemStyle(flexShrink = 2f)),
        )
        // overflow = 100
        // weighted: item0 = 100*1 = 100, item1 = 100*2 = 200, total = 300
        // item0 shrinks by 100*(100/300) ≈ 33.33 → 66.67
        // item1 shrinks by 100*(200/300) ≈ 66.67 → 33.33
        assertLayout(r[0], 0f, 0f, 200f / 3f, 30f, label = "item0")
        assertLayout(r[1], 200f / 3f, 0f, 100f / 3f, 30f, label = "item1")
    }

    // ── flexBasis ─────────────────────────────────────────────────────────────

    @Test
    fun flexBasis_sizeOverridesNatural() {
        val r = layout(
            FlexContainerStyle(alignItems = AlignItems.FlexStart),
            300f, 100f,
            item(50f, 30f, FlexItemStyle(flexBasis = FlexBasis.Size(120f))),
        )
        // basis = 120, natural = 50 → final width = 120 (no grow)
        assertLayout(r[0], 0f, 0f, 120f, 30f)
    }

    @Test
    fun flexBasis_autoUsesNaturalSize() {
        val r = layout(
            FlexContainerStyle(alignItems = AlignItems.FlexStart),
            300f, 100f,
            item(80f, 30f, FlexItemStyle(flexBasis = FlexBasis.Auto)),
        )
        assertLayout(r[0], 0f, 0f, 80f, 30f)
    }

    @Test
    fun flexBasis_sizeWithGrow() {
        val r = layout(
            FlexContainerStyle(alignItems = AlignItems.FlexStart),
            200f, 100f,
            item(50f, 30f, FlexItemStyle(flexBasis = FlexBasis.Size(50f), flexGrow = 1f)),
            item(50f, 30f, FlexItemStyle(flexBasis = FlexBasis.Size(50f), flexGrow = 1f)),
        )
        // base = 100, free = 100, each grows 50
        assertLayout(r[0], 0f, 0f, 100f, 30f, label = "item0")
        assertLayout(r[1], 100f, 0f, 100f, 30f, label = "item1")
    }

    @Test
    fun flexBasis_percentage_halfContainer() {
        val r = layout(
            FlexContainerStyle(alignItems = AlignItems.FlexStart),
            200f, 100f,
            item(60f, 20f, FlexItemStyle(flexBasis = FlexBasis.Percentage(0.5f))),
            item(60f, 20f, FlexItemStyle(flexBasis = FlexBasis.Percentage(0.5f))),
        )
        // basis = 0.5 × 200 = 100px each; total = container, no free space
        assertLayout(r[0], 0f, 0f, 100f, 20f, label = "item0")
        assertLayout(r[1], 100f, 0f, 100f, 20f, label = "item1")
    }

    @Test
    fun flexBasis_percentage_withGrow() {
        val r = layout(
            FlexContainerStyle(alignItems = AlignItems.FlexStart),
            200f, 100f,
            item(40f, 20f, FlexItemStyle(flexBasis = FlexBasis.Percentage(0.25f), flexGrow = 1f)),
            item(40f, 20f, FlexItemStyle(flexBasis = FlexBasis.Percentage(0.25f), flexGrow = 1f)),
        )
        // basis = 0.25 × 200 = 50px each; free space = 100px split equally → each = 100px
        assertLayout(r[0], 0f, 0f, 100f, 20f, label = "item0")
        assertLayout(r[1], 100f, 0f, 100f, 20f, label = "item1")
    }

    @Test
    fun flexBasis_percentage_unconstrainedFallsBackToNatural() {
        val r = layout(
            FlexContainerStyle(alignItems = AlignItems.FlexStart),
            Float.MAX_VALUE, Float.MAX_VALUE,
            item(80f, 20f, FlexItemStyle(flexBasis = FlexBasis.Percentage(0.5f))),
        )
        // unconstrained container → percentage falls back to intrinsic natural size
        assertLayout(r[0], 0f, 0f, 80f, 20f)
    }

    // ── alignItems ────────────────────────────────────────────────────────────

    @Test
    fun alignItems_flexStart() {
        val r = layout(
            FlexContainerStyle(alignItems = AlignItems.FlexStart),
            200f, 100f,
            item(50f, 30f), item(50f, 50f)
        )
        assertLayout(r[0], 0f, 0f, 50f, 30f, label = "item0")   // top-aligned
        assertLayout(r[1], 50f, 0f, 50f, 50f, label = "item1")
    }

    @Test
    fun alignItems_flexEnd() {
        val r = layout(
            FlexContainerStyle(alignItems = AlignItems.FlexEnd),
            200f, 100f,
            item(50f, 30f), item(50f, 50f)
        )
        // line cross = 100 (container height)
        assertLayout(r[0], 0f, 70f, 50f, 30f, label = "item0")  // bottom-aligned
        assertLayout(r[1], 50f, 50f, 50f, 50f, label = "item1")
    }

    @Test
    fun alignItems_center() {
        val r = layout(
            FlexContainerStyle(alignItems = AlignItems.Center),
            200f, 100f,
            item(50f, 30f), item(50f, 50f)
        )
        assertLayout(r[0], 0f, 35f, 50f, 30f, label = "item0")  // (100-30)/2 = 35
        assertLayout(r[1], 50f, 25f, 50f, 50f, label = "item1") // (100-50)/2 = 25
    }

    @Test
    fun alignItems_stretch_fillsLineCrossSize() {
        val r = layout(
            FlexContainerStyle(alignItems = AlignItems.Stretch),
            200f, 100f,
            item(50f, 30f), item(50f, 50f)
        )
        // Both items should be 100px tall (container height)
        assertLayout(r[0], 0f, 0f, 50f, 100f, label = "item0")
        assertLayout(r[1], 50f, 0f, 50f, 100f, label = "item1")
    }

    // ── alignSelf overrides alignItems ────────────────────────────────────────

    @Test
    fun alignSelf_overridesContainer() {
        val r = layout(
            FlexContainerStyle(alignItems = AlignItems.Stretch),
            200f, 100f,
            item(50f, 30f),
            item(50f, 30f, FlexItemStyle(alignSelf = AlignSelf.Center)),
        )
        // item0 stretches to 100, item1 is centered at 35
        assertLayout(r[0], 0f, 0f, 50f, 100f, label = "item0")
        assertLayout(r[1], 50f, 35f, 50f, 30f, label = "item1")
    }

    @Test
    fun alignSelf_flexEnd_override() {
        val r = layout(
            FlexContainerStyle(alignItems = AlignItems.FlexStart),
            200f, 100f,
            item(50f, 30f),
            item(50f, 40f, FlexItemStyle(alignSelf = AlignSelf.FlexEnd)),
        )
        assertLayout(r[0], 0f, 0f, 50f, 30f, label = "item0")
        assertLayout(r[1], 50f, 60f, 50f, 40f, label = "item1")  // 100 - 40 = 60
    }

    @Test
    fun alignSelf_stretch_explicitOnOneItem() {
        val r = layout(
            FlexContainerStyle(alignItems = AlignItems.FlexStart),
            200f, 100f,
            item(50f, 30f),
            item(50f, 30f, FlexItemStyle(alignSelf = AlignSelf.Stretch)),
        )
        assertLayout(r[0], 0f, 0f, 50f, 30f, label = "item0")  // FlexStart, natural size
        assertLayout(r[1], 50f, 0f, 50f, 100f, label = "item1") // Stretched to 100
    }

    // ── alignContent (multi-line) ─────────────────────────────────────────────

    private fun twoLineSetup(alignContent: AlignContent) = layout(
        FlexContainerStyle(
            flexWrap = FlexWrap.Wrap,
            alignItems = AlignItems.FlexStart,
            alignContent = alignContent,
        ),
        200f, 200f,
        item(80f, 40f), item(80f, 60f),  // line 1: 80+80=160 ≤ 200, cross=60
        item(80f, 50f), item(80f, 30f),  // line 2: 80+80=160 ≤ 200, cross=50
    )
    // Total cross = 110, free = 90

    @Test
    fun alignContent_flexStart_twoLines() {
        val r = twoLineSetup(AlignContent.FlexStart)
        assertEquals(0f, r[0].y, 0.01f, "line1 y")
        assertEquals(60f, r[2].y, 0.01f, "line2 y")
    }

    @Test
    fun alignContent_flexEnd_twoLines() {
        val r = twoLineSetup(AlignContent.FlexEnd)
        // free=90, line1 starts at 90, line2 at 90+60=150
        assertEquals(90f, r[0].y, 0.01f, "line1 y")
        assertEquals(150f, r[2].y, 0.01f, "line2 y")
    }

    @Test
    fun alignContent_center_twoLines() {
        val r = twoLineSetup(AlignContent.Center)
        // line1 starts at 45, line2 at 105
        assertEquals(45f, r[0].y, 0.01f, "line1 y")
        assertEquals(105f, r[2].y, 0.01f, "line2 y")
    }

    @Test
    fun alignContent_spaceBetween_twoLines() {
        val r = twoLineSetup(AlignContent.SpaceBetween)
        // line1 at 0, line2 at 0+60+90=150
        assertEquals(0f, r[0].y, 0.01f, "line1 y")
        assertEquals(150f, r[2].y, 0.01f, "line2 y")
    }

    @Test
    fun alignContent_spaceAround_twoLines() {
        val r = twoLineSetup(AlignContent.SpaceAround)
        // unit = 90/2 = 45, line1 at 22.5, line2 at 22.5+60+45=127.5
        assertEquals(22.5f, r[0].y, 0.01f, "line1 y")
        assertEquals(127.5f, r[2].y, 0.01f, "line2 y")
    }

    @Test
    fun alignContent_spaceEvenly_twoLines() {
        val r = twoLineSetup(AlignContent.SpaceEvenly)
        // unit = 90/3 = 30, line1 at 30, line2 at 30+60+30=120
        assertEquals(30f, r[0].y, 0.01f, "line1 y")
        assertEquals(120f, r[2].y, 0.01f, "line2 y")
    }

    @Test
    fun alignContent_stretch_expandsLines() {
        val r = twoLineSetup(AlignContent.Stretch)
        // free=90, each line gains 45: line1 crossSize=105, line2 crossSize=95
        // line1 at 0, line2 at 105
        assertEquals(0f, r[0].y, 0.01f, "line1 y")
        assertEquals(105f, r[2].y, 0.01f, "line2 y")
    }

    // ── order ─────────────────────────────────────────────────────────────────

    @Test
    fun order_reordersItems() {
        val r = layout(
            FlexContainerStyle(alignItems = AlignItems.FlexStart),
            300f, 100f,
            item(50f, 30f, FlexItemStyle(order = 2)),
            item(50f, 30f, FlexItemStyle(order = 0)),
            item(50f, 30f, FlexItemStyle(order = 1)),
        )
        // Layout order: item1 (order=0), item2 (order=1), item0 (order=2)
        // item0 at position 2 → x=100
        // item1 at position 0 → x=0
        // item2 at position 1 → x=50
        assertLayout(r[0], 100f, 0f, 50f, 30f, label = "item0 (order=2)")
        assertLayout(r[1], 0f, 0f, 50f, 30f, label = "item1 (order=0)")
        assertLayout(r[2], 50f, 0f, 50f, 30f, label = "item2 (order=1)")
    }

    @Test
    fun order_stableForEqualOrders() {
        val r = layout(
            FlexContainerStyle(alignItems = AlignItems.FlexStart),
            200f, 100f,
            item(50f, 30f, FlexItemStyle(order = 0)),
            item(60f, 30f, FlexItemStyle(order = 0)),
        )
        // Same order, preserve source order
        assertLayout(r[0], 0f, 0f, 50f, 30f, label = "item0")
        assertLayout(r[1], 50f, 0f, 60f, 30f, label = "item1")
    }

    @Test
    fun order_negativeOrderFirst() {
        val r = layout(
            FlexContainerStyle(alignItems = AlignItems.FlexStart),
            200f, 100f,
            item(50f, 30f, FlexItemStyle(order = 0)),
            item(60f, 30f, FlexItemStyle(order = -1)),
        )
        // item1 (order=-1) appears first
        assertLayout(r[0], 60f, 0f, 50f, 30f, label = "item0 (order=0)")
        assertLayout(r[1], 0f, 0f, 60f, 30f, label = "item1 (order=-1)")
    }

    // ── Gap ───────────────────────────────────────────────────────────────────

    @Test
    fun columnGap_rowDirection() {
        val r = layout(
            FlexContainerStyle(columnGap = 10f, alignItems = AlignItems.FlexStart),
            300f, 100f,
            item(50f, 30f), item(50f, 30f), item(50f, 30f)
        )
        assertLayout(r[0], 0f, 0f, 50f, 30f, label = "item0")
        assertLayout(r[1], 60f, 0f, 50f, 30f, label = "item1")   // 50 + 10 gap
        assertLayout(r[2], 120f, 0f, 50f, 30f, label = "item2")  // 60 + 60
    }

    @Test
    fun rowGap_columnDirection() {
        val r = layout(
            FlexContainerStyle(
                flexDirection = FlexDirection.Column,
                rowGap = 20f,
                alignItems = AlignItems.FlexStart
            ),
            100f, 300f,
            item(40f, 50f), item(40f, 50f)
        )
        assertLayout(r[0], 0f, 0f, 40f, 50f, label = "item0")
        assertLayout(r[1], 0f, 70f, 40f, 50f, label = "item1")   // 50 + 20 gap
    }

    @Test
    fun columnGap_triggersWrap() {
        // Items are 80px each, gap 20px, container 200px
        // Line 1: item0=80, +gap=20+item1=80 → 180 ≤ 200 ✓
        // item0+gap+item1+gap+item2 = 260 > 200 → item2 wraps
        val r = layout(
            FlexContainerStyle(
                flexWrap = FlexWrap.Wrap,
                columnGap = 20f,
                alignItems = AlignItems.FlexStart,
                alignContent = AlignContent.FlexStart,  // prevent cross-axis stretching
            ),
            200f, 200f,
            item(80f, 40f), item(80f, 40f), item(80f, 50f)
        )
        assertLayout(r[0], 0f, 0f, 80f, 40f, label = "item0")
        assertLayout(r[1], 100f, 0f, 80f, 40f, label = "item1")  // 80+20
        assertLayout(r[2], 0f, 40f, 80f, 50f, label = "item2")   // wrapped
    }

    @Test
    fun rowGap_multiLine() {
        val r = layout(
            FlexContainerStyle(
                flexWrap = FlexWrap.Wrap,
                rowGap = 15f,
                alignItems = AlignItems.FlexStart,
                alignContent = AlignContent.FlexStart,  // prevent cross-axis stretching
            ),
            150f, 300f,
            item(100f, 40f), item(100f, 50f)
        )
        // Each on its own line (100px each, container 150 but 200 > 150)
        assertLayout(r[0], 0f, 0f, 100f, 40f, label = "item0")
        assertLayout(r[1], 0f, 55f, 100f, 50f, label = "item1")  // 40 + 15 gap
    }

    // ── Empty & edge cases ────────────────────────────────────────────────────

    @Test
    fun emptyItems_returnsEmpty() {
        val r = layout(FlexContainerStyle(), 300f, 100f)
        assertEquals(0, r.size)
    }

    @Test
    fun singleItem_noGrowNoShrink_naturalSize() {
        val r = layout(
            FlexContainerStyle(alignItems = AlignItems.FlexStart),
            300f, 100f,
            item(50f, 30f, FlexItemStyle(flexGrow = 0f, flexShrink = 0f))
        )
        assertLayout(r[0], 0f, 0f, 50f, 30f)
    }

    // ── flex shorthand ────────────────────────────────────────────────────────

    @Test
    fun flex_shorthand_equalGrow_distributesProportion() {
        // Two items with flex(1f) in a 300px row; both get 150px (basis=0 so no content bias)
        val r = layout(
            FlexContainerStyle(alignItems = AlignItems.FlexStart),
            300f, 100f,
            item(80f, 30f, FlexItemStyle.flex(1f)),
            item(80f, 30f, FlexItemStyle.flex(1f)),
        )
        assertLayout(r[0], 0f, 0f, 150f, 30f, label = "item0")
        assertLayout(r[1], 150f, 0f, 150f, 30f, label = "item1")
    }

    @Test
    fun flex_shorthand_unequalGrow_distributesByWeight() {
        // flex(2f) gets twice the space of flex(1f): 200px vs 100px in a 300px container
        val r = layout(
            FlexContainerStyle(alignItems = AlignItems.FlexStart),
            300f, 100f,
            item(50f, 30f, FlexItemStyle.flex(2f)),
            item(50f, 30f, FlexItemStyle.flex(1f)),
        )
        assertLayout(r[0], 0f, 0f, 200f, 30f, label = "item0")
        assertLayout(r[1], 200f, 0f, 100f, 30f, label = "item1")
    }

    @Test
    fun flex_shorthand_customShrinkAndBasis() {
        // flex(grow=0, shrink=0, basis=Size(60f)) → fixed 60px item (no grow, no shrink)
        val r = layout(
            FlexContainerStyle(alignItems = AlignItems.FlexStart),
            300f, 100f,
            item(80f, 30f, FlexItemStyle.flex(grow = 0f, shrink = 0f, basis = FlexBasis.Size(60f))),
        )
        assertLayout(r[0], 0f, 0f, 60f, 30f)
    }

    // ── gap shorthand ─────────────────────────────────────────────────────────

    @Test
    fun withGap_setsBothRowAndColumnGap() {
        val style = FlexContainerStyle().withGap(12f)
        assertEquals(12f, style.rowGap, 0.001f)
        assertEquals(12f, style.columnGap, 0.001f)
    }

    @Test
    fun withGap_rowLayout_matchesColumnGap() {
        // withGap(10f) on a row should produce identical positions to columnGap=10f
        val gapStyle = FlexContainerStyle(
            alignItems = AlignItems.FlexStart,
            flexWrap = FlexWrap.NoWrap,
        ).withGap(10f)
        val explicitStyle = FlexContainerStyle(
            alignItems = AlignItems.FlexStart,
            flexWrap = FlexWrap.NoWrap,
            columnGap = 10f,
            rowGap = 10f,
        )
        val items = listOf(item(50f, 30f), item(60f, 30f), item(40f, 30f))
        val r1 = FlexboxEngine.calculateLayout(gapStyle, 300f, 100f, items)
        val r2 = FlexboxEngine.calculateLayout(explicitStyle, 300f, 100f, items)
        r1.zip(r2).forEachIndexed { i, (a, b) ->
            assertLayout(a, b.x, b.y, b.width, b.height, label = "item$i")
        }
    }

    // ── position ──────────────────────────────────────────────────────────────

    @Test
    fun position_static_defaultBehavior() {
        // Static (default) items lay out exactly as before the feature was added
        val r = layout(
            FlexContainerStyle(alignItems = AlignItems.FlexStart),
            300f, 100f,
            item(50f, 30f, FlexItemStyle(position = Position.Static)),
            item(80f, 30f, FlexItemStyle(position = Position.Static)),
        )
        assertLayout(r[0], 0f, 0f, 50f, 30f, label = "item0")
        assertLayout(r[1], 50f, 0f, 80f, 30f, label = "item1")
    }

    @Test
    fun position_relative_offsetsItem_doesNotAffectSiblings() {
        // item0 has a relative offset of left=10; item1 should still start at x=50
        val r = layout(
            FlexContainerStyle(alignItems = AlignItems.FlexStart),
            300f, 100f,
            item(50f, 30f, FlexItemStyle(position = Position.Relative(left = 10f))),
            item(80f, 30f),
        )
        // item0 painted at x=10 (0 + 10), item1 at x=50 (unaffected by sibling offset)
        assertLayout(r[0], 10f, 0f, 50f, 30f, label = "item0")
        assertLayout(r[1], 50f, 0f, 80f, 30f, label = "item1")
    }

    @Test
    fun position_relative_topAndRight_offsetsCorrectly() {
        // top shifts down, right shifts left
        val r = layout(
            FlexContainerStyle(alignItems = AlignItems.FlexStart),
            300f, 200f,
            item(50f, 30f, FlexItemStyle(position = Position.Relative(top = 5f, right = 8f))),
        )
        // flex position = (0, 0); offset: left - right = 0 - 8 = -8; top - bottom = 5 - 0 = 5
        assertLayout(r[0], -8f, 5f, 50f, 30f)
    }

    @Test
    fun position_absolute_removedFromFlow() {
        // The absolute item should not shift item1's position
        val r = layout(
            FlexContainerStyle(alignItems = AlignItems.FlexStart),
            300f, 200f,
            item(50f, 30f, FlexItemStyle(position = Position.Absolute(top = 0f, left = 0f))),
            item(80f, 30f),
        )
        // item0: absolute at (0,0); item1: in-flow starting at x=0 (absolute sibling not counted)
        assertLayout(r[0], 0f, 0f, 50f, 30f, label = "absolute")
        assertLayout(r[1], 0f, 0f, 80f, 30f, label = "in-flow")
    }

    @Test
    fun position_absolute_topLeft_placement() {
        val r = layout(
            FlexContainerStyle(),
            400f, 300f,
            item(60f, 40f, FlexItemStyle(position = Position.Absolute(top = 20f, left = 30f))),
        )
        assertLayout(r[0], 30f, 20f, 60f, 40f)
    }

    @Test
    fun position_absolute_rightBottom_placement() {
        // right=10 → x = 400 - 10 - 60 = 330; bottom=15 → y = 300 - 15 - 40 = 245
        val r = layout(
            FlexContainerStyle(),
            400f, 300f,
            item(60f, 40f, FlexItemStyle(position = Position.Absolute(right = 10f, bottom = 15f))),
        )
        assertLayout(r[0], 330f, 245f, 60f, 40f)
    }

    @Test
    fun position_absolute_leftAndRight_stretchWidth() {
        // left=20, right=30 on a 400px container → width = 400 - 20 - 30 = 350
        val r = layout(
            FlexContainerStyle(),
            400f, 300f,
            item(60f, 40f, FlexItemStyle(position = Position.Absolute(left = 20f, right = 30f))),
        )
        assertLayout(r[0], 20f, 0f, 350f, 40f)
    }

    @Test
    fun position_absolute_mixedWithInFlow() {
        // Three items: in-flow / absolute / in-flow.
        // The absolute item should not affect the gap between the two in-flow items.
        val r = layout(
            FlexContainerStyle(alignItems = AlignItems.FlexStart),
            300f, 200f,
            item(50f, 30f),                                                          // in-flow
            item(
                40f,
                25f,
                FlexItemStyle(position = Position.Absolute(top = 5f, left = 100f))
            ), // absolute
            item(60f, 30f),                                                          // in-flow
        )
        assertLayout(r[0], 0f, 0f, 50f, 30f, label = "in-flow0")
        assertLayout(r[1], 100f, 5f, 40f, 25f, label = "absolute")
        assertLayout(r[2], 50f, 0f, 60f, 30f, label = "in-flow1")
    }
}
