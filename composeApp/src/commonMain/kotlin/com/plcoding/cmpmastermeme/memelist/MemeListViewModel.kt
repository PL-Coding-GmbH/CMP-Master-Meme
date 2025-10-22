package com.plcoding.cmpmastermeme.memelist

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MemeListViewModel: ViewModel() {

    private val _state = MutableStateFlow(MemeListState())
    val state = _state.asStateFlow()

}