package com.plcoding.cmpmastermeme.core.data

import com.plcoding.cmpmastermeme.core.database.MasterMemeDatabase
import com.plcoding.cmpmastermeme.core.domain.LocalMemeDataSource
import com.plcoding.cmpmastermeme.core.domain.Meme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.uuid.Uuid

class RoomLocalMemeDataSource(
    db: MasterMemeDatabase
) : LocalMemeDataSource {

    private val dao by lazy { db.memeDao() }

    override fun observeAll(): Flow<List<Meme>> {
        return dao.getMemes().map { it.toDomain() }
    }

    override suspend fun save(meme: Meme) {
        dao.insertMeme(meme.toEntity())
    }

    override suspend fun delete(id: Uuid) {
        dao.deleteById(id)
    }
}

