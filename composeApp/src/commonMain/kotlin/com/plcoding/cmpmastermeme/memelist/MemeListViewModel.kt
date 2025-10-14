package com.plcoding.cmpmastermeme.memelist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class MemeListViewModel : ViewModel() {

    private val _state = MutableStateFlow(MemeListState())
    val state = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = MemeListState()
        )

    fun onAction(action: MemeListAction) {
        when(action) {
            MemeListAction.OnCreateNewMeme -> showTemplatePicker()
            MemeListAction.OnCancelNewMemeCreation -> hideTemplatePicker()

            /* Handled in UI */
            is MemeListAction.OnTemplateSelected -> Unit
        }
    }

    private fun hideTemplatePicker() {
        _state.update {
            it.copy(isCreatingNewMeme = false)
        }
    }

    private fun showTemplatePicker() {
        _state.update {
            it.copy(isCreatingNewMeme = true)
        }
    }
}