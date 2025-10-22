package com.plcoding.cmpmastermeme.memelist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class MemeListViewModel: ViewModel() {

    private var hasInitialized = false

    private val _state = MutableStateFlow(MemeListState())
    val state = _state
        .onStart {
            hasInitialized = true
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = MemeListState()
        )

    fun onAction(action: MemeListAction) {
        when (action) {
            /* Handled in UI */
            is MemeListAction.OnTemplateSelected -> Unit
            else -> Unit
        }
    }

}