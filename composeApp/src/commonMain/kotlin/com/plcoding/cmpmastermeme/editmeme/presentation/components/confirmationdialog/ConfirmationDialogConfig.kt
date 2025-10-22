package com.plcoding.cmpmastermeme.editmeme.presentation.components.confirmationdialog

import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.resources.StringResource

data class ConfirmationDialogConfig(
    val titleResource: StringResource,
    val messageResource: StringResource,
    val confirmButtonResource: StringResource,
    val cancelButtonResource: StringResource,
    val confirmButtonColor: Color? = null
)