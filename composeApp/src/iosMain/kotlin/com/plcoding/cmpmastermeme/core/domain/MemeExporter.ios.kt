@file:OptIn(ExperimentalForeignApi::class)

package com.plcoding.cmpmastermeme.core.domain

import androidx.compose.ui.unit.IntSize
import com.plcoding.cmpmastermeme.editmeme.models.MemeText
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.useContents
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import platform.CoreGraphics.CGContextRef
import platform.CoreGraphics.CGContextRestoreGState
import platform.CoreGraphics.CGContextRotateCTM
import platform.CoreGraphics.CGContextSaveGState
import platform.CoreGraphics.CGContextScaleCTM
import platform.CoreGraphics.CGContextTranslateCTM
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSizeMake
import platform.Foundation.NSData
import platform.Foundation.NSNumber
import platform.Foundation.NSString
import platform.Foundation.create
import platform.Foundation.writeToFile
import platform.UIKit.NSFontAttributeName
import platform.UIKit.NSForegroundColorAttributeName
import platform.UIKit.NSLineBreakByWordWrapping
import platform.UIKit.NSMutableParagraphStyle
import platform.UIKit.NSParagraphStyleAttributeName
import platform.UIKit.NSStrokeColorAttributeName
import platform.UIKit.NSStrokeWidthAttributeName
import platform.UIKit.NSTextAlignmentLeft
import platform.UIKit.UIColor
import platform.UIKit.UIFont
import platform.UIKit.UIGraphicsBeginImageContextWithOptions
import platform.UIKit.UIGraphicsEndImageContext
import platform.UIKit.UIGraphicsGetCurrentContext
import platform.UIKit.UIGraphicsGetImageFromCurrentImageContext
import platform.UIKit.UIImage
import platform.UIKit.UIImagePNGRepresentation
import platform.UIKit.UIScreen
import platform.UIKit.boundingRectWithSize
import platform.UIKit.drawWithRect
import kotlin.math.PI

