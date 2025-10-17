@file:OptIn(ExperimentalForeignApi::class)

package com.plcoding.cmpmastermeme.core.domain

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import com.plcoding.cmpmastermeme.editmeme.models.MemeText
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.useContents
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import platform.CoreGraphics.CGContextRef
import platform.CoreGraphics.CGRect
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSize
import platform.CoreGraphics.CGSizeMake
import platform.Foundation.NSData
import platform.Foundation.NSNumber
import platform.Foundation.NSString
import platform.Foundation.create
import platform.Foundation.writeToFile
import platform.UIKit.NSFontAttributeName
import platform.UIKit.NSForegroundColorAttributeName
import platform.UIKit.NSMutableParagraphStyle
import platform.UIKit.NSParagraphStyleAttributeName
import platform.UIKit.NSStrokeColorAttributeName
import platform.UIKit.NSStrokeWidthAttributeName
import platform.UIKit.NSTextAlignmentCenter
import platform.UIKit.UIColor
import platform.UIKit.UIFont
import platform.UIKit.UIGraphicsBeginImageContextWithOptions
import platform.UIKit.UIGraphicsEndImageContext
import platform.UIKit.UIGraphicsGetCurrentContext
import platform.UIKit.UIGraphicsGetImageFromCurrentImageContext
import platform.UIKit.UIImage
import platform.UIKit.UIImagePNGRepresentation
import platform.UIKit.drawInRect
import platform.UIKit.sizeWithAttributes

actual class MemeExporter {

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
        beginImageContext(canvasSize)

        val context = UIGraphicsGetCurrentContext()
        if (context == null) {
            UIGraphicsEndImageContext()
            return null
        }

        drawBackground(backgroundImage, canvasSize)

        textBoxes.forEach { textBox ->
            drawTextBox(
                textBox = textBox,
                canvasSize = canvasSize
            )
        }

        val resultImage = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()

        return resultImage
    }

    /**
     * Creates an iOS offscreen graphics context for drawing.
     * false = opaque background, 0.0 = use device's natural scale factor.
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
     * Uses the graphics context for precise positioning and scaling relative to canvas size.
     */
    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    private fun drawTextBox(
        textBox: MemeText,
        canvasSize: IntSize
    ) {
        val scaledFontSize = textBox.fontSize
        val attributes = createTextAttributes(scaledFontSize)
        val textNS = NSString.create(string = textBox.text)
        val textSize = textNS.sizeWithAttributes(attributes)
        val drawRect = createTextRect(textBox.offset, textSize, canvasSize)

        textNS.drawInRect(drawRect, withAttributes = attributes)
    }

    /**
     * Creates iOS text styling attributes for meme text.
     *
     * NSStrokeWidthAttributeName controls text stroke behavior:
     * - Positive values (e.g., 3.0): Hollow outline only, no fill
     * - Negative values (e.g., -3.0): Filled text WITH outline (perfect for memes)
     * - Zero (0.0): No stroke, filled text only
     * - Higher absolute values = thicker stroke (e.g., -5.0 = thicker outline than -2.0)
     *
     * For our meme text, we use -3.0 to get white filled text with black outline.
     */
    private fun createTextAttributes(fontSize: Float): Map<Any?, Any?> {
        val font = UIFont.boldSystemFontOfSize(fontSize.toDouble())
        val paragraphStyle = NSMutableParagraphStyle().apply {
            setAlignment(NSTextAlignmentCenter)
        }

        return mapOf(
            NSFontAttributeName to font,
            NSForegroundColorAttributeName to UIColor.whiteColor,
            NSStrokeColorAttributeName to UIColor.blackColor,
            NSStrokeWidthAttributeName to NSNumber(-3.0),
            NSParagraphStyleAttributeName to paragraphStyle
        )
    }


    /**
     * Creates a CGRect (iOS rectangle) for text positioning.
     * Centers text horizontally and positions it relative to canvas size with bounds checking.
     */
    private fun createTextRect(
        offset: Offset,
        textSize: CValue<CGSize>,
        canvasSize: IntSize
    ): CValue<CGRect> {
        return textSize.useContents {
            CGRectMake(
                x = offset.x.toDouble(),
                y = offset.y.toDouble(),
                width = width,
                height = height
            )
        }
    }
}