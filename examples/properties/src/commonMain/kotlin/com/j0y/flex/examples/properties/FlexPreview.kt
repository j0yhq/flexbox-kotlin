package com.j0y.flex.examples.properties

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.j0y.flex.FlexBox

@Composable
fun FlexPreview(state: PropertyState, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .border(1.dp, MaterialTheme.colorScheme.outline)
            .background(MaterialTheme.colorScheme.surfaceContainer),
    ) {
        FlexBox(
            containerStyle = state.toContainerStyle(),
            modifier = Modifier.fillMaxSize(),
        ) {
            state.items.forEach { item ->
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(item.baseWidth.dp, item.baseHeight.dp)
                        .background(item.color)
                        .flexItem(item.toFlexItemStyle()),
                ) {
                    Text(
                        text = item.label,
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }
        }
    }
}
