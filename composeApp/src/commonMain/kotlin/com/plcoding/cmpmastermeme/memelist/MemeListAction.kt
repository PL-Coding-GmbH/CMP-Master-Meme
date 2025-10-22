package com.plcoding.cmpmastermeme.memelist

import com.plcoding.cmpmastermeme.core.presentation.MemeTemplate

sealed interface MemeListAction {
    data class OnTemplateSelected(val template: MemeTemplate) : MemeListAction
}