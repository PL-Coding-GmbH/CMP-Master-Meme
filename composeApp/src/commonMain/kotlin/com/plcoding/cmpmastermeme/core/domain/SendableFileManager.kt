package com.plcoding.cmpmastermeme.core.domain

interface SendableFileManager {
    suspend fun shareFile(filePath: String, mimeType: String = "image/png")
}

expect class PlatformSendableFileManager : SendableFileManager {
    override suspend fun shareFile(filePath: String, mimeType: String)
}