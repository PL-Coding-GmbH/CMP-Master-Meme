package com.plcoding.cmpmastermeme.memelist

sealed interface MemeListEvent {
    data object MemeDeleted : MemeListEvent
}
