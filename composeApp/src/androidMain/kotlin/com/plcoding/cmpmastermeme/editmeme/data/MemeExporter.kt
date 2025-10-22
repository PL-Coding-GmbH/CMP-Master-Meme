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

    actual suspend fun exportMeme(
        backgroundImageBytes: ByteArray,
        textBoxes: List<MemeText>,
        canvasSize: IntSize,
        fileName: String,
        saveStrategy: SaveStrategy,
    ) = withContext(Dispatchers.IO) {
        try {
            val bitmap =
                BitmapFactory.decodeByteArray(backgroundImageBytes, 0, backgroundImageBytes.size)
            val outputBitmap = renderMeme(bitmap, textBoxes, canvasSize)
            val filePath = saveStrategy.getFilePath(fileName)
            val file = File(filePath)
            FileOutputStream(file).use { out ->
                outputBitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            bitmap.recycle()
            outputBitmap.recycle()
            Result.success(file.absolutePath)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Result.failure(e)
        }
    }

    private fun renderMeme(
        background: Bitmap,
        textBoxes: List<MemeText>,
        displaySize: IntSize
    ): Bitmap {
        val output = background.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(output)

        // Use common calculator
        val scaleFactors = calculator.calculateScaleFactors(
            background.width,
            background.height,
            displaySize
        )

        val scaledBoxes = calculator.calculateScaledTextBoxes(
            textBoxes,
            scaleFactors,
            displaySize.width
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
            textAlign = Paint.Align.LEFT
        }

        val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            textSize = scaledBox.scaledFontSize
            typeface = impactTypeface
            color = Color.WHITE
            textAlign = Paint.Align.LEFT
        }

        val strokeLayout = StaticLayout.Builder.obtain(
            scaledBox.text,
            0,
            scaledBox.text.length,
            TextPaint(strokePaint),
            scaledBox.constraintWidth
        )
            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .setIncludePad(false)
            .build()

        val fillLayout = StaticLayout.Builder.obtain(
            scaledBox.text,
            0,
            scaledBox.text.length,
            TextPaint(fillPaint),
            scaledBox.constraintWidth
        )
            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .setIncludePad(false)
            .build()

        // Get actual text dimensions
        val actualTextWidth = (0 until strokeLayout.lineCount).maxOfOrNull { line ->
            strokeLayout.getLineWidth(line)
        } ?: strokeLayout.width.toFloat()
        val textHeight = strokeLayout.height.toFloat()

        val boxWithPivots = calculator.calculatePivotPoints(
            scaledBox,
            actualTextWidth,
            textHeight
        )

        val textPosition = calculator.getTextDrawingPosition(boxWithPivots)

        // Apply transformations
        canvas.withScale(boxWithPivots.scale, boxWithPivots.scale, boxWithPivots.pivotX, boxWithPivots.pivotY) {
            canvas.withRotation(boxWithPivots.rotation, boxWithPivots.pivotX, boxWithPivots.pivotY) {
                canvas.withTranslation(textPosition.x, textPosition.y) {
                    strokeLayout.draw(this)
                    fillLayout.draw(this)
                }
            }
        }
    }
}