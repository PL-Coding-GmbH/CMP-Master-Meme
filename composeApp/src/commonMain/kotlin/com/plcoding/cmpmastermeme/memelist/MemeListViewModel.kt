package com.plcoding.cmpmastermeme.memelist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plcoding.cmpmastermeme.core.domain.FilePathResolver
import com.plcoding.cmpmastermeme.core.domain.SendableFileManager
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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
            is MemeListAction.OnSelectMeme -> setSelectedMeme(meme = action.meme)

            /* Handled in UI */
            is MemeListAction.OnTemplateSelected -> Unit
            else -> Unit
        }
    }

    private fun setSelectedMeme(meme: MemeUi?) {
        _state.update {
            it.copy(selectedMeme = meme)
        }
    }
}