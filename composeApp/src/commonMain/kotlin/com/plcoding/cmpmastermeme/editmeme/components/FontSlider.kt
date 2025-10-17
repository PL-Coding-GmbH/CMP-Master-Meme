package com.plcoding.cmpmastermeme.editmeme.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.plcoding.cmpmastermeme.core.designsystem.MasterMemeTheme
import com.plcoding.cmpmastermeme.editmeme.MAX_TEXT_FONT_SIZE
import com.plcoding.cmpmastermeme.editmeme.MIN_TEXT_FONT_SIZE
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
fun FontSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    minFontSize: Float = MIN_TEXT_FONT_SIZE,
    maxFontSize: Float = MAX_TEXT_FONT_SIZE,
    sliderColor: Color = MaterialTheme.colorScheme.secondary
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isDragging by interactionSource.collectIsDraggedAsState()

    val outerCircleScale by animateFloatAsState(
        targetValue = if (isDragging) 1f else 0f,
        animationSpec = if (isDragging) {
            spring(
                dampingRatio = 0.4f, // Low value = more bounce
                stiffness = 300f
            )
        } else {
            spring(
                dampingRatio = 0.8f, // high value = smooth shrink
                stiffness = 200f // Lower for slower shrink
            )
        }
    )

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Aa", // I don't see a reason to put this in strings.xml
            style = MaterialTheme.typography.bodySmall,
            color = Color.White
        )

        Slider(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f),
            enabled = enabled,
            valueRange = minFontSize..maxFontSize,
            interactionSource = interactionSource,
            thumb = {
                Canvas(
                    modifier = Modifier.size(32.dp),
                    onDraw = {
                        // Draw animated outer circle
                        if (outerCircleScale > 0f) {
                            drawCircle(
                                color = sliderColor.copy(alpha = 0.3f * outerCircleScale),
                                radius = (size.width / 2) * outerCircleScale
                            )
                        }
                        // Always draw inner circle
                        drawCircle(
                            color = sliderColor,
                            radius = size.width / 4
                        )
                    }
                )
            },
            track = { _ ->
                HorizontalDivider(
                    thickness = 1.5.dp,
                    color = sliderColor
                )
            }
        )

        Text(
            text = "Aa",
            style = MaterialTheme.typography.headlineLarge,
            color = Color.White
        )
    }
}

@Preview
@Composable
private fun Preview() {
    MasterMemeTheme {
        Box(
            Modifier.background(MaterialTheme.colorScheme.surfaceContainerLow)
        ) {
            var value by remember { mutableFloatStateOf(36f) }
            FontSlider(
                value = value,
                onValueChange = { value = it },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}