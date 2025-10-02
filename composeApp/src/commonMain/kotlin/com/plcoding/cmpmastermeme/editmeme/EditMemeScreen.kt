@file:OptIn(ExperimentalMaterial3Api::class)

package com.plcoding.cmpmastermeme.editmeme

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import com.plcoding.cmpmastermeme.memelist.TemplateListSheetRoot

@Composable
fun EditMemeScreenRoot(
    onGoBackClick: () -> Unit,
) {
    EditMemeScreen(onGoBackClick)
}

@Composable
private fun EditMemeScreen(
    onGoBackClick: () -> Unit,
) {
    var isSheetVisible by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("Edit Meme", fontSize = 39.sp, modifier = Modifier.clickable { isSheetVisible = true })
        Button(onClick = { onGoBackClick() }) {
            Text("Go back")
        }
    }
    if (isSheetVisible) {
        SaveMemeContextSheetRoot(
            onSaveClick = {},
            onShareClick = {},
            onDismiss = { isSheetVisible = false },
            sheetState = sheetState,
        )
    }
}