package com.plcoding.cmpmastermeme.editmeme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plcoding.cmpmastermeme.core.domain.Meme
import com.plcoding.cmpmastermeme.core.domain.MemeDataSource
import com.plcoding.cmpmastermeme.core.domain.MemeExporter
import com.plcoding.cmpmastermeme.core.domain.MemeTemplate
import com.plcoding.cmpmastermeme.core.domain.SendableFileManager
import com.plcoding.cmpmastermeme.editmeme.models.EditMemeAction
import com.plcoding.cmpmastermeme.editmeme.models.EditMemeEvent
import com.plcoding.cmpmastermeme.editmeme.models.EditMemeState
import com.plcoding.cmpmastermeme.editmeme.models.MemeText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.consumeAsFlow
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
    private val memeDataSource: MemeDataSource
) : ViewModel(), KoinComponent {

    private val _state = MutableStateFlow(EditMemeState())
    val state = _state.asStateFlow()

    private val eventChannel = Channel<EditMemeEvent>()
    val events = eventChannel.consumeAsFlow()

    fun onAction(action: EditMemeAction) {
        when (action) {
            EditMemeAction.OnAddNewMemeTextClick -> createTextBox()
            is EditMemeAction.OnSaveMemeClick -> saveMeme(action.memeTemplate)
            is EditMemeAction.OnDeleteMemeText -> removeTextBox(action.id)
            is EditMemeAction.OnEditMemeText -> editTextBox(action.id)
            is EditMemeAction.OnSelectMemeText -> selectTextBox(action.id)
            is EditMemeAction.OnMemeTextChange -> onTextBoxTextChange(
                textBoxId = action.id,
                text = action.text
            )

            is EditMemeAction.OnMemeTextPositionChange -> onTextBoxPositionChange(
                textBoxId = action.id,
                x = action.x,
                y = action.y
            )

            is EditMemeAction.OnContainerSizeChanged -> updateTemplateSize(action.size)
            EditMemeAction.OnCompleteEditingClick -> toggleIsFinalisingMeme(isFinalising = true)
            EditMemeAction.OnContinueEditing -> toggleIsFinalisingMeme(isFinalising = false)
            is EditMemeAction.OnShareMemeClick -> shareMeme(action.memeTemplate)

            /* Handled in UI */
            EditMemeAction.OnGoBackClick -> Unit
        }
    }

    private fun shareMeme(memeTemplate: MemeTemplate) = viewModelScope.launch {
        _state.update {
            it.copy(isFinalisingMeme = false)
        }
        memeExporter.exportMeme(
            backgroundImageBytes = memeTemplate.drawableResource.getBytes(),
            textBoxes = state.value.memeTexts,
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

    private fun saveMeme(memeTemplate: MemeTemplate) = viewModelScope.launch {
        _state.update {
            it.copy(isFinalisingMeme = false)
        }

        memeExporter.exportMeme(
            backgroundImageBytes = memeTemplate.drawableResource.getBytes(),
            textBoxes = state.value.memeTexts,
            canvasSize = state.value.templateSize,
            saveStrategy = get(named("private_dir"))
        )
            .onSuccess { uri ->
                memeDataSource.save(Meme(imageUri = uri))
                eventChannel.send(EditMemeEvent.SavedMeme)
            }
            .onFailure {
                // TODO show toast
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

    private fun onTextBoxPositionChange(textBoxId: Int, x: Float, y: Float) {
        _state.update {
            it.copy(
                memeTexts = it.memeTexts.map { textBox ->
                    if (textBox.id == textBoxId) {
                        textBox.copy(offset = Offset(x, y))
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

        // Place new text at top-left corner
        val position = Offset(0f, 0f)

        val newBox = MemeText(
            id = newId,
            text = "Tap to edit",
            offset = position,
        )

        _state.update {
            it.copy(
                memeTexts = currentState.memeTexts + newBox
            )
        }
    }

}