package com.plcoding.cmpmastermeme.editmeme.presentation

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plcoding.cmpmastermeme.core.presentation.MemeTemplate
import com.plcoding.cmpmastermeme.editmeme.data.MemeExporter
import com.plcoding.cmpmastermeme.editmeme.domain.SaveToStorageStrategy
import com.plcoding.cmpmastermeme.editmeme.presentation.models.EditMemeAction
import com.plcoding.cmpmastermeme.editmeme.presentation.models.EditMemeEvent
import com.plcoding.cmpmastermeme.editmeme.presentation.models.EditMemeState
import com.plcoding.cmpmastermeme.editmeme.presentation.models.MemeText
import com.plcoding.cmpmastermeme.editmeme.presentation.models.TextBoxInteractionState
import com.plcoding.cmpmastermeme.editmeme.presentation.util.ShareSheetManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.getDrawableResourceBytes
import org.jetbrains.compose.resources.getSystemResourceEnvironment

class EditMemeViewModel(
    private val memeExporter: MemeExporter,
    private val shareSheetManager: ShareSheetManager,
    private val saveStrategy: SaveToStorageStrategy
) : ViewModel() {

    private val _state = MutableStateFlow(EditMemeState())
    val state = _state.asStateFlow()

    private val eventChannel = Channel<EditMemeEvent>()
    val events = eventChannel.receiveAsFlow()

    private var selectedTextFontSizeCache: Float? = null

    fun onAction(action: EditMemeAction) {
        when (action) {
            EditMemeAction.OnAddNewMemeTextClick -> createTextBox()
            is EditMemeAction.OnEditMemeText -> editTextBox(action.id)
            is EditMemeAction.OnSaveMemeClick -> shareMeme(action.memeTemplate)
            is EditMemeAction.OnSelectMemeText -> selectTextBox(action.id)
            is EditMemeAction.OnMemeTextChange -> onTextBoxTextChange(
                textBoxId = action.id,
                text = action.text
            )

            is EditMemeAction.OnMemeTextTransformChanged -> onTextBoxPositionChange(
                textBoxId = action.id,
                offset = action.offset,
                rotation = action.rotation,
                scale = action.scale
            )

            is EditMemeAction.OnContainerSizeChanged -> updateTemplateSize(action.size)
            is EditMemeAction.OnMemeTextFontSizeChange -> onTextBoxFontSizeChange(
                textBoxId = action.id,
                fontSize = action.fontSize
            )
            EditMemeAction.OnGoBackClick -> showLeaveConfirmationIfEdited()
            EditMemeAction.OnCancelLeaveWithoutSaving -> toggleLeaveEditorConfirmation(show = false)
            EditMemeAction.OnConfirmLeaveWithoutSaving -> leaveWithoutSaving()
            EditMemeAction.ClearSelectedMemeText -> clearSelectedMemeText()
            else -> Unit
        }
    }

    private fun clearSelectedMemeText() {
        _state.update { it.copy(
            textBoxInteraction = TextBoxInteractionState.None
        ) }
    }

    private fun showLeaveConfirmationIfEdited() = viewModelScope.launch {
        if (state.value.memeTexts.isEmpty()) {
            eventChannel.send(EditMemeEvent.ConfirmedLeaveWithoutSaving)
        } else toggleLeaveEditorConfirmation(show = true)
    }

    private fun leaveWithoutSaving() = viewModelScope.launch {
        _state.update {
            it.copy(isLeavingWithoutSaving = false)
        }
        // simplest way to first let the dialog hide and then trigger navigation
        delay(100)
        eventChannel.send(EditMemeEvent.ConfirmedLeaveWithoutSaving)
    }

    private fun toggleLeaveEditorConfirmation(show: Boolean) {
        _state.update {
            it.copy(isLeavingWithoutSaving = show)
        }
    }

    private fun shareMeme(memeTemplate: MemeTemplate) = viewModelScope.launch {
        memeExporter.exportMeme(
            backgroundImageBytes = memeTemplate.drawableResource.getBytes(),
            textBoxes = state.value.memeTexts,
            canvasSize = state.value.templateSize,
            saveStrategy = saveStrategy
        )
            .onSuccess {
                shareSheetManager.shareFile(it, "image/jpeg")
            }
            .onFailure {
                // TODO show failure toast
            }
    }

    /*
        Couldn't determine if this function was main-safe
        See https://www.jetbrains.com/help/kotlin-multiplatform-dev/whats-new-compose-1610.html#experimental-byte-array-functions-for-images-and-fonts
     */
    private suspend fun DrawableResource.getBytes(): ByteArray = withContext(Dispatchers.IO) {
        return@withContext getDrawableResourceBytes(
            environment = getSystemResourceEnvironment(),
            resource = this@getBytes
        )
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

    private fun onTextBoxPositionChange(
        textBoxId: Int,
        offset: Offset,
        rotation: Float,
        scale: Float
    ) {
        _state.update {
            it.copy(
                memeTexts = it.memeTexts.map { textBox ->
                    if (textBox.id == textBoxId) {
                        textBox.copy(
                            offset = offset,
                            rotation = rotation,
                            scale = scale
                        )
                    } else textBox
                }
            )
        }
    }

    private fun onTextBoxFontSizeChange(textBoxId: Int, fontSize: Float) {
        _state.update {
            it.copy(
                memeTexts = it.memeTexts.map { textBox ->
                    if (textBox.id == textBoxId) {
                        textBox.copy(fontSize = fontSize)
                    } else textBox
                }
            )
        }
    }

    private fun updateTemplateSize(size: IntSize) {
        _state.update {
            it.copy(templateSize = size)
        }
    }

    private fun selectTextBox(id: Int) {
        selectedTextFontSizeCache = state.value.memeTexts.firstOrNull { it.id == id }?.fontSize
        _state.update {
            it.copy(
                textBoxInteraction = TextBoxInteractionState.Selected(id)
            )
        }
    }

    private fun editTextBox(id: Int) {
        _state.update {
            it.copy(
                textBoxInteraction = TextBoxInteractionState.Editing(id)
            )
        }
    }

    private fun createTextBox() = viewModelScope.launch {
        val currentState = state.value
        val newId = currentState.memeTexts.maxOfOrNull { it.id }?.inc() ?: 1

        // Place new text at center if template size is known, otherwise top-left
        val position = if (currentState.templateSize != IntSize.Zero) {
            Offset(
                x = (currentState.templateSize.width * 0.25f),
                y = (currentState.templateSize.height * 0.25f)
            )
        } else {
            Offset(50f, 50f)
        }

        val newBox = MemeText(
            id = newId,
            text = "TAP TO EDIT", // TODO string resources
            offset = position,
            fontSize = 36f
        )

        _state.update {
            it.copy(
                memeTexts = currentState.memeTexts + newBox,
                textBoxInteraction = TextBoxInteractionState.Selected(newId)
            )
        }
    }

}