package com.plcoding.cmpmastermeme.editmeme.domain

interface FilePathResolver {
    fun getAbsolutePath(fileName: String): String
    fun extractFileName(absolutePath: String): String
}