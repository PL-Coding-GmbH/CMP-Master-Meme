package com.plcoding.cmpmastermeme

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.plcoding.cmpmastermeme.di.initKoin

fun main() = application {

    initKoin()

    Window(
        onCloseRequest = ::exitApplication,
        title = "CMPMasterMeme",
    ) {
        App()
    }
}