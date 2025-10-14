package com.plcoding.cmpmastermeme.core.domain

interface SaveToStorageStrategy {
    fun getFilePath(fileName: String): FilePath
}

expect class CacheSaveStrategy : SaveToStorageStrategy {
    override fun getFilePath(fileName: String): FilePath
}

expect class PrivateAppDirSaveStrategy : SaveToStorageStrategy {
    override fun getFilePath(fileName: String): FilePath
}