package com.plcoding.cmpmastermeme.core.data

import com.plcoding.cmpmastermeme.core.database.MemeEntity
import com.plcoding.cmpmastermeme.core.domain.Meme
import kotlin.time.Instant

fun MemeEntity.toDomain(): Meme {
    return Meme(
        id = this.id,
        imageUri = this.imageUri,
        createdAt = Instant.fromEpochMilliseconds(this.createdAt)
    )
}

fun List<MemeEntity>.toDomain(): List<Meme> {
    return this.map { it.toDomain() }
}

fun Meme.toEntity(): MemeEntity {
    return MemeEntity(
        id = this.id,
        imageUri = this.imageUri,
        createdAt = this.createdAt.toEpochMilliseconds()
    )
}