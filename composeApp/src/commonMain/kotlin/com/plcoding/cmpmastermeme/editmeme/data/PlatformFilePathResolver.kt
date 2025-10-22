package com.plcoding.cmpmastermeme.editmeme.data

import com.plcoding.cmpmastermeme.editmeme.domain.FilePathResolver

expect class PlatformFilePathResolver : FilePathResolver {
    override fun getAbsolutePath(fileName: String): String
    override fun extractFileName(absolutePath: String): String
}