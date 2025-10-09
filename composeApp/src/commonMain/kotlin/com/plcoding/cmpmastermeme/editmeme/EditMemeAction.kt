package com.plcoding.cmpmastermeme.editmeme

sealed interface EditMemeAction {
    data object OnGoBackClick : EditMemeAction
    data object OnSaveMemeClick : EditMemeAction
    data object OnAddTextToTemplateClick : EditMemeAction
}