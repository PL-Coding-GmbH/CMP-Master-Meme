package com.plcoding.cmpmastermeme.editmeme.models

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import com.plcoding.cmpmastermeme.core.domain.MemeTemplate
import com.plcoding.cmpmastermeme.core.domain.PickedImageData

sealed interface EditMemeAction {
    // Navigation & Dialog Actions
    data object OnGoBackClick : EditMemeAction
    data object OnConfirmLeaveWithoutSaving : EditMemeAction
    data object OnCancelLeaveWithoutSaving : EditMemeAction

    // Meme Completion Actions
    data object OnCompleteEditingClick : EditMemeAction
    data class OnSaveMemeClick(val memeTemplate: MemeTemplate) : EditMemeAction
    data class OnShareMemeClick(val memeTemplate: MemeTemplate) : EditMemeAction
    data object OnContinueEditing : EditMemeAction
    data object ClearSelectedMemeText : EditMemeAction

    data class OnPickedImage(val data: PickedImageData): EditMemeAction

    // Text Management Actions
    data object OnAddNewMemeTextClick : EditMemeAction
    data class OnSelectMemeText(val id: Int) : EditMemeAction
    data class OnEditMemeText(val id: Int) : EditMemeAction
    data class OnMemeTextChange(val id: Int, val text: String) : EditMemeAction
    data class OnDeleteMemeText(val id: Int) : EditMemeAction

    // Text Positioning Actions
    data class OnMemeTextTransformChanged(
        val id: Int,
        val offset: Offset,
        val rotation: Float,
        val scale: Float
    ) : EditMemeAction

    // Layout Actions
    data class OnContainerSizeChanged(val size: IntSize) : EditMemeAction
}