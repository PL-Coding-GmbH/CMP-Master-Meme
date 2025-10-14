package com.plcoding.cmpmastermeme.editmeme.models

import androidx.compose.ui.geometry.Offset

data class MemeText(
    val id: Int,
    val text: String,
    val fontSize: Float = 36f,
    val offset: Offset = Offset.Zero,
)