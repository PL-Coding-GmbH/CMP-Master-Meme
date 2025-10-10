package com.plcoding.cmpmastermeme.editmeme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.plcoding.cmpmastermeme.core.designsystem.Impact
import com.plcoding.cmpmastermeme.core.designsystem.MasterMemeTheme
import com.plcoding.cmpmastermeme.editmeme.models.MemeText
import kotlinx.coroutines.delay
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * A complete meme text component that handles:
 * - Visual state management (selected/editing modes)
 * - User interactions (selection, deletion)
 * - Container styling (borders, backgrounds)
 * - Focus management for editing
 *
 * Uses OutlinedText for display and OutlinedTextField for editing
 * to achieve the white-text-with-black-outline meme style.
 */
@Composable
fun MemeTextBox(
    memeText: MemeText,
    isSelected: Boolean,
    isEditing: Boolean,
    onClick: () -> Unit,
    onDoubleClick: () -> Unit,
    onTextInputChange: (String) -> Unit,
    onDelete: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val editableMemeText = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(isEditing) {
        if (isEditing) {
            editableMemeText.requestFocus()
            delay(100)
            keyboardController?.show()
        }
    }

    LaunchedEffect(isSelected) {
        if (!isSelected) {
            focusManager.clearFocus()
        }
    }

    Box(
        modifier = modifier
            .border(
                width = 1.dp,
                color = if (isSelected || isEditing) Color.White else Color.Transparent,
                shape = RoundedCornerShape(4.dp)
            )
            .background(
                color = if (false) Color.LightGray.copy(alpha = 0.5f)
                else Color.Transparent,
                shape = RoundedCornerShape(4.dp)
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onClick() },
                    onDoubleTap = { onDoubleClick() }
                )
            }
    ) {
        if (isEditing) {
            OutlinedTextField(
                text = memeText.text,
                fontSize = memeText.fontSize,
                fontFamily = Impact,
                onTextChange = onTextInputChange,
                modifier = Modifier
                    .focusRequester(editableMemeText)
                    .padding(4.dp)
            )
        } else {
            OutlinedText(
                text = memeText.text,
                fontSize = memeText.fontSize,
                modifier = Modifier.padding(4.dp)
            )
        }
        if (isSelected || isEditing) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 12.dp, y = (-12).dp)
                    .clip(CircleShape)
                    .background(Color(0xFFB3261E))
                    .clickable { onDelete() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    var isSelected by remember { mutableStateOf(true) }
    var isEditing by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf("TAP TO SELECT") }

    MasterMemeTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Gray)
                .padding(48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            MemeTextBox(
                memeText = MemeText(
                    id = 1,
                    text = text
                ),
                isSelected = isSelected,
                isEditing = isEditing,
                onTextInputChange = { text = it },
                onDelete = {
                    isSelected = false
                    isEditing = false
                },
                onClick = { isSelected = true; isEditing = false },
                onDoubleClick = { isEditing = true; isSelected = false }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    isSelected = false
                    isEditing = false
                }
            ) {
                Text("Clear Focus")
            }
        }
    }
}