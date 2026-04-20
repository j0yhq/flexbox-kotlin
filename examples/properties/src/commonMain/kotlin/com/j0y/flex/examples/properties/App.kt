package com.j0y.flex.examples.properties

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    val state = remember { PropertyState() }

    MaterialTheme {
        Scaffold(
            topBar = {
                TopAppBar(title = { Text("Flex Properties") })
            },
        ) { padding ->
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
            ) {
                if (maxWidth >= 600.dp) {
                    // Wide layout: controls on the left, preview on the right
                    Row(Modifier.fillMaxSize()) {
                        ControlPanel(
                            state = state,
                            modifier = Modifier
                                .width(320.dp)
                                .fillMaxHeight(),
                        )
                        FlexPreview(
                            state = state,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .padding(16.dp),
                        )
                    }
                } else {
                    // Narrow layout: preview on top, controls below
                    Column(Modifier.fillMaxSize()) {
                        FlexPreview(
                            state = state,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                        )
                        ControlPanel(
                            state = state,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                        )
                    }
                }
            }
        }
    }
}
