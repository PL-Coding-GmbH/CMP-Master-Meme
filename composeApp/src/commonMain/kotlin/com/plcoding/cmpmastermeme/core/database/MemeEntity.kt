package com.plcoding.cmpmastermeme.core.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.uuid.Uuid

@Entity(tableName = "tbl_meme")
data class MemeEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Uuid = Uuid.random(),
    val imageUri: String,
    val createdAt: Long
)