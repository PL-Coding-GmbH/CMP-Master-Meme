package com.plcoding.cmpmastermeme.editmeme.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.compose.ui.unit.IntSize
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.withRotation
import androidx.core.graphics.withScale
import androidx.core.graphics.withTranslation
import com.plcoding.cmpmastermeme.R
import com.plcoding.cmpmastermeme.editmeme.domain.SaveToStorageStrategy
import com.plcoding.cmpmastermeme.editmeme.presentation.models.MemeText
import com.plcoding.cmpmastermeme.editmeme.presentation.util.MemeRenderCalculator
import com.plcoding.cmpmastermeme.editmeme.presentation.util.ScaledTextBox
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

actual class MemeExporter(
    private val context: Context
) {
    private val calculator = MemeRenderCalculator(
        density = context.resources.displayMetrics.density
    )

    // Temporarily saves the file and returns a shareable file path
    actual suspend fun exportMeme(
        backgroundImageBytes: ByteArray,
        textBoxes: List<MemeText>,
        canvasSize: IntSize,
        fileName: String,
        saveStrategy: SaveToStorageStrategy,
    ) = withContext(Dispatchers.IO) {
        var bitmap: Bitmap? = null
        var outputBitmap: Bitmap? = null
        try {
            bitmap =
                BitmapFactory.decodeByteArray(backgroundImageBytes, 0, backgroundImageBytes.size)
            outputBitmap = renderMeme(bitmap, textBoxes, canvasSize)
            val filePath = saveStrategy.getFilePath(fileName)
            val file = File(filePath)
            FileOutputStream(file).use { out ->
                outputBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }
            bitmap.recycle()
            outputBitmap.recycle()
            Result.success(file.absolutePath)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Result.failure(e)
        } finally {
            bitmap?.recycle()
            outputBitmap?.recycle()
        }
    }

    private fun renderMeme(
        background: Bitmap,
        textBoxes: List<MemeText>,
        displaySize: IntSize
    ): Bitmap {
        val output = background.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(output)

        val scaleFactors = calculator.calculateScaleFactors(
            bitmapWidth = background.width,
            bitmapHeight = background.height,
            displaySize = displaySize
        )

        val scaledBoxes = calculator.calculateScaledTextBoxes(
            textBoxes = textBoxes,
            scaleFactors = scaleFactors,
            templateSize = displaySize
        )

        scaledBoxes.forEach { scaledBox ->
            drawText(canvas, scaledBox)
        }

        return output
    }


    private fun drawText(
        canvas: Canvas,
        scaledBox: ScaledTextBox
    ) {
        val impactTypeface = ResourcesCompat.getFont(context, R.font.impact) ?: Typeface.DEFAULT_BOLD

        val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = scaledBox.strokeWidth
            textSize = scaledBox.scaledFontSize
            typeface = impactTypeface
            color = Color.BLACK
        }

        val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            textSize = scaledBox.scaledFontSize
            typeface = impactTypeface
            color = Color.WHITE
        }

        val strokeLayout = StaticLayout.Builder.obtain(
            scaledBox.text,
            0,
            scaledBox.text.length,
            TextPaint(strokePaint),
            scaledBox.constraintWidth
        )
            .setAlignment(Layout.Alignment.ALIGN_CENTER)
            .setIncludePad(false)
            .build()

        val fillLayout = StaticLayout.Builder.obtain(
            scaledBox.text,
            0,
            scaledBox.text.length,
            TextPaint(fillPaint),
            scaledBox.constraintWidth
        )
            .setAlignment(Layout.Alignment.ALIGN_CENTER)
            .setIncludePad(false)
            .build()

        val textHeight = strokeLayout.height.toFloat()

        // Calculate box dimensions (drawing rectangle including padding)
        val boxWidth = scaledBox.constraintWidth + scaledBox.textPaddingX * 2
        val boxHeight = textHeight + scaledBox.textPaddingY * 2

        // Calculate center of the box (THIS is the pivot)
        val centerX = scaledBox.scaledOffset.x + boxWidth / 2f
        val centerY = scaledBox.scaledOffset.y + boxHeight / 2f

        canvas.withTranslation(centerX, centerY) {

            // Step 1: Translate to center
            // Step 2: Apply transformations (now around center)
            scale(scaledBox.scale, scaledBox.scale)
            rotate(scaledBox.rotation)

            // Step 3: Translate back to drawing position (relative to center)
            translate(
                -boxWidth / 2f + scaledBox.textPaddingX,
                -boxHeight / 2f + scaledBox.textPaddingY
            )

            // Step 4: Draw text at origin in this transformed space
            strokeLayout.draw(this)
            fillLayout.draw(this)

        }
    }
}