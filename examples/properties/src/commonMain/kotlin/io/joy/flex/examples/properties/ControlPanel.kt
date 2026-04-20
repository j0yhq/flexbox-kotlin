package io.joy.flex.examples.properties

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.joy.flex.AlignContent
import io.joy.flex.AlignItems
import io.joy.flex.AlignSelf
import io.joy.flex.FlexBasis
import io.joy.flex.FlexDirection
import io.joy.flex.FlexWrap
import io.joy.flex.JustifyContent
import io.joy.flex.Overflow

@Composable
fun ControlPanel(state: PropertyState, modifier: Modifier = Modifier) {
    var selectedItemTab by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        SectionHeader("Container")

        EnumSelector("Direction", FlexDirection.entries, state.flexDirection) {
            state.flexDirection = it
        }
        EnumSelector("Wrap", FlexWrap.entries, state.flexWrap) {
            state.flexWrap = it
        }
        EnumSelector("Justify Content", JustifyContent.entries, state.justifyContent) {
            state.justifyContent = it
        }
        EnumSelector("Align Items", AlignItems.entries, state.alignItems) {
            state.alignItems = it
        }
        EnumSelector("Align Content", AlignContent.entries, state.alignContent) {
            state.alignContent = it
        }

        LabeledSlider("Row Gap", state.rowGap, 0f..50f) { state.rowGap = it }
        LabeledSlider("Column Gap", state.columnGap, 0f..50f) { state.columnGap = it }
        EnumSelector("Overflow", Overflow.entries, state.overflow) { state.overflow = it }

        Spacer(Modifier.height(8.dp))
        SectionHeader("Items")

        PrimaryTabRow(selectedTabIndex = selectedItemTab) {
            state.items.forEachIndexed { index, item ->
                Tab(
                    selected = selectedItemTab == index,
                    onClick = { selectedItemTab = index },
                    text = { Text(item.label) },
                )
            }
        }

        ItemControls(state.items[selectedItemTab])
    }
}

@Composable
private fun ItemControls(item: ItemState) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        LabeledSlider("Flex Grow", item.flexGrow, 0f..3f) { item.flexGrow = it }
        LabeledSlider("Flex Shrink", item.flexShrink, 0f..3f) { item.flexShrink = it }
        FlexBasisControl(item)
        EnumSelector("Align Self", AlignSelf.entries, item.alignSelf) { item.alignSelf = it }
        OrderControl(item)
    }
}

@Composable
private fun FlexBasisControl(item: ItemState) {
    val isAuto = item.flexBasis is FlexBasis.Auto
    val isSize = item.flexBasis is FlexBasis.Size
    val isPercentage = item.flexBasis is FlexBasis.Percentage
    val sizeValue = (item.flexBasis as? FlexBasis.Size)?.value ?: 80f
    val percentageValue = (item.flexBasis as? FlexBasis.Percentage)?.fraction ?: 0.5f

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text("Flex Basis", style = MaterialTheme.typography.labelMedium)
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FilterChip(
                selected = isAuto,
                onClick = { item.flexBasis = FlexBasis.Auto },
                label = { Text("Auto") },
            )
            FilterChip(
                selected = isSize,
                onClick = { item.flexBasis = FlexBasis.Size(sizeValue) },
                label = { Text("Size") },
            )
            FilterChip(
                selected = isPercentage,
                onClick = { item.flexBasis = FlexBasis.Percentage(percentageValue) },
                label = { Text("%") },
            )
            when {
                isSize -> Text("${sizeValue.toInt()}dp", style = MaterialTheme.typography.bodySmall)
                isPercentage -> Text(
                    "${(percentageValue * 100).toInt()}%",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        when {
            isSize -> Slider(
                value = sizeValue,
                onValueChange = { item.flexBasis = FlexBasis.Size(it) },
                valueRange = 10f..200f,
                modifier = Modifier.fillMaxWidth(),
            )

            isPercentage -> Slider(
                value = percentageValue,
                onValueChange = { item.flexBasis = FlexBasis.Percentage(it) },
                valueRange = 0f..1f,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun OrderControl(item: ItemState) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text("Order", style = MaterialTheme.typography.labelMedium)
        Spacer(Modifier.weight(1f))
        TextButton(onClick = { if (item.order > -5) item.order-- }) { Text("-") }
        Text(
            text = item.order.toString(),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(32.dp),
        )
        TextButton(onClick = { if (item.order < 5) item.order++ }) { Text("+") }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun <T> EnumSelector(
    label: String,
    values: List<T>,
    selected: T,
    onSelect: (T) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            values.forEach { value ->
                FilterChip(
                    selected = value == selected,
                    onClick = { onSelect(value) },
                    label = { Text(value.toString(), style = MaterialTheme.typography.labelSmall) },
                )
            }
        }
    }
}

@Composable
private fun LabeledSlider(
    label: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(label, style = MaterialTheme.typography.labelMedium)
            Text("${value.toInt()}", style = MaterialTheme.typography.bodySmall)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun SectionHeader(title: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        HorizontalDivider()
    }
}
