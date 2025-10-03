package com.plcoding.cmpmastermeme.core.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Dao
interface MemeDao {
    @Query("SELECT * FROM tbl_meme ORDER BY createdAt DESC")
    fun getMemes(): Flow<List<MemeEntity>>

    @Upsert
    suspend fun insertMeme(meme: MemeEntity)

    @Query("SELECT * FROM tbl_meme WHERE id IN (:ids)")
    suspend fun getMemesByIds(ids: Set<Int>): List<MemeEntity>

    @Query("DELETE FROM tbl_meme WHERE id = :id")
    suspend fun deleteById(id: Uuid)
}