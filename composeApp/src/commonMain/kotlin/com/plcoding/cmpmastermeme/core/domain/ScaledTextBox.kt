package com.plcoding.cmpmastermeme.core.domain

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import com.plcoding.cmpmastermeme.editmeme.models.MemeText

/**
 * Contains all calculated values needed to render a text box
 */
data class ScaledTextBox(
    val text: String,
    val scaledOffset: Offset,
    val scaledFontSize: Float,
    val strokeWidth: Float,
    val constraintWidth: Int,
    val textPaddingX: Float,
    val textPaddingY: Float,
    val pivotX: Float,
    val pivotY: Float,
    val rotation: Float,
    val scale: Float,
    val originalBox: MemeText
)

/**
 * Common calculation logic for meme rendering across platforms
 */
class MemeRenderCalculator(
    private val density: Float
) {
    companion object {
        private const val TEXT_PADDING_DP = 8f
        private const val STROKE_WIDTH_DP = 3f
        private const val TEXT_WIDTH_FACTOR = 0.3f
    }

    /**
     * Calculate scale factors from display size to bitmap size
     */
    fun calculateScaleFactors(
        bitmapWidth: Int,
        bitmapHeight: Int,
        displaySize: IntSize
    ): ScaleFactors {
        val scaleX = if (displaySize.width > 0) bitmapWidth.toFloat() / displaySize.width else 1f
        val scaleY = if (displaySize.height > 0) bitmapHeight.toFloat() / displaySize.height else 1f
        val bitmapScale = (scaleX + scaleY) / 2f
        
        return ScaleFactors(scaleX, scaleY, bitmapScale)
    }

    /**
     * Convert all text boxes with proper scaling calculations
     */
    fun calculateScaledTextBoxes(
        textBoxes: List<MemeText>,
        scaleFactors: ScaleFactors,
        displayWidth: Int
    ): List<ScaledTextBox> {
        return textBoxes.map { box ->
            calculateScaledTextBox(box, scaleFactors, displayWidth)
        }
    }

    /**
     * Calculate all rendering parameters for a single text box
     */
    private fun calculateScaledTextBox(
        box: MemeText,
        scaleFactors: ScaleFactors,
        displayWidth: Int
    ): ScaledTextBox {
        val (scaleX, scaleY, bitmapScale) = scaleFactors

        // Scale position
        val scaledOffset = Offset(
            x = box.offset.x * scaleX,
            y = box.offset.y * scaleY
        )

        // Calculate padding in bitmap coordinates
        val textPaddingPx = TEXT_PADDING_DP * density
        val textPaddingBitmapX = textPaddingPx * scaleX
        val textPaddingBitmapY = textPaddingPx * scaleY

        // Calculate font size (scaled by bitmap scale, before box.scale is applied)
        val scaledFontSize = box.fontSize * bitmapScale * density

        // Calculate stroke width
        val strokeWidth = STROKE_WIDTH_DP * density * scaleX

        // Calculate constraint width (matching editor logic)
        val paddingDp = TEXT_PADDING_DP * 2  // 8dp * 2 = 16dp
        val paddingPx = paddingDp * density
        val constraintWidth = ((displayWidth * TEXT_WIDTH_FACTOR / box.scale) * density * scaleX - paddingPx * scaleX)
            .toInt()
            .coerceAtLeast(1)

        return ScaledTextBox(
            text = box.text,
            scaledOffset = scaledOffset,
            scaledFontSize = scaledFontSize,
            strokeWidth = strokeWidth,
            constraintWidth = constraintWidth,
            textPaddingX = textPaddingBitmapX,
            textPaddingY = textPaddingBitmapY,
            pivotX = 0f,  // Calculated after text layout
            pivotY = 0f,  // Calculated after text layout
            rotation = box.rotation,
            scale = box.scale,
            originalBox = box
        )
    }

    /**
     * Calculate pivot points based on actual text dimensions
     */
    fun calculatePivotPoints(
        scaledBox: ScaledTextBox,
        actualTextWidth: Float,
        textHeight: Float
    ): ScaledTextBox {
        val outerBoxWidth = actualTextWidth + scaledBox.textPaddingX * 2
        val outerBoxHeight = textHeight + scaledBox.textPaddingY * 2

        val pivotX = scaledBox.scaledOffset.x + outerBoxWidth / 2f
        val pivotY = scaledBox.scaledOffset.y + outerBoxHeight / 2f

        return scaledBox.copy(
            pivotX = pivotX,
            pivotY = pivotY
        )
    }

    /**
     * Get text drawing position (offset by padding)
     */
    fun getTextDrawingPosition(scaledBox: ScaledTextBox): Offset {
        return Offset(
            x = scaledBox.scaledOffset.x + scaledBox.textPaddingX,
            y = scaledBox.scaledOffset.y + scaledBox.textPaddingY
        )
    }
}

data class ScaleFactors(
    val scaleX: Float,
    val scaleY: Float,
    val bitmapScale: Float
)