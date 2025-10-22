package com.plcoding.cmpmastermeme.editmeme.data

import com.plcoding.cmpmastermeme.editmeme.domain.FilePath
import com.plcoding.cmpmastermeme.editmeme.domain.SaveToStorageStrategy
import platform.Foundation.*

actual class CacheSaveStrategy : SaveStrategy {
    actual override fun getFilePath(fileName: String): FilePath {
        val cacheDirectory = NSSearchPathForDirectoriesInDomains(
            NSCachesDirectory,
            NSUserDomainMask,
            true
        ).firstOrNull() as? String ?: throw IllegalStateException("Could not find cache directory")
        
        return "$cacheDirectory/$fileName"
    }
}