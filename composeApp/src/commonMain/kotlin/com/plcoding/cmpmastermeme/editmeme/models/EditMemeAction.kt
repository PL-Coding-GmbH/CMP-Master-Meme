package com.plcoding.cmpmastermeme.editmeme.models

sealed interface EditMemeAction {
    data object OnGoBackClick : EditMemeAction
    data object OnSaveMemeClick : EditMemeAction
    data object OnAddNewMemeTextClick : EditMemeAction
    data class OnMemeTextChange(val id: Int, val text: String) : EditMemeAction
    data class OnDeleteMemeText(val id: Int) : EditMemeAction
    data class OnSelectMemeText(val id: Int) : EditMemeAction
    data class OnEditMemeText(val id: Int) : EditMemeAction
}