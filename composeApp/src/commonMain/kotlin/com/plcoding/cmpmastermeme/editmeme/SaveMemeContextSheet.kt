@file:OptIn(ExperimentalMaterial3Api::class)

package com.plcoding.cmpmastermeme.editmeme

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

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
        contentWindowInsets = { WindowInsets(0) },
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
) {
    Column {
        Button(
            onClick = onSaveClick
        ) { Text("Save") }
        Button(
            onClick = onShareClick
        ) { Text("Share") }
    }
}