package com.plcoding.cmpmastermeme.editmeme.presentation.models

sealed interface EditMemeEvent {
    data object SavedMeme : EditMemeEvent
    data object ConfirmedLeaveWithoutSaving : EditMemeEvent
}