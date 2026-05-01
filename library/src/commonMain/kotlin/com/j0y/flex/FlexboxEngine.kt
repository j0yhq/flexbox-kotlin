package com.j0y.flex

// ── Public API types ──────────────────────────────────────────────────────────

/**
 * Callback used by [FlexboxEngine] to determine an item's size.
 *
 * To avoid violating Compose's single-measurement contract, implementations
 * should use intrinsic measurements rather than [Measurable.measure] when
 * integrating with Compose Layout.
 *
 * @param maxWidth  the final width constraint in px, or `null` when unconstrained
 * @param maxHeight the final height constraint in px, or `null` when unconstrained
 * @return (width, height) in pixels
 */
fun interface FlexMeasurer {
    fun measure(maxWidth: Float?, maxHeight: Float?): Pair<Float, Float>
}

/**
 * Input descriptor for a single flex item.
 *
 * @property style    Flex item layout properties.
 * @property measurer Callback invoked by the engine to measure this item at the flex-resolved constraints.
 */
data class FlexItemInput(
    val style: FlexItemStyle,
    val measurer: FlexMeasurer,
)

/**
 * Layout result for a single flex item — all values are in pixels from the container's top-left.
 *
 * @property x      Left edge of the item relative to the flex container origin.
 * @property y      Top edge of the item relative to the flex container origin.
 * @property width  Final computed width of the item in pixels.
 * @property height Final computed height of the item in pixels.
 */
data class FlexItemLayout(
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
)

// ── Internal mutable state ─────────────────────────────────────────────────────

private class FlexLineItem(
    val input: FlexItemInput,
    val originalIndex: Int,
    var hypotheticalMainSize: Float,
) {
    var finalMainSize: Float = hypotheticalMainSize
    var measuredCrossSize: Float = 0f
    var finalCrossSize: Float = 0f
}

private class FlexLine {
    val items: MutableList<FlexLineItem> = mutableListOf()
    var crossSize: Float = 0f
}

// ── Engine ────────────────────────────────────────────────────────────────────

object FlexboxEngine {

