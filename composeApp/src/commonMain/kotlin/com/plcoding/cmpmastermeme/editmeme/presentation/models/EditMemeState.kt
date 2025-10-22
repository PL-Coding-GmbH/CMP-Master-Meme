package com.plcoding.cmpmastermeme.editmeme.presentation.models

import androidx.compose.ui.unit.IntSize

data class EditMemeState(
    val templateSize: IntSize = IntSize.Zero,
    val memeTexts: List<MemeText> = emptyList(),
    val textBoxInteraction: TextBoxInteractionState = TextBoxInteractionState.None,
    val isLeavingWithoutSaving: Boolean = false,
)

sealed class TextBoxInteractionState {
    data object None : TextBoxInteractionState()
    data class Selected(val textBoxId: String) : TextBoxInteractionState()
    data class Editing(val textBoxId: String) : TextBoxInteractionState()

    val targetedTextBoxId: String?
        get() = when (this) {
            None -> null
            is Editing -> textBoxId
            is Selected -> textBoxId
        }
}
