package com.plcoding.cmpmastermeme.editmeme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.Dp
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
    modifier: Modifier = Modifier,
    maxWidth: Dp,
    maxHeight: Dp
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

    LaunchedEffect(isSelected, memeText.id) {
        if (!isSelected) {
            focusManager.clearFocus()
        }
    }

    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .sizeIn(maxWidth = maxWidth, maxHeight = maxHeight)
                .border(
                    width = 2.dp,
                    color = if (isSelected || isEditing) Color.White else Color.Transparent,
                    shape = RoundedCornerShape(4.dp)
                )
                .background(
                    color = if (isEditing) {
                        Color.Black.copy(alpha = 0.1f)
                    } else Color.Transparent,
                    shape = RoundedCornerShape(4.dp)
                )
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { onClick() },
                        onDoubleTap = { onDoubleClick() }
                    )
                }
        ) {
            val textPadding = 8.dp
            if (isEditing) {
                OutlinedTextField(
                    text = memeText.text,
                    fontSize = memeText.fontSize,
                    fontFamily = Impact,
                    onTextChange = onTextInputChange,
                    // Accounting here for padding
                    maxWidth = maxWidth - (textPadding * 2),
                    maxHeight = maxHeight - (textPadding * 2),
                    modifier = Modifier
                        .focusRequester(editableMemeText)
                        .padding(textPadding)
                )
            } else {
                OutlinedText(
                    text = memeText.text,
                    fontSize = memeText.fontSize,
                    // Accounting here for padding
                    maxWidth = maxWidth - (textPadding * 2),
                    maxHeight = maxHeight - (textPadding * 2),
                    modifier = Modifier.padding(textPadding)
                )
            }
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
    var placement by remember {
        mutableStateOf(
            Offset.Zero
        )
    }
    var memeText by remember(text, placement) {
        mutableStateOf(
            MemeText(
                id = 1,
                text = text,
                offset = placement
            )
        )
    }

    MasterMemeTheme {
        Box(
            modifier = Modifier
                .background(Color.LightGray)
                .size(400.dp),
            contentAlignment = Alignment.Center,
        ) {
            MemeTextBox(
                memeText = memeText,
                isSelected = isSelected,
                isEditing = isEditing,
                onTextInputChange = { text = it },
                onDelete = {
                    isSelected = false
                    isEditing = false
                },
                onClick = { isSelected = true; isEditing = false },
                onDoubleClick = { isEditing = true; isSelected = false },
                maxWidth = 250.dp,
                maxHeight = 250.dp
            )
        }
    }
}