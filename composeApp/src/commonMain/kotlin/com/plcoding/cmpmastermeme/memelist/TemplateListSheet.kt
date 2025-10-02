@file:OptIn(ExperimentalMaterial3Api::class)

package com.plcoding.cmpmastermeme.memelist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TemplateListSheetRoot(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onMemeTemplateSelected: () -> Unit,
    modifier: Modifier = Modifier,
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
        Box {
            TemplateListContent(onMemeTemplateSelected)
        }
    }
}

@Composable
private fun TemplateListContent(
    onMemeTemplateSelected: () -> Unit,
) {
    Text(text = " Template List Content ", fontSize = 39.sp, modifier = Modifier.fillMaxSize().padding(14.dp).clickable {
        onMemeTemplateSelected()
    })
}
