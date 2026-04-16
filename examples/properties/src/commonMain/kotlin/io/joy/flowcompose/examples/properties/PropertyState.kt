package io.joy.flowcompose.examples.properties

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import io.joy.flowcompose.AlignContent
import io.joy.flowcompose.AlignItems
import io.joy.flowcompose.AlignSelf
import io.joy.flowcompose.FlexBasis
import io.joy.flowcompose.FlexContainerStyle
import io.joy.flowcompose.FlexDirection
import io.joy.flowcompose.FlexItemStyle
import io.joy.flowcompose.FlexWrap
import io.joy.flowcompose.JustifyContent
import io.joy.flowcompose.Overflow

@Stable
class ItemState(
    val label: String,
    val color: Color,
    val baseWidth: Int,
    val baseHeight: Int,
) {
    var flexGrow by mutableFloatStateOf(0f)
    var flexShrink by mutableFloatStateOf(1f)
    var flexBasis by mutableStateOf<FlexBasis>(FlexBasis.Auto)
    var alignSelf by mutableStateOf(AlignSelf.Auto)
    var order by mutableIntStateOf(0)

    fun toFlexItemStyle() = FlexItemStyle(
        order = order,
        flexGrow = flexGrow,
        flexShrink = flexShrink,
        flexBasis = flexBasis,
        alignSelf = alignSelf,
    )
}

@Stable
class PropertyState {
    var flexDirection by mutableStateOf(FlexDirection.Row)
    var flexWrap by mutableStateOf(FlexWrap.NoWrap)
    var justifyContent by mutableStateOf(JustifyContent.FlexStart)
    var alignItems by mutableStateOf(AlignItems.Stretch)
    var alignContent by mutableStateOf(AlignContent.Stretch)
    var rowGap by mutableFloatStateOf(0f)
    var columnGap by mutableFloatStateOf(0f)
    var overflow by mutableStateOf(Overflow.Visible)

    val items = mutableStateListOf(
        ItemState("1", Color(0xFFE53935), baseWidth = 60, baseHeight = 60),
        ItemState("2", Color(0xFF1E88E5), baseWidth = 80, baseHeight = 80),
        ItemState("3", Color(0xFF43A047), baseWidth = 50, baseHeight = 90),
        ItemState("4", Color(0xFFF59300), baseWidth = 100, baseHeight = 60),
        ItemState("5", Color(0xFF8E24AA), baseWidth = 70, baseHeight = 70),
    )

    fun toContainerStyle() = FlexContainerStyle(
        flexDirection = flexDirection,
        flexWrap = flexWrap,
        justifyContent = justifyContent,
        alignItems = alignItems,
        alignContent = alignContent,
        rowGap = rowGap,
        columnGap = columnGap,
        overflow = overflow,
    )
}
