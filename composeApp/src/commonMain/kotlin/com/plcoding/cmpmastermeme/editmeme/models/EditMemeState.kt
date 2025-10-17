package com.plcoding.cmpmastermeme.editmeme.models

import androidx.compose.ui.unit.IntSize

data class EditMemeState(
    val templateSize: IntSize = IntSize.Zero,
    val memeTexts: List<MemeText> = emptyList(),
    val textBoxInteraction: TextBoxInteractionState = TextBoxInteractionState.None,
    val isFinalisingMeme: Boolean = false,
    val isLeavingWithoutSaving: Boolean = false
)

sealed class TextBoxInteractionState {
    data object None : TextBoxInteractionState()
    data class Selected(val textBoxId: Int) : TextBoxInteractionState()
    data class Editing(val textBoxId: Int) : TextBoxInteractionState()

    val targetedTextBoxId: Int?
        get() = when (this) {
            None -> null
            is Editing -> textBoxId
            is Selected -> textBoxId
        }
}
