package com.plcoding.cmpmastermeme.editmeme

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plcoding.cmpmastermeme.editmeme.models.EditMemeAction
import com.plcoding.cmpmastermeme.editmeme.models.EditMemeState
import com.plcoding.cmpmastermeme.editmeme.models.MemeText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditMemeViewModel(

) : ViewModel() {

    private val _state = MutableStateFlow(EditMemeState())
    val state = _state.asStateFlow()

    fun onAction(action: EditMemeAction) {
        when(action) {
            EditMemeAction.OnAddNewMemeTextClick -> createTextBox()
            EditMemeAction.OnSaveMemeClick -> TODO()
            is EditMemeAction.OnDeleteMemeText -> removeTextBox(action.id)
            is EditMemeAction.OnEditMemeText -> editTextBox(action.id)
            is EditMemeAction.OnSelectMemeText -> selectTextBox(action.id)
            is EditMemeAction.OnMemeTextChange -> onTextBoxTextChange(textBoxId = action.id, text = action.text)

            /* Handled in UI */
            EditMemeAction.OnGoBackClick -> Unit
        }
    }

    private fun onTextBoxTextChange(textBoxId: Int, text: String) {
        _state.update {
            it.copy(
                memeTexts = it.memeTexts.map { textBox ->
                    if (textBox.id == textBoxId) {
                        textBox.copy(text = text)
                    } else textBox
                }
            )
        }
    }

    private fun selectTextBox(id: Int) {
        _state.update {
            it.copy(
                selectedTextBoxId = id,
                editingTextBoxId = null
            )
        }
    }

    private fun editTextBox(id: Int) {
        _state.update {
            it.copy(
                selectedTextBoxId = null,
                editingTextBoxId = id
            )
        }
    }

    private fun removeTextBox(id: Int) {
        _state.update {
            it.copy(
                memeTexts = it.memeTexts.filter { it.id != id }
            )
        }
    }

    private fun createTextBox() = viewModelScope.launch {
        val currentState = state.value
        val newId = currentState.memeTexts.maxOfOrNull { it.id }?.inc() ?: 1
        val position = Offset(
            x = currentState.templateSize.width / 2f,
            y = currentState.templateSize.height / 2f + (currentState.memeTexts.size * 40f)
        )

        val newBox = MemeText(
            id = newId,
            text = "Tap to edit",
            position = position,
        )

        _state.update {
            it.copy(
                memeTexts = currentState.memeTexts + newBox
            )
        }
    }

}