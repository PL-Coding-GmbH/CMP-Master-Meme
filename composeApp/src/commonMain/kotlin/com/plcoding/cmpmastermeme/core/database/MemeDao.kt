package com.plcoding.cmpmastermeme.core.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface MemeDao {
    @Query("SELECT * FROM memes ORDER BY createdAt DESC")
    fun getMemes(): Flow<List<MemeEntity>>

    @Upsert
    suspend fun insertMeme(meme: MemeEntity)

    @Query("SELECT * FROM memes WHERE id IN (:ids)")
    suspend fun getMemesByIds(ids: Set<Int>): List<MemeEntity>

    @Delete
    suspend fun deleteMemes(memes: Set<MemeEntity>)
}