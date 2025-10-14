package com.plcoding.cmpmastermeme.core.domain

import androidx.compose.ui.unit.IntSize
import com.plcoding.cmpmastermeme.editmeme.models.MemeText

actual class MemeExporter {
    actual suspend fun exportMeme(
        backgroundImageBytes: ByteArray,
        textBoxes: List<MemeText>,
        canvasSize: IntSize,
        fileName: String
    ): Result<FilePath> {
        TODO("Not yet implemented")
    }
}