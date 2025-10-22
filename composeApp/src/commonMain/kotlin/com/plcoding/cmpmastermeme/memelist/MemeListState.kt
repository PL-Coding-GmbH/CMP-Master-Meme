package com.plcoding.cmpmastermeme.memelist

import com.plcoding.cmpmastermeme.core.presentation.MemeTemplate

data class MemeListState(
    val templates: List<MemeTemplate> = MemeTemplate.entries,
    val isCreatingNewMeme: Boolean = false,
)
