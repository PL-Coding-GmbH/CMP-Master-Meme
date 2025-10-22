package com.plcoding.cmpmastermeme.editmeme.data

import android.content.Context
import com.plcoding.cmpmastermeme.editmeme.domain.FilePath
import com.plcoding.cmpmastermeme.editmeme.domain.SaveToStorageStrategy
import java.io.File

actual class CacheSaveStrategy(
    private val context: Context
) : SaveStrategy {
    actual override fun getFilePath(fileName: String): FilePath {
        return File(context.cacheDir, fileName).absolutePath
    }
}