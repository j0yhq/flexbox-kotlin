package io.joy.flex.examples.properties

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Flex Properties",
    ) {
        App()
    }
}
