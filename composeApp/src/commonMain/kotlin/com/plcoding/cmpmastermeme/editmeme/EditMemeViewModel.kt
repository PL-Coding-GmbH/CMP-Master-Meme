package com.plcoding.cmpmastermeme.editmeme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plcoding.cmpmastermeme.core.domain.MemeExporter
import com.plcoding.cmpmastermeme.core.domain.MemeTemplate
import com.plcoding.cmpmastermeme.core.domain.PickedImageData
import com.plcoding.cmpmastermeme.core.domain.SendableFileManager
import com.plcoding.cmpmastermeme.editmeme.models.EditMemeAction
import com.plcoding.cmpmastermeme.editmeme.models.EditMemeEvent
import com.plcoding.cmpmastermeme.editmeme.models.EditMemeState
import com.plcoding.cmpmastermeme.editmeme.models.MemeElement
import com.plcoding.cmpmastermeme.editmeme.models.TextBoxInteractionState
import com.plcoding.cmpmastermeme.editmeme.models.Transform
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
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.qualifier.named

class EditMemeViewModel(
    private val memeExporter: MemeExporter,
    private val sendableFileManager: SendableFileManager,
) : ViewModel(), KoinComponent {

    private val _state = MutableStateFlow(EditMemeState())
    val state = _state.asStateFlow()

    private val eventChannel = Channel<EditMemeEvent>()
    val events = eventChannel.receiveAsFlow()

    private var selectedTextFontSizeCache: Float? = null

    fun onAction(action: EditMemeAction) {
        when (action) {
            EditMemeAction.OnAddNewMemeTextClick -> createTextBox()
            is EditMemeAction.OnEditMemeText -> editTextBox(action.id)
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
            EditMemeAction.OnCompleteEditingClick -> toggleIsFinalisingMeme(isFinalising = true)
            EditMemeAction.OnContinueEditing -> toggleIsFinalisingMeme(isFinalising = false)
            is EditMemeAction.OnShareMemeClick -> shareMeme(action.memeTemplate)
            EditMemeAction.OnGoBackClick -> showLeaveConfirmationIfEdited()
            EditMemeAction.OnCancelLeaveWithoutSaving -> toggleLeaveEditorConfirmation(show = false)
            EditMemeAction.OnConfirmLeaveWithoutSaving -> leaveWithoutSaving()
            EditMemeAction.ClearSelectedMemeText -> clearSelectedMemeText()
            is EditMemeAction.OnPickedImage -> addImageToMeme(action.data)
            else -> Unit
        }
    }

    private fun addImageToMeme(data: PickedImageData) {
        _state.update { it.copy(
            memeElements = it.memeElements + MemeElement.Image(
                id = it.memeElements.size + 1,
                bytes = data.bytes,
            )
        ) }
    }

    private fun clearSelectedMemeText() {
        _state.update { it.copy(
            textBoxInteraction = TextBoxInteractionState.None
        ) }
    }

    private fun showLeaveConfirmationIfEdited() = viewModelScope.launch {
        if (state.value.memeElements.isEmpty()) {
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
        _state.update {
            it.copy(isFinalisingMeme = false)
        }
        memeExporter.exportMeme(
            backgroundImageBytes = memeTemplate.drawableResource.getBytes(),
            textBoxes = state.value.memeElements.filterIsInstance<MemeElement.Text>(),
            canvasSize = state.value.templateSize,
            saveStrategy = get(named("cache"))
        )
            .onSuccess {
                sendableFileManager.shareFile(it)
            }
            .onFailure {
                // TODO show failure toast
            }
    }

    private fun toggleIsFinalisingMeme(isFinalising: Boolean) {
        _state.update {
            it.copy(isFinalisingMeme = isFinalising)
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
                memeElements = it.memeElements.map { textBox ->
                    if (textBox.id == textBoxId && textBox is MemeElement.Text) {
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
        updateTransformForElement(
            id = textBoxId,
            transform = Transform(
                offset = offset,
                rotation = rotation,
                scale = scale
            )
        )
    }

    private fun updateTransformForElement(id: Int, transform: Transform) {
        _state.update {
            val currentElements = it.memeElements.toMutableList()
            val indexOfEditedElement = currentElements.indexOfFirst { it.id == id }
            val editedElement = currentElements[indexOfEditedElement]
            currentElements[indexOfEditedElement] = when(editedElement) {
                is MemeElement.Image -> MemeElement.Image(
                    id = editedElement.id,
                    bytes = editedElement.bytes,
                    transform = transform
                )
                is MemeElement.Text -> editedElement.copy(
                    transform = transform
                )
            }
            it.copy(
                memeElements = currentElements.toList()
            )
        }
    }

    private fun updateTemplateSize(size: IntSize) {
        _state.update {
            it.copy(templateSize = size)
        }
    }

    private fun selectTextBox(id: Int) {
        selectedTextFontSizeCache = state.value.memeElements
            .filterIsInstance<MemeElement.Text>()
            .firstOrNull { it.id == id }?.fontSize

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
        val newId = currentState.memeElements.maxOfOrNull { it.id }?.inc() ?: 1

        // Place new text at center if template size is known, otherwise top-left
        val position = if (currentState.templateSize != IntSize.Zero) {
            Offset(
                x = (currentState.templateSize.width * 0.25f),
                y = (currentState.templateSize.height * 0.25f)
            )
        } else {
            Offset(50f, 50f)
        }

        val newBox = MemeElement.Text(
            id = newId,
            text = "TAP TO EDIT", // TODO string resources
            transform = Transform(
                offset = position
            ),
            fontSize = 36f
        )

        _state.update {
            it.copy(
                memeElements = currentState.memeElements + newBox,
                textBoxInteraction = TextBoxInteractionState.Selected(newId)
            )
        }
    }

}