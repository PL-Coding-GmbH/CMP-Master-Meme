package com.plcoding.cmpmastermeme.editmeme.models

import androidx.compose.ui.unit.IntSize
import com.plcoding.cmpmastermeme.core.domain.MemeTemplate

sealed interface EditMemeAction {
    data object OnGoBackClick : EditMemeAction
    data object OnCompleteEditingClick : EditMemeAction
    data class OnSaveMemeClick(val memeTemplate: MemeTemplate) : EditMemeAction
    data object OnAddNewMemeTextClick : EditMemeAction
    data class OnMemeTextChange(val id: Int, val text: String) : EditMemeAction
    data class OnMemeTextPositionChange(val id: Int, val x: Float, val y: Float) : EditMemeAction
    data class OnDeleteMemeText(val id: Int) : EditMemeAction
    data class OnSelectMemeText(val id: Int) : EditMemeAction
    data class OnEditMemeText(val id: Int) : EditMemeAction
    data class OnContainerSizeChanged(val size: IntSize) : EditMemeAction
    data object OnContinueEditing : EditMemeAction
}