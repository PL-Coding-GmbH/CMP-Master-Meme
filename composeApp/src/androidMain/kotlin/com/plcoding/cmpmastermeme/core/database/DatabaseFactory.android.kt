package com.plcoding.cmpmastermeme.core.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

actual class DatabaseFactory(private val context: Context) {
    actual fun create(): RoomDatabase.Builder<MasterMemeDatabase> {
        val appContext = context.applicationContext
        val dbFile = appContext.getDatabasePath(MasterMemeDatabase.DB_NAME)

        return Room.databaseBuilder(
            context = appContext,
            name = dbFile.absolutePath
        )
    }
}