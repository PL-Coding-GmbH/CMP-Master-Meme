package com.plcoding.cmpmastermeme.core.domain

interface SendableFileManager {
    fun shareFile(filePath: String, mimeType: String = "image/png")
}

expect class PlatformSendableFileManager : SendableFileManager {
    override fun shareFile(filePath: String, mimeType: String)
}