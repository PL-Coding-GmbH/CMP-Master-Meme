package com.plcoding.cmpmastermeme.core.domain

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.TypedValue
import androidx.compose.ui.geometry.Offset
import androidx.core.content.res.ResourcesCompat
import androidx.compose.ui.unit.IntSize
import com.plcoding.cmpmastermeme.R
import com.plcoding.cmpmastermeme.editmeme.models.MemeText
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import androidx.core.graphics.withRotation
import androidx.core.graphics.withScale
import androidx.core.graphics.withTranslation

actual class MemeExporter(
    private val context: Context
) {
    actual suspend fun exportMeme(
        backgroundImageBytes: ByteArray,
        textBoxes: List<MemeText>,
        canvasSize: IntSize,
        fileName: String,
        saveStrategy: SaveToStorageStrategy,
    ) = withContext(Dispatchers.IO) {

        try {
            val bitmap = BitmapFactory.decodeByteArray(backgroundImageBytes, 0, backgroundImageBytes.size)
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

        val bitmapWidth = background.width.toFloat()
        val bitmapHeight = background.height.toFloat()

        val scaleX = if (displaySize.width > 0) bitmapWidth / displaySize.width else 1f
        val scaleY = if (displaySize.height > 0) bitmapHeight / displaySize.height else 1f

        textBoxes.forEach { box ->
            val scaledBox = box.copy(
                offset = Offset(
                    x = box.offset.x * scaleX,
                    y = box.offset.y * scaleY
                ),
            )
            drawText(
                canvas = canvas,
                box = scaledBox,
                scaleX = scaleX,
                scaleY = scaleY,
                displayWidth = displaySize.width,
            )
        }

        return output
    }

    private fun drawText(
        canvas: Canvas,
        box: MemeText,
        scaleX: Float,
        scaleY: Float,
        displayWidth: Int,
    ) {
        val bitmapScale = (scaleX + scaleY) / 2f

        // Convert the 8dp padding from the editor to bitmap pixels
        val textPaddingDp = 8f
        val textPaddingPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            textPaddingDp,
            context.resources.displayMetrics
        )
        val textPaddingBitmapX = textPaddingPx * scaleX
        val textPaddingBitmapY = textPaddingPx * scaleY

        // Font size WITHOUT box.scale - that's applied via canvas scaling
        val textSizePx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            box.fontSize * bitmapScale,
            context.resources.displayMetrics
        )

        val strokeWidthPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            1f * bitmapScale,
            context.resources.displayMetrics
        )

        val impactTypeface = ResourcesCompat.getFont(context, R.font.impact) ?: Typeface.DEFAULT_BOLD

        val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = strokeWidthPx
            textSize = textSizePx
            typeface = impactTypeface
            color = Color.BLACK
            textAlign = Paint.Align.LEFT
        }

        val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            textSize = textSizePx
            typeface = impactTypeface
            color = Color.WHITE
            textAlign = Paint.Align.LEFT
        }

        // Match editor constraint
        // The editor uses (displayWidth * 0.3f / zoom).dp which incorrectly treats pixels as dp
        // This gets multiplied by density during layout, so we must account for it here
        val density = context.resources.displayMetrics.density
        val constraintWidthBitmap = ((displayWidth * 0.3f / box.scale) * density * scaleX).toInt()
            .coerceAtLeast(1)

        // Use StaticLayout to handle text wrapping
        val strokeLayout = android.text.StaticLayout.Builder.obtain(
            box.text,
            0,
            box.text.length,
            android.text.TextPaint(strokePaint),
            constraintWidthBitmap
        )
            .setAlignment(android.text.Layout.Alignment.ALIGN_CENTER)
            .setIncludePad(false)
            .build()

        val fillLayout = android.text.StaticLayout.Builder.obtain(
            box.text,
            0,
            box.text.length,
            android.text.TextPaint(fillPaint),
            constraintWidthBitmap
        )
            .setAlignment(android.text.Layout.Alignment.ALIGN_CENTER)
            .setIncludePad(false)
            .build()

        // Get text layout dimensions
        val textWidth = strokeLayout.width.toFloat()
        val textHeight = strokeLayout.height.toFloat()

        // Calculate outer box dimensions (text + padding on all sides)
        val outerBoxWidth = textWidth + textPaddingBitmapX * 2
        val outerBoxHeight = textHeight + textPaddingBitmapY * 2

        // Pivot at the center of the OUTER BOX
        // box.offset points to top-left of the outer box
        val pivotX = box.offset.x + outerBoxWidth / 2f
        val pivotY = box.offset.y + outerBoxHeight / 2f

        // Text drawing position is offset by padding from box position
        val textTopLeftX = box.offset.x + textPaddingBitmapX
        val textTopLeftY = box.offset.y + textPaddingBitmapY

        // Apply transformations: scale -> rotate -> translate
        canvas.withScale(box.scale, box.scale, pivotX, pivotY) {
            canvas.withRotation(box.rotation, pivotX, pivotY) {
                canvas.withTranslation(textTopLeftX, textTopLeftY) {
                    strokeLayout.draw(this)
                    fillLayout.draw(this)
                }
            }
        }
    }
}