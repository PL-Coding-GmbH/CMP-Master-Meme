@file:OptIn(ExperimentalMaterial3Api::class)

package com.plcoding.cmpmastermeme.editmeme.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Share
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
import cmpmastermeme.composeapp.generated.resources.Res
import cmpmastermeme.composeapp.generated.resources.icon_save
import cmpmastermeme.composeapp.generated.resources.save_to_device
import cmpmastermeme.composeapp.generated.resources.save_to_device_desc
import cmpmastermeme.composeapp.generated.resources.share_meme
import cmpmastermeme.composeapp.generated.resources.share_meme_desc
import com.plcoding.cmpmastermeme.core.designsystem.MasterMemeTheme
import com.plcoding.cmpmastermeme.core.designsystem.button
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun SaveMemeContextSheetRoot(
    onSaveClick: () -> Unit,
    onShareClick: () -> Unit,
    onDismiss: () -> Unit,
    sheetState: SheetState,
    modifier: Modifier = Modifier
) {
    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = { onDismiss() },
        sheetState = sheetState,
        contentWindowInsets = { WindowInsets.navigationBars },
        dragHandle = {
            BottomSheetDefaults.DragHandle()
        },
        properties = ModalBottomSheetProperties(
            shouldDismissOnBackPress = true
        )
    ) {
        SaveMemeContextContent(
            onSaveClick,
            onShareClick
        )
    }
}

@Composable
private fun SaveMemeContextContent(
    onSaveClick: () -> Unit,
    onShareClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        ListItem(
            modifier = Modifier.clickable(onClick = onSaveClick),
            leadingContent = {
                Icon(
                    imageVector = vectorResource(Res.drawable.icon_save),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary
                )
            },
            headlineContent = {
                Text(
                    text = stringResource(Res.string.save_to_device),
                    style = MaterialTheme.typography.button.copy(
                        color = MaterialTheme.colorScheme.secondary
                    )
                )
            },
            supportingContent = {
                Text(
                    text = stringResource(Res.string.save_to_device_desc),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.outline
                    )

                )
            },
            colors = ListItemDefaults.colors(
                containerColor = Color.Transparent
            )
        )
        ListItem(
            modifier = Modifier.clickable(onClick = onShareClick),
            leadingContent = {
                Icon(
                    imageVector = Icons.Outlined.Share,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary
                )
            },
            headlineContent = {
                Text(
                    text = stringResource(Res.string.share_meme),
                    style = MaterialTheme.typography.button.copy(
                        color = MaterialTheme.colorScheme.secondary
                    )
                )
            },
            supportingContent = {
                Text(
                    text = stringResource(Res.string.share_meme_desc),
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
}

@Preview
@Composable
private fun Preview() {
    MasterMemeTheme {
        SaveMemeContextContent(
            onSaveClick = {},
            onShareClick = {}
        )
    }
}