    /**
     * Calculates flexbox layout for the given items within a container.
     *
     * The returned list is parallel to [items]: `result[i]` is the layout for `items[i]`.
     *
     * @param container       flex container style (direction, wrap, alignment, gap)
     * @param containerWidth  available width in px (`Float.MAX_VALUE` = unconstrained)
     * @param containerHeight available height in px (`Float.MAX_VALUE` = unconstrained)
     * @param items           flex items in DOM / source order
     */
    fun calculateLayout(
        container: FlexContainerStyle,
        containerWidth: Float,
        containerHeight: Float,
        items: List<FlexItemInput>,
    ): List<FlexItemLayout> {
        if (items.isEmpty()) return emptyList()

        val isRow = container.flexDirection == FlexDirection.Row ||
                container.flexDirection == FlexDirection.RowReverse
        val isMainAxisReversed = container.flexDirection == FlexDirection.RowReverse ||
                container.flexDirection == FlexDirection.ColumnReverse

        val mainContainerSize = if (isRow) containerWidth else containerHeight
        val crossContainerSize = if (isRow) containerHeight else containerWidth
        val mainGap = if (isRow) container.columnGap else container.rowGap
        val crossGap = if (isRow) container.rowGap else container.columnGap

        val result = arrayOfNulls<FlexItemLayout>(items.size)

        // Partition: absolute items are removed from flex flow
        val inFlowIndices = items.indices.filter { items[it].style.position !is Position.Absolute }
        val absoluteIndices = items.indices.filter { items[it].style.position is Position.Absolute }

        // ── In-flow items: run the 7-step flex algorithm ──────────────────────

        val inFlowItems = inFlowIndices.map { items[it] }

        if (inFlowItems.isNotEmpty()) {
            // Step 1 — Sort by `order` (stable sort preserves source order for ties)
            val sortedIndexed = inFlowItems.indices.sortedBy { inFlowItems[it].style.order }

            // Step 2 — Resolve flex-basis → hypothetical main size
            val lineItems = sortedIndexed.map { idx ->
                val input = inFlowItems[idx]
                val basis = resolveBasis(input, isRow, mainContainerSize)
                FlexLineItem(input, idx, basis)
            }

            // Step 3 — Collect items into flex lines
            val lines = collectLines(lineItems, container.flexWrap, mainContainerSize, mainGap)

            // Step 4 — Resolve flexible lengths (grow / shrink) per line
            lines.forEach { line -> resolveFlexibleLengths(line, mainContainerSize, mainGap) }

            // Step 5 — Determine cross size of each flex line
            lines.forEach { line -> determineCrossSize(line, container, isRow) }

            // Step 6 — Resolve alignContent (position lines on the cross axis)
            val linePositions = resolveAlignContent(
                lines, container.alignContent, crossContainerSize, crossGap
            )

            // Step 7 — Place every in-flow item into a temporary array
            val inFlowResult = arrayOfNulls<FlexItemLayout>(inFlowItems.size)
            placeItems(
                lines, linePositions, inFlowResult,
                container, isRow, isMainAxisReversed, mainContainerSize, mainGap
            )

            // Map back to original indices and apply relative offsets
            inFlowIndices.forEachIndexed { idx, origIdx ->
                var layout = inFlowResult[idx]!!
                val pos = items[origIdx].style.position
                if (pos is Position.Relative) {
                    val offsetX = (pos.left ?: 0f) - (pos.right ?: 0f)
                    val offsetY = (pos.top ?: 0f) - (pos.bottom ?: 0f)
                    layout = layout.copy(x = layout.x + offsetX, y = layout.y + offsetY)
                }
                result[origIdx] = layout
            }
        }

        // ── Absolute items: positioned against the container ──────────────────

        absoluteIndices.forEach { origIdx ->
            val item = items[origIdx]
            val abs = item.style.position as Position.Absolute
            val (natW, natH) = item.measurer.measure(null, null)

            val w = if (abs.left != null && abs.right != null)
                maxOf(0f, containerWidth - abs.left - abs.right) else natW
            val h = if (abs.top != null && abs.bottom != null)
                maxOf(0f, containerHeight - abs.top - abs.bottom) else natH

            val x = abs.left
                ?: if (abs.right != null) containerWidth - abs.right - w else 0f
            val y = abs.top
                ?: if (abs.bottom != null) containerHeight - abs.bottom - h else 0f

            result[origIdx] = FlexItemLayout(x, y, w, h)
        }

        return result.map { it!! }
    }

    // ── Step 2: resolve flex-basis ─────────────────────────────────────────────

    private fun resolveBasis(
        input: FlexItemInput,
        isRow: Boolean,
        mainContainerSize: Float,
    ): Float {
        return when (val b = input.style.flexBasis) {
            is FlexBasis.Size -> b.value
            is FlexBasis.Percentage -> {
                if (mainContainerSize < Float.MAX_VALUE / 2f) {
                    b.fraction * mainContainerSize
                } else {
                    // Unconstrained container — fall back to natural size
                    val (w, h) = input.measurer.measure(null, null)
                    if (isRow) w else h
                }
            }

            is FlexBasis.Auto -> {
                // Natural/max-content main size (unconstrained)
                val (w, h) = input.measurer.measure(null, null)
                if (isRow) w else h
            }
        }
    }

    // ── Step 3: collect lines ─────────────────────────────────────────────────

    private fun collectLines(
        items: List<FlexLineItem>,
        wrap: FlexWrap,
        mainSize: Float,
        mainGap: Float,
    ): List<FlexLine> {
        if (wrap == FlexWrap.NoWrap || mainSize >= Float.MAX_VALUE / 2f) {
            // Single line — all items on one line
            return listOf(FlexLine().also { it.items.addAll(items) })
        }

        val lines = mutableListOf<FlexLine>()
        var currentLine = FlexLine()
        var usedMainSize = 0f

        for (item in items) {
            val gapIfAdded = if (currentLine.items.isNotEmpty()) mainGap else 0f
            val wouldOverflow = usedMainSize + gapIfAdded + item.hypotheticalMainSize > mainSize

            if (wouldOverflow && currentLine.items.isNotEmpty()) {
                lines.add(currentLine)
                currentLine = FlexLine()
                usedMainSize = 0f
            }

            currentLine.items.add(item)
            usedMainSize += (if (currentLine.items.size == 1) 0f else mainGap) + item.hypotheticalMainSize
        }

        if (currentLine.items.isNotEmpty()) lines.add(currentLine)

        return if (wrap == FlexWrap.WrapReverse) lines.reversed() else lines
    }

