package com.plcoding.cmpmastermeme.editmeme.presentation.models

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import com.plcoding.cmpmastermeme.core.presentation.MemeTemplate

sealed interface EditMemeAction {
    // Navigation & Dialog Actions
    data object OnGoBackClick : EditMemeAction
    data object OnConfirmLeaveWithoutSaving : EditMemeAction
    data object OnCancelLeaveWithoutSaving : EditMemeAction

    // Meme Completion Actions
    data class OnSaveMemeClick(val memeTemplate: MemeTemplate) : EditMemeAction
    data object ClearSelectedMemeText : EditMemeAction

    // Text Management Actions
    data object OnAddNewMemeTextClick : EditMemeAction
    data class OnSelectMemeText(val id: String) : EditMemeAction
    data class OnEditMemeText(val id: String) : EditMemeAction
    data class OnMemeTextChange(val id: String, val text: String) : EditMemeAction
    data class OnDeleteMemeText(val id: String) : EditMemeAction

    // Text Positioning Actions
    data class OnMemeTextTransformChanged(
        val id: String,
        val offset: Offset,
        val rotation: Float,
        val scale: Float
    ) : EditMemeAction

    // Text Styling Actions
    data class OnMemeTextFontSizeChange(val id: String, val fontSize: Float) : EditMemeAction

    // Layout Actions
    data class OnContainerSizeChanged(val size: IntSize) : EditMemeAction
}