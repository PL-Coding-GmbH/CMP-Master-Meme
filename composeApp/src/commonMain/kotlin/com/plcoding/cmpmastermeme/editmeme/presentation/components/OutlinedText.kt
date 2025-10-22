package com.plcoding.cmpmastermeme.editmeme.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.plcoding.cmpmastermeme.core.designsystem.Fonts
import com.plcoding.cmpmastermeme.core.designsystem.MasterMemeTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun OutlinedText(
    text: String,
    fontSize: Float,
    modifier: Modifier = Modifier,
    fontFamily: FontFamily = Fonts.Impact,
    fillColor: Color = Color.White,
    strokeColor: Color = Color.Black,
    strokeWidth: Dp = 3.dp,
    textAlign: TextAlign = TextAlign.Center,
    maxWidth: Dp? = null,
    maxHeight: Dp? = null
) {
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current

    val textStyle = TextStyle(
        fontSize = fontSize.sp,
        fontFamily = fontFamily,
        textAlign = textAlign
    )

    val constraints = when {
        maxWidth != null && maxHeight != null -> with(density) {
            Constraints(
                maxWidth = maxWidth.roundToPx(),
                maxHeight = maxHeight.roundToPx()
            )
        }
        maxWidth != null -> with(density) {
            Constraints(
                maxWidth = maxWidth.roundToPx()
            )
        }
        maxHeight != null -> with(density) {
            Constraints(
                maxHeight = maxHeight.roundToPx()
            )
        }
        else -> null
    }

    val textLayoutResult = textMeasurer.measure(
        text = AnnotatedString(text),
        style = textStyle,
        constraints = constraints ?: Constraints()
    )

    Canvas(
        modifier = modifier.background(Color.Transparent)
            .size(
                width = with(density) { textLayoutResult.size.width.toDp() },
                height = with(density) { textLayoutResult.size.height.toDp() }
            )
    ) {
        // Draw stroke (outline) with rounded joins for better appearance
        drawText(
            textLayoutResult = textLayoutResult,
            color = strokeColor,
            drawStyle = Stroke(
                width = strokeWidth.toPx(),
                miter = 10f,
                join = StrokeJoin.Round
            )
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
            fontFamily = Fonts.Impact,
            fillColor = Color.White,
            strokeColor = Color.Black,
            modifier = Modifier
        )
    }
}