    // ── Step 4: resolve flexible lengths ─────────────────────────────────────

    private fun resolveFlexibleLengths(line: FlexLine, mainSize: Float, mainGap: Float) {
        val n = line.items.size
        val totalHypothetical = line.items.sumOf { it.hypotheticalMainSize.toDouble() }.toFloat()
        val totalGap = if (n > 1) mainGap * (n - 1) else 0f
        // Indefinite main size: items keep their hypothetical sizes (CSS treats
        // flex-grow as inert without a definite container size).
        val freeSpace =
            if (mainSize >= Float.MAX_VALUE / 2f) 0f
            else mainSize - totalHypothetical - totalGap

        when {
            freeSpace > 0f -> {
                val totalGrow = line.items.sumOf { it.input.style.flexGrow.toDouble() }.toFloat()
                if (totalGrow > 0f) {
                    line.items.forEach { item ->
                        item.finalMainSize =
                            item.hypotheticalMainSize + freeSpace * (item.input.style.flexGrow / totalGrow)
                    }
                } else {
                    line.items.forEach { it.finalMainSize = it.hypotheticalMainSize }
                }
            }

            freeSpace < 0f -> {
                // Weighted shrink: shrink factor × hypothetical size
                val totalWeighted = line.items.sumOf {
                    (it.input.style.flexShrink * it.hypotheticalMainSize).toDouble()
                }.toFloat()
                if (totalWeighted > 0f) {
                    line.items.forEach { item ->
                        val ratio =
                            (item.input.style.flexShrink * item.hypotheticalMainSize) / totalWeighted
                        item.finalMainSize =
                            maxOf(0f, item.hypotheticalMainSize + freeSpace * ratio)
                    }
                } else {
                    line.items.forEach { it.finalMainSize = it.hypotheticalMainSize }
                }
            }

            else -> line.items.forEach { it.finalMainSize = it.hypotheticalMainSize }
        }
    }

    // ── Step 5: determine cross size ──────────────────────────────────────────

    private fun determineCrossSize(
        line: FlexLine,
        container: FlexContainerStyle,
        isRow: Boolean,
    ) {
        // Measure each item at its final main size to get the natural cross size
        line.items.forEach { item ->
            val (w, h) = item.input.measurer.measure(
                maxWidth = if (isRow) item.finalMainSize else null,
                maxHeight = if (isRow) null else item.finalMainSize,
            )
            item.measuredCrossSize = if (isRow) h else w
        }

        // Line cross size = max of non-Stretch items (Stretch items fill the line, not define it)
        val nonStretch = line.items.filter { item ->
            effectiveAlign(item, container.alignItems) != AlignSelf.Stretch
        }
        line.crossSize = if (nonStretch.isNotEmpty()) {
            nonStretch.maxOf { it.measuredCrossSize }
        } else if (line.items.isNotEmpty()) {
            line.items.maxOf { it.measuredCrossSize }
        } else {
            0f
        }

        // Finalize cross size for each item
        line.items.forEach { item ->
            item.finalCrossSize =
                if (effectiveAlign(item, container.alignItems) == AlignSelf.Stretch) {
                    line.crossSize
                } else {
                    item.measuredCrossSize
                }
        }
    }

    // ── Step 6: align-content ─────────────────────────────────────────────────

