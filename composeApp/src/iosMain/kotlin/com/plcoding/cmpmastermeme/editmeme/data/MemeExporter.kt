package com.plcoding.cmpmastermeme.editmeme.data

import androidx.compose.ui.unit.IntSize
import com.plcoding.cmpmastermeme.editmeme.domain.FilePath
import com.plcoding.cmpmastermeme.editmeme.domain.SaveToStorageStrategy
import com.plcoding.cmpmastermeme.editmeme.presentation.models.MemeText
import com.plcoding.cmpmastermeme.editmeme.presentation.util.MemeRenderCalculator
import com.plcoding.cmpmastermeme.editmeme.presentation.util.ScaledTextBox
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
            density = UIScreen.Companion.mainScreen.scale.toFloat()
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
            NSData.Companion.create(bytes = pinned.addressOf(0), length = imageBytes.size.toULong())
        }
        return UIImage.Companion.imageWithData(imageData)
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
    private fun drawTextBox(
        context: CGContextRef,
        scaledBox: ScaledTextBox
    ) {
        val attributes = createMemeTextAttributes(scaledBox.scaledFontSize)
        val textNS = NSString.Companion.create(string = scaledBox.text)

        // Calculate text size
        val boundingRect = textNS.boundingRectWithSize(
            size = CGSizeMake(scaledBox.constraintWidth.toDouble(), 10000.0),
            options = 1L shl 0,
            attributes = attributes,
            context = null
        )

        val actualTextWidth = boundingRect.useContents { size.width }
        val actualTextHeight = boundingRect.useContents { size.height }

        // Get positioning from calculator
        val boxWithPivots = calculator.calculatePivotPoints(
            scaledBox = scaledBox,
            actualTextWidth = actualTextWidth.toFloat(),
            textHeight = actualTextHeight.toFloat()
        )
        val textPosition = calculator.getTextDrawingPosition(boxWithPivots)

        // Apply transforms and draw
        CGContextSaveGState(context)

        CGContextTranslateCTM(
            c = context,
            tx = boxWithPivots.pivotX.toDouble(),
            ty = boxWithPivots.pivotY.toDouble()
        )
        CGContextScaleCTM(
            c = context,
            sx = boxWithPivots.scale.toDouble(),
            sy = boxWithPivots.scale.toDouble()
        )
        CGContextRotateCTM(
            c = context,
            angle = boxWithPivots.rotation * PI / 180.0
        )
        CGContextTranslateCTM(
            c = context,
            tx = -boxWithPivots.pivotX.toDouble(),
            ty = -boxWithPivots.pivotY.toDouble()
        )

        textNS.drawWithRect(
            rect = CGRectMake(
                textPosition.x.toDouble(),
                textPosition.y.toDouble(),
                scaledBox.constraintWidth.toDouble(),
                actualTextHeight
            ),
            options = 1L shl 0,
            attributes = attributes,
            context = null
        )

        CGContextRestoreGState(context)
    }

    /**
     * Creates meme text attributes with white fill and black outline
     */
    private fun createMemeTextAttributes(fontSize: Float): Map<Any?, Any?> {
        val font = UIFont.Companion.fontWithName("Impact", size = fontSize.toDouble())
            ?: UIFont.Companion.boldSystemFontOfSize(fontSize.toDouble())

        val paragraphStyle = NSMutableParagraphStyle().apply {
            setAlignment(NSTextAlignmentLeft)
            setLineBreakMode(NSLineBreakByWordWrapping)
        }

        return mapOf(
            NSFontAttributeName to font,
            NSForegroundColorAttributeName to UIColor.Companion.whiteColor,
            NSStrokeColorAttributeName to UIColor.Companion.blackColor,
            NSStrokeWidthAttributeName to NSNumber(-3.0), // Negative = fill + stroke
            NSParagraphStyleAttributeName to paragraphStyle
        )
    }
}