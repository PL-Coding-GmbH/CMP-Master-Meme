package com.plcoding.cmpmastermeme.memelist

import com.plcoding.cmpmastermeme.core.domain.MemeTemplate

sealed interface MemeListAction {
    data object OnCreateNewMeme : MemeListAction
    data object OnHideTemplateOptions : MemeListAction

    data class OnTemplateSelected(val template: MemeTemplate) : MemeListAction

    data class OnSelectMeme(val meme: MemeUi) : MemeListAction
    data object OnClearMemeSelection : MemeListAction

    data class OnShareMemeClick(val uri: String) : MemeListAction
    data class OnDeleteMemeClick(val meme: MemeUi) : MemeListAction
    data object CancelMemeDeletion : MemeListAction
    data class OnConfirmDeleteMeme(val meme: MemeUi) : MemeListAction
}