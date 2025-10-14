package com.plcoding.cmpmastermeme.memelist

import com.plcoding.cmpmastermeme.core.domain.MemeTemplate

data class MemeListState(
    val memes: List<MemeUi> = emptyList(),
    val templates: List<MemeTemplate> = MemeTemplate.entries,
    val isCreatingNewMeme: Boolean = false
)
