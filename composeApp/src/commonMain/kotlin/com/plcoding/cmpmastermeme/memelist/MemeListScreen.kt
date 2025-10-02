@file:OptIn(ExperimentalMaterial3Api::class)

package com.plcoding.cmpmastermeme.memelist

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.plcoding.cmpmastermeme.core.domain.MemeTemplate

@Composable
fun MemeListScreenRoot(
    onMemeTemplateSelected: (MemeTemplate) -> Unit,
) {
    MemeListScreen(onMemeTemplateSelected)
}

@Composable
private fun MemeListScreen(
    onMemeTemplateSelected: (MemeTemplate) -> Unit
) {
    var isSheetVisible by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "MemeList", fontSize = 38.sp)

        Button(
            onClick = { isSheetVisible = true }
        ) {
            Text("Create Meme")
        }

        if (isSheetVisible) {
            TemplateListSheetRoot(
                sheetState = sheetState,
                onMemeTemplateSelected = onMemeTemplateSelected,
                onDismiss = {
                    isSheetVisible = false
                }
            )
        }
    }
}