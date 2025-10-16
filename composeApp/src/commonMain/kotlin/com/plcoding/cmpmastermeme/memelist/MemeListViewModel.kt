package com.plcoding.cmpmastermeme.memelist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plcoding.cmpmastermeme.core.domain.MemeDataSource
import com.plcoding.cmpmastermeme.core.domain.SendableFileManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MemeListViewModel(
    private val memeDataSource: MemeDataSource,
    private val sendableFileManager: SendableFileManager,
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
            MemeListAction.OnCreateNewMeme -> toggleTemplatePicker(isVisible = true)
            MemeListAction.OnHideTemplateOptions -> toggleTemplatePicker(isVisible = false)
            MemeListAction.OnClearMemeSelection -> setSelectedMeme(meme = null)
            is MemeListAction.OnSelectMeme -> setSelectedMeme(meme = action.meme)
            is MemeListAction.OnShareMemeClick -> shareMeme(action.uri)
            is MemeListAction.OnDeleteMemeClick -> showDeleteMemeConfirmation(meme = action.meme)
            MemeListAction.CancelMemeDeletion -> cancelMemeDeletionAndSelection()
            is MemeListAction.OnConfirmDeleteMeme -> delete(meme = action.meme)

            /* Handled in UI */
            is MemeListAction.OnTemplateSelected -> Unit
        }
    }

    private fun delete(meme: MemeUi) = viewModelScope.launch {
        memeDataSource.delete(meme.id)
        _state.update {
            it.copy(
                memeBeingDeleted = null,
                selectedMeme = null
            )
        }
        // todo show confirmation toast
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

    private fun cancelMemeDeletionAndSelection() {
        _state.update {
            it.copy(
                selectedMeme = null,
                memeBeingDeleted = null
            )
        }
    }

    private fun showDeleteMemeConfirmation(meme: MemeUi) {
        _state.update {
            it.copy(memeBeingDeleted = meme)
        }
    }

    private fun shareMeme(uri: String) = viewModelScope.launch {
        sendableFileManager.shareFile(filePath = uri)
    }

    private fun toggleTemplatePicker(isVisible: Boolean) {
        _state.update {
            it.copy(isCreatingNewMeme = isVisible)
        }
    }

    private fun setSelectedMeme(meme: MemeUi?) {
        _state.update {
            it.copy(selectedMeme = meme)
        }
    }
}