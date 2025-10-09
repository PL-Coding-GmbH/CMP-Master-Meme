package com.plcoding.cmpmastermeme.editmeme.models

import androidx.compose.ui.geometry.Offset

data class TextBox(
    val id: Int,
    val text: String,
    val fontSize: Float = 36f,
    val position: Offset = Offset.Zero,
)