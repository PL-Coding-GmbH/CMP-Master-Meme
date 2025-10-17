package com.plcoding.cmpmastermeme.editmeme.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.plcoding.cmpmastermeme.core.designsystem.Impact
import com.plcoding.cmpmastermeme.core.designsystem.MasterMemeTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Problem:
 *   BasicTextField doesn't let us customise HOW text gets rendered.
 *   That is why we just use a Canvas in [OutlinedText], but doing so
 *   for the editable version is a bit trickier when you consider that
 *   we need to add: cursor rendering, text selection, keyboard inputs,
 *   focus management, IME support, text editing logic (copy, paste, delete, etc.).
 *
 *   We'd essentially be recreating the BasicTextField 😅
 *
 * Solution: Two Layers
 *   Bottom layer (OutlinedText) - uses Canvas to draw same text twice.
 *     - The black stroke outline
 *     - The white fill
 *   Top Layer (BasicTextField) - Transparent text for user input
 *
 * Both have same text measurements so that the top layer cursor placement matches up
 * perfectly with the visible bottom layer.
 */
@Composable
fun OutlinedTextField(
    text: String,
    fontSize: Float,
    fontFamily: FontFamily,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    fillColor: Color = Color.White,
    strokeColor: Color = Color.Black,
    strokeWidth: Float = 8f,
    textAlign: TextAlign = TextAlign.Center,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    var textFieldValue by remember {
        mutableStateOf(
            TextFieldValue(text)
        )
    }
    
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    
    val textStyle = TextStyle(
        fontSize = fontSize.sp,
        fontFamily = fontFamily,
        textAlign = textAlign
    )
    
    val textLayoutResult = textMeasurer.measure(
        text = AnnotatedString(textFieldValue.text.ifEmpty { " " }), // Ensure minimum size
        style = textStyle
    )

    val textSize = with(density) {
        DpSize(
            width = textLayoutResult.size.width.toDp(),
            height = textLayoutResult.size.height.toDp()
        )
    }

    BasicTextField(
        value = textFieldValue,
        onValueChange = { newValue ->
            textFieldValue = newValue
            onTextChange(newValue.text)
        },
        textStyle = textStyle.copy(color = Color.Transparent),
        cursorBrush = SolidColor(Color.White),
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        modifier = modifier.size(textSize)
    )
    { innerTextField ->
        Box {
            OutlinedText(
                text = textFieldValue.text,
                fontSize = fontSize,
                fontFamily = fontFamily,
                fillColor = fillColor,
                strokeColor = strokeColor,
                strokeWidth = strokeWidth,
                textAlign = textAlign
            )
            // Invisible text field for cursor
            innerTextField()
        }
    }
}

@Preview
@Composable 
private fun Preview() {
    MasterMemeTheme {
        var editableText by remember { mutableStateOf("EDITABLE TEXT") }
        OutlinedTextField(
            text = editableText,
            fontSize = 36f,
            fontFamily = Impact,
            onTextChange = { editableText = it },
            modifier = Modifier.padding(8.dp)
        )
    }
}