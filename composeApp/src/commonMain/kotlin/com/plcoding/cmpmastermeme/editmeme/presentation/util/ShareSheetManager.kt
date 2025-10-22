package com.plcoding.cmpmastermeme.editmeme.presentation.util

expect class ShareSheetManager {
    suspend fun shareFile(filePath: String, mimeType: String)
}