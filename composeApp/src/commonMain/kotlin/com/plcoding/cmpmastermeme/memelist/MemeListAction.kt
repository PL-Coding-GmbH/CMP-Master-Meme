package com.plcoding.cmpmastermeme.memelist

import com.plcoding.cmpmastermeme.core.domain.MemeTemplate

sealed interface MemeListAction {
    data object OnCreateNewMeme : MemeListAction
    data object OnStopPickTemplate : MemeListAction
    data class OnTemplateSelected(val template: MemeTemplate) : MemeListAction
}