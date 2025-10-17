package com.plcoding.cmpmastermeme.core.domain

import android.content.Context
import java.io.File

actual class PlatformFilePathResolver(
    private val context: Context
) : FilePathResolver {

    actual override fun getAbsolutePath(fileName: String): String {
        if (fileName.startsWith("/")) {
            return fileName
        }
        return File(context.getExternalFilesDir(null), fileName).absolutePath
    }

    actual override fun extractFileName(absolutePath: String): String {
        return File(absolutePath).name
    }
}