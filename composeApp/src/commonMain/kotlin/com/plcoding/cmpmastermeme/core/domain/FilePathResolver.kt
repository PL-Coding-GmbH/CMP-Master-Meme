package com.plcoding.cmpmastermeme.core.domain

interface FilePathResolver {
    fun getAbsolutePath(fileName: String): String
    fun extractFileName(absolutePath: String): String
}

expect class PlatformFilePathResolver : FilePathResolver {
    override fun getAbsolutePath(fileName: String): String
    override fun extractFileName(absolutePath: String): String
}