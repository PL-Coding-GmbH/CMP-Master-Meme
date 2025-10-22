package com.plcoding.cmpmastermeme.core.domain

interface SaveStrategy {
    fun getFilePath(fileName: String): FilePath
}

expect class CacheSaveStrategy : SaveStrategy {
    override fun getFilePath(fileName: String): FilePath
}