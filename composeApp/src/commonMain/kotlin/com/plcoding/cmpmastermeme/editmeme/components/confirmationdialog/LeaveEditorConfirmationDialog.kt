package com.plcoding.cmpmastermeme.editmeme.components.confirmationdialog

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cmpmastermeme.composeapp.generated.resources.Res
import cmpmastermeme.composeapp.generated.resources.cancel
import cmpmastermeme.composeapp.generated.resources.leave
import cmpmastermeme.composeapp.generated.resources.leave_editor_message
import cmpmastermeme.composeapp.generated.resources.leave_editor_title
import com.plcoding.cmpmastermeme.core.designsystem.MasterMemeTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun LeaveEditorConfirmationDialog(
    onDismiss: () -> Unit,
    onConfirmLeave: () -> Unit,
    modifier: Modifier = Modifier
) {
    ConfirmationDialog(
        config = ConfirmationDialogConfig(
            titleResource = Res.string.leave_editor_title,
            messageResource = Res.string.leave_editor_message,
            confirmButtonResource = Res.string.leave,
            cancelButtonResource = Res.string.cancel,
            confirmButtonColor = MaterialTheme.colorScheme.secondary
        ),
        onConfirm = onConfirmLeave,
        onDismiss = onDismiss,
        modifier = modifier
    )
}

@Preview
@Composable
private fun Preview() {
    MasterMemeTheme {
        LeaveEditorConfirmationDialog(
            onDismiss = {},
            onConfirmLeave = {}
        )
    }
}