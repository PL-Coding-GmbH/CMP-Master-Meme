package com.plcoding.cmpmastermeme.editmeme.models

sealed interface EditMemeEvent {
    data object SavedMeme : EditMemeEvent
}