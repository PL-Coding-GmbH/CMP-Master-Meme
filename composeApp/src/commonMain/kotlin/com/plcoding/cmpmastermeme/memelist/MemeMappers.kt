package com.plcoding.cmpmastermeme.memelist

import com.plcoding.cmpmastermeme.core.domain.Meme

fun Meme.toMemeUi(): MemeUi {
    return MemeUi(
        id = id,
        imageUri = imageUri
    )
}

fun List<Meme>.toMemeUiList(): List<MemeUi> {
    return map { it.toMemeUi() }
}