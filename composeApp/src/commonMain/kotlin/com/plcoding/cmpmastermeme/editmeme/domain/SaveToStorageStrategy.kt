package com.plcoding.cmpmastermeme.editmeme.domain

interface SaveToStorageStrategy {
    fun getFilePath(fileName: String): FilePath
}