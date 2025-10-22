package com.plcoding.cmpmastermeme.editmeme.presentation.util

import platform.Foundation.NSURL
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

actual class ShareSheetManager {
    actual suspend fun shareFile(filePath: String, mimeType: String) = withContext(Dispatchers.Main) {
        val fileUrl = NSURL.fileURLWithPath(filePath)
        
        // Create items array with the file URL only
        // Using just the file URL prevents duplicate images in the share sheet
        val itemsToShare = listOf(fileUrl)
        
        // Create activity view controller
        val activityViewController = UIActivityViewController(
            activityItems = itemsToShare,
            applicationActivities = null
        )
        
        // Get the root view controller
        val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
            ?: throw IllegalStateException("No root view controller found")
        
        // Present the share sheet
        rootViewController.presentViewController(
            viewControllerToPresent = activityViewController,
            animated = true,
            completion = null
        )
    }
}