package com.plcoding.cmpmastermeme.core.domain

import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

interface MemeDataSource {
    fun observeAll(): Flow<List<Meme>>
    suspend fun save(meme: Meme)
    suspend fun delete(id: Uuid)
}