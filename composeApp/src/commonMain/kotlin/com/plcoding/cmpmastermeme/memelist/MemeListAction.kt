package com.plcoding.cmpmastermeme.memelist

import com.plcoding.cmpmastermeme.core.domain.MemeTemplate

sealed interface MemeListAction {
    data class OnTemplateSelected(val template: MemeTemplate) : MemeListAction

    data class OnSelectMeme(val meme: MemeUi) : MemeListAction
}