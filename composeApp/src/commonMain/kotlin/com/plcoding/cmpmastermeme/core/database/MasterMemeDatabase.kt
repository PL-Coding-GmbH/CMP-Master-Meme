package com.plcoding.cmpmastermeme.core.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@TypeConverters(
    value = [UuidTypeConverter::class]
)
@Database(
    entities = [MemeEntity::class],
    version = 1,
    exportSchema = true
)
@ConstructedBy(MasterMemeDatabaseConstructor::class)
abstract class MasterMemeDatabase : RoomDatabase() {
    abstract fun memeDao(): MemeDao

    companion object {
        const val DB_NAME = "master_meme.db"
    }
}