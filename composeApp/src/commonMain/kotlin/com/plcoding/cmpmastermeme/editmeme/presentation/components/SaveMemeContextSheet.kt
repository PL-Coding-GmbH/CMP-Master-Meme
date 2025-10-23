@file:OptIn(ExperimentalMaterial3Api::class)

package com.plcoding.cmpmastermeme.editmeme.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.plcoding.cmpmastermeme.core.designsystem.MasterMemeTheme
import com.plcoding.cmpmastermeme.core.designsystem.button
import com.plcoding.cmpmastermeme.editmeme.presentation.models.MemeUiAction
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
private fun SaveMemeContextContent(
    availableActions: List<MemeUiAction>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        availableActions.forEach { action ->
            ActionListItem(
                action = action,
                onClick = { action.onClick() }
            )
        }
    }
}

@Composable
private fun ActionListItem(
    action: MemeUiAction,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ListItem(
        modifier = modifier.clickable(onClick = onClick),
        leadingContent = {
            when {
                action.icon != null -> Icon(
                    imageVector = action.icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary
                )
                action.vectorRes != null -> Icon(
                    imageVector = vectorResource(action.vectorRes),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
        },
        headlineContent = {
            Text(
                text = stringResource(action.titleRes),
                style = MaterialTheme.typography.button.copy(
                    color = MaterialTheme.colorScheme.secondary
                )
            )
        },
        supportingContent = {
            Text(
                text = stringResource(action.descriptionRes),
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.outline
                )
            )
        },
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent
        )
    )
}

@Preview
@Composable
private fun Preview() {
    MasterMemeTheme {
        SaveMemeContextContent(
            availableActions = listOf(
                MemeUiAction.Share(onClick = {}),
            )
        )
    }
}