    private fun resolveAlignContent(
        lines: List<FlexLine>,
        alignContent: AlignContent,
        containerCrossSize: Float,
        crossGap: Float,
    ): FloatArray {
        val n = lines.size
        if (n == 0) return FloatArray(0)

        val totalLinesCross = lines.sumOf { it.crossSize.toDouble() }.toFloat()
        val totalGap = if (n > 1) crossGap * (n - 1) else 0f
        val positions = FloatArray(n)

        // Indefinite cross size (e.g. intrinsic measurement): no free space to
        // distribute, so every alignContent collapses to FlexStart. Skips the
        // Stretch branch's per-line growth, which would otherwise inflate
        // line.crossSize by ~Float.MAX_VALUE / n.
        if (containerCrossSize >= Float.MAX_VALUE / 2f) {
            var pos = 0f
            lines.forEachIndexed { i, line ->
                positions[i] = pos; pos += line.crossSize + crossGap
            }
            return positions
        }

        val freeSpace = containerCrossSize - totalLinesCross - totalGap

        when (alignContent) {
            AlignContent.FlexStart -> {
                var pos = 0f
                lines.forEachIndexed { i, line ->
                    positions[i] = pos; pos += line.crossSize + crossGap
                }
            }

            AlignContent.FlexEnd -> {
                var pos = maxOf(0f, freeSpace)
                lines.forEachIndexed { i, line ->
                    positions[i] = pos; pos += line.crossSize + crossGap
                }
            }

            AlignContent.Center -> {
                var pos = maxOf(0f, freeSpace / 2f)
                lines.forEachIndexed { i, line ->
                    positions[i] = pos; pos += line.crossSize + crossGap
                }
            }

            AlignContent.SpaceBetween -> {
                // crossGap always present between lines; freeSpace is distributed on top of it
                val extraPerGap = if (n > 1) maxOf(0f, freeSpace / (n - 1)) else 0f
                var pos = 0f
                lines.forEachIndexed { i, line ->
                    positions[i] = pos; pos += line.crossSize + crossGap + extraPerGap
                }
            }

            AlignContent.SpaceAround -> {
                val unit = if (n > 0) freeSpace / n else 0f
                var pos = unit / 2f
                lines.forEachIndexed { i, line ->
                    positions[i] = pos; pos += line.crossSize + crossGap + unit
                }
            }

            AlignContent.SpaceEvenly -> {
                val unit = freeSpace / (n + 1)
                var pos = unit
                lines.forEachIndexed { i, line ->
                    positions[i] = pos; pos += line.crossSize + crossGap + unit
                }
            }

            AlignContent.Stretch -> {
                val extra = if (freeSpace > 0f && n > 0) freeSpace / n else 0f
                // Expand each line's cross size; placeItems will use line.crossSize for Stretch items
                lines.forEach { it.crossSize += extra }
                var pos = 0f
                lines.forEachIndexed { i, line ->
                    positions[i] = pos; pos += line.crossSize + crossGap
                }
            }
        }

        return positions
    }

    // ── Step 7: place items ────────────────────────────────────────────────────

