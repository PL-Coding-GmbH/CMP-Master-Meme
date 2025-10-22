package com.plcoding.cmpmastermeme.editmeme.data

import com.plcoding.cmpmastermeme.editmeme.domain.FilePath
import com.plcoding.cmpmastermeme.editmeme.domain.SaveToStorageStrategy

expect class CacheSaveStrategy : SaveToStorageStrategy {
    override fun getFilePath(fileName: String): FilePath
}