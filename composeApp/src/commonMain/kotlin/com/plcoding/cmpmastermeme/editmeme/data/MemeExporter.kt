package com.plcoding.cmpmastermeme.editmeme.data

import androidx.compose.ui.unit.IntSize
import com.plcoding.cmpmastermeme.editmeme.domain.FilePath
import com.plcoding.cmpmastermeme.editmeme.domain.SaveToStorageStrategy
import com.plcoding.cmpmastermeme.editmeme.presentation.models.MemeText
import kotlin.time.Clock

expect class MemeExporter {
    suspend fun exportMeme(
        backgroundImageBytes: ByteArray,
        textBoxes: List<MemeText>,
        canvasSize: IntSize,
        fileName: String = "meme_${Clock.System.now().toEpochMilliseconds()}.png",
        saveStrategy: SaveStrategy
    ): Result<FilePath>
}