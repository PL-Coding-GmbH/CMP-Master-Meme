package com.plcoding.cmpmastermeme.editmeme.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cmpmastermeme.composeapp.generated.resources.Res
import cmpmastermeme.composeapp.generated.resources.cancel
import cmpmastermeme.composeapp.generated.resources.leave
import cmpmastermeme.composeapp.generated.resources.leave_editor_message
import cmpmastermeme.composeapp.generated.resources.leave_editor_title
import com.plcoding.cmpmastermeme.core.designsystem.MasterMemeTheme
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun LeaveEditorConfirmationDialog(
    onDismiss: () -> Unit,
    onConfirmLeave: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(Res.string.leave_editor_title),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Text(
                text = stringResource(Res.string.leave_editor_message),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirmLeave
            ) {
                Text(
                    text = stringResource(Res.string.leave),
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(
                    text = stringResource(Res.string.cancel),
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
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