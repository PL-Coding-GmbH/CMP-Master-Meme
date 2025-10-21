package com.plcoding.cmpmastermeme.editmeme.models

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize

data class EditMemeState(
    val templateSize: IntSize = IntSize.Zero,
    val memeElements: List<MemeElement> = emptyList(),
    val textBoxInteraction: TextBoxInteractionState = TextBoxInteractionState.None,
    val isFinalisingMeme: Boolean = false,
    val isLeavingWithoutSaving: Boolean = false
)

sealed class MemeElement(
    open val id: Int,
    open val transform: Transform = Transform()
) {
    data class Text(
        override val id: Int,
        val text: String,
        val fontSize: Float = 36f,
        override val transform: Transform = Transform()
    ): MemeElement(id, transform)

    class Image(
        override val id: Int,
        val bytes: ByteArray,
        override val transform: Transform = Transform()
    ): MemeElement(id, transform)
}

data class Transform(
    val offset: Offset = Offset.Zero,
    val rotation: Float = 0f,
    val scale: Float = 1f
)

sealed class TextBoxInteractionState {
    data object None : TextBoxInteractionState()
    data class Selected(val textBoxId: Int) : TextBoxInteractionState()
    data class Editing(val textBoxId: Int) : TextBoxInteractionState()

    val targetedTextBoxId: Int?
        get() = when (this) {
            None -> null
            is Editing -> textBoxId
            is Selected -> textBoxId
        }
}
