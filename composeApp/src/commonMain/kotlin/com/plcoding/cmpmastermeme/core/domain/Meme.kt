package com.plcoding.cmpmastermeme.core.domain

import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.uuid.Uuid

data class Meme(
    val id: Uuid = Uuid.random(),
    val imageUri: String,
    val createdAt: Instant = Clock.System.now()
)