package com.plcoding.cmpmastermeme.core.domain

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File

actual class PlatformSendableFileManager(
    private val context: Context
): SendableFileManager {
    actual override fun shareFile(filePath: String, mimeType: String) {
        val file = File(filePath)

        // Use FileProvider to get a content:// URI
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        val chooser = Intent.createChooser(shareIntent, null).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        context.startActivity(chooser)
    }
}