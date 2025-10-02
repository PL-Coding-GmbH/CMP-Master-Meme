package com.plcoding.cmpmastermeme.core.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "memes")
data class MemeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val imageUri: String,
    val createdAt: Long
)