    private fun placeItems(
        lines: List<FlexLine>,
        linePositions: FloatArray,
        result: Array<FlexItemLayout?>,
        container: FlexContainerStyle,
        isRow: Boolean,
        isMainAxisReversed: Boolean,
        mainContainerSize: Float,
        mainGap: Float,
    ) {
        lines.forEachIndexed { lineIdx, line ->
            val lineCrossStart = linePositions[lineIdx]

            // Compute main-axis positions for this line's items
            val mainPositions = computeMainPositions(
                line.items, container.justifyContent, mainContainerSize, mainGap, isMainAxisReversed
            )

            line.items.forEachIndexed { itemIdx, item ->
                val mainPos = mainPositions[itemIdx]
                val align = effectiveAlign(item, container.alignItems)
                // Use line.crossSize for Stretch items — it may have grown via alignContent:Stretch
                val itemCross =
                    if (align == AlignSelf.Stretch) line.crossSize else item.finalCrossSize
                val crossPos =
                    computeCrossPosition(align, lineCrossStart, line.crossSize, itemCross)

                val x: Float
                val y: Float
                val w: Float
                val h: Float
                if (isRow) {
                    x = mainPos; y = crossPos; w = item.finalMainSize; h = itemCross
                } else {
                    x = crossPos; y = mainPos; w = itemCross; h = item.finalMainSize
                }
                result[item.originalIndex] = FlexItemLayout(x, y, w, h)
            }
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun computeMainPositions(
        items: List<FlexLineItem>,
        justifyContent: JustifyContent,
        mainSize: Float,
        mainGap: Float,
        isReversed: Boolean,
    ): FloatArray {
        val n = items.size
        val totalItemsSize = items.sumOf { it.finalMainSize.toDouble() }.toFloat()
        val totalGap = if (n > 1) mainGap * (n - 1) else 0f
        // Indefinite main size collapses every justifyContent to FlexStart and
        // disables reversed placement (subtracting from Float.MAX_VALUE yields
        // huge positions that overflow Compose's layout-dimension cap).
        val isMainUnbounded = mainSize >= Float.MAX_VALUE / 2f
        val freeSpace =
            if (isMainUnbounded) 0f else maxOf(0f, mainSize - totalItemsSize - totalGap)

        // startOffset: distance from "flex-start edge" to the first item
        // betweenExtra: extra space added between each pair of items (beyond mainGap)
        val startOffset: Float
        val betweenExtra: Float
        when (justifyContent) {
            JustifyContent.FlexStart -> {
                startOffset = 0f; betweenExtra = 0f
            }

            JustifyContent.FlexEnd -> {
                startOffset = freeSpace; betweenExtra = 0f
            }

            JustifyContent.Center -> {
                startOffset = freeSpace / 2f; betweenExtra = 0f
            }

            JustifyContent.SpaceBetween -> {
                startOffset = 0f
                betweenExtra = if (n > 1) freeSpace / (n - 1) else 0f
            }

            JustifyContent.SpaceAround -> {
                val unit = if (n > 0) freeSpace / n else 0f
                startOffset = unit / 2f; betweenExtra = unit
            }

            JustifyContent.SpaceEvenly -> {
                val unit = freeSpace / (n + 1)
                startOffset = unit; betweenExtra = unit
            }
        }

        val positions = FloatArray(n)
        if (isReversed && !isMainUnbounded) {
            // Reversed: item[0] is closest to the "end" edge visually; lay out from end
            var distFromEnd = startOffset
            for (i in 0 until n) {
                positions[i] = mainSize - distFromEnd - items[i].finalMainSize
                distFromEnd += items[i].finalMainSize + mainGap + betweenExtra
            }
        } else {
            var pos = startOffset
            for (i in 0 until n) {
                positions[i] = pos
                pos += items[i].finalMainSize + mainGap + betweenExtra
            }
        }
        return positions
    }

    private fun computeCrossPosition(
        align: AlignSelf,
        lineCrossStart: Float,
        lineCrossSize: Float,
        itemCrossSize: Float,
    ): Float = when (align) {
        AlignSelf.Auto, AlignSelf.FlexStart, AlignSelf.Stretch -> lineCrossStart
        AlignSelf.FlexEnd -> lineCrossStart + lineCrossSize - itemCrossSize
        AlignSelf.Center -> lineCrossStart + (lineCrossSize - itemCrossSize) / 2f
        AlignSelf.Baseline -> lineCrossStart // simplified: treat baseline as FlexStart
    }

    private fun effectiveAlign(item: FlexLineItem, containerAlignItems: AlignItems): AlignSelf =
        when (item.input.style.alignSelf) {
            AlignSelf.Auto -> containerAlignItems.toAlignSelf()
            else -> item.input.style.alignSelf
        }
}

private fun AlignItems.toAlignSelf(): AlignSelf = when (this) {
    AlignItems.FlexStart -> AlignSelf.FlexStart
    AlignItems.FlexEnd -> AlignSelf.FlexEnd
    AlignItems.Center -> AlignSelf.Center
    AlignItems.Baseline -> AlignSelf.Baseline
    AlignItems.Stretch -> AlignSelf.Stretch
}
