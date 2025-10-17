package com.plcoding.cmpmastermeme.memelist

import com.plcoding.cmpmastermeme.core.domain.FilePathResolver
import com.plcoding.cmpmastermeme.core.domain.Meme

fun Meme.toMemeUi(filePathResolver: FilePathResolver): MemeUi {
    return MemeUi(
        id = id,
        // Convert stored path to absolute path for current app session
        imageUri = filePathResolver.getAbsolutePath(imageUri)
    )
}

fun List<Meme>.toMemeUiList(filePathResolver: FilePathResolver): List<MemeUi> {
    return map { it.toMemeUi(filePathResolver) }
}