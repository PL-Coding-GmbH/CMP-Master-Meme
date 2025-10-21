package com.plcoding.cmpmastermeme.core.domain

import androidx.compose.ui.unit.IntSize
import com.plcoding.cmpmastermeme.editmeme.models.MemeElement
import kotlin.time.Clock

expect class MemeExporter {
    suspend fun exportMeme(
        backgroundImageBytes: ByteArray,
        textBoxes: List<MemeElement.Text>,
        canvasSize: IntSize,
        fileName: String = "meme_${Clock.System.now().toEpochMilliseconds()}.png",
        saveStrategy: SaveToStorageStrategy
    ): Result<FilePath>
}