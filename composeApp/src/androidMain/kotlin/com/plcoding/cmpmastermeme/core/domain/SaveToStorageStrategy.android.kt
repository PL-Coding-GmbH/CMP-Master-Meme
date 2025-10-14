package com.plcoding.cmpmastermeme.core.domain

import android.content.Context
import java.io.File

actual class CacheSaveStrategy(
    private val context: Context
) : SaveToStorageStrategy {
    actual override fun getFilePath(fileName: String): FilePath {
        return File(context.cacheDir, fileName).absolutePath
    }
}

actual class PrivateAppDirSaveStrategy(
    private val context: Context
) : SaveToStorageStrategy {
    actual override fun getFilePath(fileName: String): FilePath {
        return File(context.getExternalFilesDir(null), fileName).absolutePath
    }
}