package com.plcoding.cmpmastermeme.editmeme.presentation.components.confirmationdialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cmpmastermeme.composeapp.generated.resources.Res
import cmpmastermeme.composeapp.generated.resources.cancel
import cmpmastermeme.composeapp.generated.resources.delete_meme
import cmpmastermeme.composeapp.generated.resources.delete_meme_message
import cmpmastermeme.composeapp.generated.resources.delete_meme_title
import com.plcoding.cmpmastermeme.core.designsystem.MasterMemeTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun DeleteMemeConfirmationDialog(
    onDismiss: () -> Unit,
    onConfirmDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    ConfirmationDialog(
        config = ConfirmationDialogConfig(
            titleResource = Res.string.delete_meme_title,
            messageResource = Res.string.delete_meme_message,
            confirmButtonResource = Res.string.delete_meme,
            cancelButtonResource = Res.string.cancel
        ),
        onConfirm = onConfirmDelete,
        onDismiss = onDismiss,
        modifier = modifier
    )
}

@Preview
@Composable
private fun Preview() {
    MasterMemeTheme {
        DeleteMemeConfirmationDialog(
            onDismiss = {},
            onConfirmDelete = {}
        )
    }
}