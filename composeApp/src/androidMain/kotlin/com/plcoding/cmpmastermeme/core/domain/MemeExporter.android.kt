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

            // TODO co-operative compression
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

        // Calculate scaling factors from display size to bitmap size
        // Without scaling factor, small offsets on screen translates to larger offsets on rendered image
        val scaleX = if (displaySize.width > 0) bitmapWidth / displaySize.width else 1f
        val scaleY = if (displaySize.height > 0) bitmapHeight / displaySize.height else 1f

        textBoxes.forEach { box ->
            // Scale the position from display coordinates to bitmap coordinates
            val scaledBox = box.copy(
                offset = Offset(
                    x = box.offset.x * scaleX,
                    y = box.offset.y * scaleY
                )
            )
            drawText(canvas, scaledBox)
        }

        return output
    }

    private fun drawText(
        canvas: Canvas,
        box: MemeText,
    ) {
        // Use a scaling factor to match Compose's rendering
        // Compose seems to render text smaller than Android Canvas for the same SP value
        // Halving the size works well
        val scaleFactor = 0.5f
        val textSizePx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            box.fontSize * scaleFactor,
            context.resources.displayMetrics
        )

        val strokeWidthPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            1f,
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

        /*
            There is a difference in what the offset means
            - In Compose UI the offset is the top-left corner of the text box
            - In drawText() the y-coordinate is the baseline of the text

            That is why we use fontMetric.top (a negative value) to start drawing higher
            to match the visual the user sees on the screen.

            Here is a visual: https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fi.loli.net%2F2021%2F09%2F18%2FUTeL41J7sRImD8H.jpg&f=1&nofb=1&ipt=ce1cb7c1b0ae3e938637c28295f77ccbefd0ec3d49444e456276062e1ac42485
         */
        val x = box.offset.x
        val y = box.offset.y - strokePaint.fontMetrics.top
        
        // Draw stroke first, then fill on top - order matters to get the same appearance as on the screen
        canvas.drawText(box.text, x, y, strokePaint)
        canvas.drawText(box.text, x, y, fillPaint)
    }
}