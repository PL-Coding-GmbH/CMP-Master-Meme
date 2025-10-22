package com.plcoding.cmpmastermeme.editmeme.presentation.models

import androidx.compose.ui.geometry.Offset

data class MemeText(
    val id: Int,
    val text: String,
    val fontSize: Float = 36f,
    val offset: Offset = Offset.Zero,
    val rotation: Float = 0f,
    val scale: Float = 1f
)