actual class MemeExporter {

    private val calculator by lazy {
        MemeRenderCalculator(
            density = UIScreen.mainScreen.scale.toFloat()
        )
    }

    actual suspend fun exportMeme(
        backgroundImageBytes: ByteArray,
        textBoxes: List<MemeText>,
        canvasSize: IntSize,
        fileName: String,
        saveStrategy: SaveToStorageStrategy
    ): Result<FilePath> = withContext(Dispatchers.IO) {
        try {
            val backgroundImage =
                createBackgroundImage(
                    imageBytes = backgroundImageBytes
                )
                    ?: return@withContext Result.failure(Exception("Failed to create background image"))

            val resultImage = renderMeme(
                backgroundImage = backgroundImage,
                textBoxes = textBoxes,
                canvasSize = canvasSize
            )
                ?: return@withContext Result.failure(Exception("Failed to render meme"))

            saveMemeToFile(resultImage, fileName, saveStrategy)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Result.failure(e)
        }
    }

    /**
     * Converts a ByteArray to a UIImage for iOS rendering.
     * usePinned ensures the ByteArray memory stays valid while NSData is created.
     * NSData is iOS Foundation's data container type.
     */
    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    private fun createBackgroundImage(imageBytes: ByteArray): UIImage? {
        val imageData = imageBytes.usePinned { pinned ->
            NSData.create(bytes = pinned.addressOf(0), length = imageBytes.size.toULong())
        }
        return UIImage.imageWithData(imageData)
    }

    /**
     * Creates an iOS graphics context and renders the meme by drawing background + text.
     * UIGraphics context is iOS's offscreen drawing environment.
     */
    @OptIn(ExperimentalForeignApi::class)
    private fun renderMeme(
        backgroundImage: UIImage, textBoxes: List<MemeText>, canvasSize: IntSize
    ): UIImage? {
        // Use the actual image size for the context, not the display size
        val imageSize = IntSize(
            width = backgroundImage.size.useContents { width.toInt() },
            height = backgroundImage.size.useContents { height.toInt() }
        )

        beginImageContext(imageSize)

        val context = UIGraphicsGetCurrentContext()
        if (context == null) {
            UIGraphicsEndImageContext()
            return null
        }

        drawBackground(backgroundImage, imageSize)

        // Calculate scale factors from display to image coordinates
        val scaleFactors = calculator.calculateScaleFactors(
            imageSize.width,
            imageSize.height,
            canvasSize
        )

        // Convert text boxes to scaled coordinates
        val scaledBoxes = calculator.calculateScaledTextBoxes(
            textBoxes,
            scaleFactors,
            canvasSize.width
        )

        scaledBoxes.forEach { scaledBox ->
            drawTextBox(
                context = context,
                scaledBox = scaledBox
            )
        }

        val resultImage = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()

        return resultImage
    }

    /**
     * Creates an iOS offscreen graphics context for drawing.
     */
    @OptIn(ExperimentalForeignApi::class)
    private fun beginImageContext(size: IntSize) {
        UIGraphicsBeginImageContextWithOptions(
            CGSizeMake(size.width.toDouble(), size.height.toDouble()), false, 0.0
        )
    }

    /**
     * Draws the background image to fill the entire canvas.
     * CGRect defines the drawing rectangle in iOS coordinate space.
     */
    @OptIn(ExperimentalForeignApi::class)
    private fun drawBackground(image: UIImage, size: IntSize) {
        image.drawInRect(
            CGRectMake(
                x = 0.0,
                y = 0.0,
                width = size.width.toDouble(),
                height = size.height.toDouble()
            )
        )
    }

    /**
     * Converts UIImage to PNG data and saves to file system.
     * UIImagePNGRepresentation creates PNG byte data from UIImage.
     * atomically=true ensures the write operation is atomic (all-or-nothing).
     */
    private fun saveMemeToFile(
        image: UIImage,
        fileName: String,
        saveStrategy: SaveToStorageStrategy
    ): Result<FilePath> {
        val pngData = UIImagePNGRepresentation(image)
            ?: return Result.failure(Exception("Failed to convert image to PNG"))

        val filePath = saveStrategy.getFilePath(fileName)
        val saved = pngData.writeToFile(filePath, atomically = true)

        return if (saved) {
            Result.success(filePath)
        } else {
            Result.failure(Exception("Failed to save image to file"))
        }
    }

    /**
     * Renders a single text box with meme-style formatting (white text, black outline).
     * Applies rotation and scaling transformations around the text center.
     */
    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    private fun drawTextBox(
        context: CGContextRef,
        scaledBox: ScaledTextBox
    ) {
        // Create text attributes for measuring (use fill attributes for accurate size)
        val fillAttributes = createTextAttributes(scaledBox.scaledFontSize, isStroke = false)
        val strokeAttributes = createTextAttributes(
            scaledBox.scaledFontSize,
            isStroke = true,
            strokeWidth = scaledBox.strokeWidth
        )
        val textNS = NSString.create(string = scaledBox.text)

        // Calculate actual text size with wrapping using fill attributes
        val boundingRect = textNS.boundingRectWithSize(
            size = CGSizeMake(scaledBox.constraintWidth.toDouble(), 10000.0),
            options = 1L shl 0, // NSStringDrawingUsesLineFragmentOrigin
            attributes = fillAttributes,
            context = null
        )

        val actualTextWidth = boundingRect.useContents { size.width }
        val actualTextHeight = boundingRect.useContents { size.height }

        // Calculate pivot points for rotation
        val boxWithPivots = calculator.calculatePivotPoints(
            scaledBox,
            actualTextWidth.toFloat(),
            actualTextHeight.toFloat()
        )

        // Get text drawing position (with padding)
        val textPosition = calculator.getTextDrawingPosition(boxWithPivots)

        // Save the current graphics state
        CGContextSaveGState(context)

        // Apply transformations around the pivot point
        // 1. Translate to pivot
        CGContextTranslateCTM(
            context,
            boxWithPivots.pivotX.toDouble(),
            boxWithPivots.pivotY.toDouble()
        )

        // 2. Apply scale (only user-applied scale, not the font scale which is already in scaledFontSize)
        CGContextScaleCTM(context, boxWithPivots.scale.toDouble(), boxWithPivots.scale.toDouble())

        // 3. Apply rotation (convert degrees to radians)
        val radians = boxWithPivots.rotation * PI / 180.0
        CGContextRotateCTM(context, radians)

        // 4. Translate back from pivot
        CGContextTranslateCTM(
            context,
            -boxWithPivots.pivotX.toDouble(),
            -boxWithPivots.pivotY.toDouble()
        )

        // Draw the text at the calculated position
        val finalDrawRect = CGRectMake(
            x = textPosition.x.toDouble(),
            y = textPosition.y.toDouble(),
            width = scaledBox.constraintWidth.toDouble(),
            height = actualTextHeight
        )

        // Draw stroke first (behind fill)
        textNS.drawWithRect(
            rect = finalDrawRect,
            options = 1L shl 0, // NSStringDrawingUsesLineFragmentOrigin
            attributes = strokeAttributes,
            context = null
        )

        // Draw fill on top
        textNS.drawWithRect(
            rect = finalDrawRect,
            options = 1L shl 0, // NSStringDrawingUsesLineFragmentOrigin
            attributes = fillAttributes,
            context = null
        )

        // Restore the graphics state
        CGContextRestoreGState(context)
    }

    /**
     * Creates iOS text styling attributes for meme text.
     * Creates separate attributes for stroke and fill to match Android's approach.
     */
    private fun createTextAttributes(
        fontSize: Float,
        isStroke: Boolean,
        strokeWidth: Float = 0f
    ): Map<Any?, Any?> {
        // Load the custom Impact font from bundle
        // The font file is in the app bundle from Compose resources
        val font = UIFont.fontWithName("Impact", size = fontSize.toDouble())
            ?: UIFont.boldSystemFontOfSize(fontSize.toDouble())

        val paragraphStyle = NSMutableParagraphStyle().apply {
            setAlignment(NSTextAlignmentLeft)
            setLineBreakMode(NSLineBreakByWordWrapping)
        }

        return if (isStroke) {
            // Stroke only (positive value = hollow text)
            // Convert actual stroke width to percentage of font size for iOS
            val strokePercentage = if (strokeWidth > 0) {
                (strokeWidth / fontSize * 100).toDouble().coerceIn(1.0, 10.0)
            } else {
                3.0 // Fallback value
            }
            mapOf(
                NSFontAttributeName to font,
                NSForegroundColorAttributeName to UIColor.clearColor(),
                NSStrokeColorAttributeName to UIColor.blackColor,
                NSStrokeWidthAttributeName to NSNumber(strokePercentage),
                NSParagraphStyleAttributeName to paragraphStyle
            )
        } else {
            // Fill only
            mapOf(
                NSFontAttributeName to font,
                NSForegroundColorAttributeName to UIColor.whiteColor,
                NSParagraphStyleAttributeName to paragraphStyle
            )
        }
    }
}