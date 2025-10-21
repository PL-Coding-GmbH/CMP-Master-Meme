package com.plcoding.cmpmastermeme.core.domain

import platform.Foundation.*

actual class CacheSaveStrategy : SaveToStorageStrategy {
    actual override fun getFilePath(fileName: String): FilePath {
        val cacheDirectory = NSSearchPathForDirectoriesInDomains(
            NSCachesDirectory,
            NSUserDomainMask,
            true
        ).firstOrNull() as? String ?: throw IllegalStateException("Could not find cache directory")
        
        return "$cacheDirectory/$fileName"
    }
}