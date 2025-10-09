package com.plcoding.cmpmastermeme.editmeme.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.plcoding.cmpmastermeme.core.designsystem.Impact
import com.plcoding.cmpmastermeme.core.designsystem.MasterMemeTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun OutlinedText(
    text: String,
    fontSize: Float,
    fontFamily: FontFamily = Impact,
    fillColor: Color = Color.White,
    strokeColor: Color = Color.Black,
    strokeWidth: Float = 8f,
    textAlign: TextAlign = TextAlign.Center,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()

    val textStyle = TextStyle(
        fontSize = fontSize.sp,
        fontFamily = fontFamily,
        textAlign = textAlign
    )

    val textLayoutResult = textMeasurer.measure(
        text = AnnotatedString(text),
        style = textStyle
    )

    val density = LocalDensity.current

    Canvas(
        modifier = modifier
            .size(
                width = with(density) { textLayoutResult.size.width.toDp() },
                height = with(density) { textLayoutResult.size.height.toDp() }
            )
    ) {
        // Draw stroke (outline)
        drawText(
            textLayoutResult = textLayoutResult,
            color = strokeColor,
            drawStyle = Stroke(width = strokeWidth)
        )

        // Draw fill on top
        drawText(
            textLayoutResult = textLayoutResult,
            color = fillColor,
            drawStyle = Fill
        )
    }
}

@Preview
@Composable
private fun Preview() {
    MasterMemeTheme {
        OutlinedText(
            text = "TAP TWICE TO EDIT",
            fontSize = 36f,
            fontFamily = Impact,
            fillColor = Color.White,
            strokeColor = Color.Black,
            modifier = Modifier.background(Color.Yellow)
        )
    }
}