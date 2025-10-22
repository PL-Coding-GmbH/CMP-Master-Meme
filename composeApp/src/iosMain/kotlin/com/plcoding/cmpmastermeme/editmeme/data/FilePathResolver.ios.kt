package com.plcoding.cmpmastermeme.editmeme.data

import com.plcoding.cmpmastermeme.editmeme.domain.FilePathResolver
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSUserDomainMask

actual class PlatformFilePathResolver : FilePathResolver {

    /*
        On iOS, we need to reconstruct the path each time
        because the app sandbox directory changes between launches
     */
    actual override fun getAbsolutePath(fileName: String): String {

        // Extract just the filename if it's a full path
        val cleanFileName = if (fileName.contains("/")) {
            fileName.substringAfterLast("/")
        } else {
            fileName
        }
        
        // Get current documents directory
        val documentsPath = NSSearchPathForDirectoriesInDomains(
            NSDocumentDirectory,
            NSUserDomainMask,
            true
        ).firstOrNull() as? String ?: throw IllegalStateException("Could not find documents directory")
        
        return "$documentsPath/$cleanFileName"
    }
    
    actual override fun extractFileName(absolutePath: String): String {
        return absolutePath.substringAfterLast("/")
    }
}