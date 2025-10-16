package com.plcoding.cmpmastermeme.editmeme.models

import androidx.compose.ui.unit.IntSize

data class EditMemeState(
    val templateSize: IntSize = IntSize.Zero,
    val memeTexts: List<MemeText> = emptyList(),
    val selectedTextBoxId: Int? = null,
    val editingTextBoxId: Int? = null,
    val isFinalisingMeme: Boolean = false,
    val isLeavingWithoutSaving: Boolean = false
)