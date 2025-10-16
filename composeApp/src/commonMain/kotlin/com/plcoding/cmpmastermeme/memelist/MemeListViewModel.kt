package com.plcoding.cmpmastermeme.memelist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plcoding.cmpmastermeme.core.domain.MemeDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class MemeListViewModel(
    private val memeDataSource: MemeDataSource
) : ViewModel() {

    private var hasInitialized = false

    private val _state = MutableStateFlow(MemeListState())
    val state = _state
        .onStart {
            observeAllMemes()
            hasInitialized = true
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = MemeListState()
        )

    fun onAction(action: MemeListAction) {
        when (action) {
            MemeListAction.OnCreateNewMeme -> showTemplatePicker()
            MemeListAction.OnCancelNewMemeCreation -> hideTemplatePicker()

            /* Handled in UI */
            is MemeListAction.OnTemplateSelected -> Unit
        }
    }

    private fun observeAllMemes() {
        memeDataSource.observeAll()
            .onEach { memes ->
                _state.update {
                    it.copy(memes = memes.toMemeUiList())
                }
            }
            .launchIn(viewModelScope)
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