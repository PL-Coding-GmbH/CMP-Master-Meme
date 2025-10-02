package com.plcoding.cmpmastermeme.core.database

import androidx.room.RoomDatabaseConstructor

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object MasterMemeDatabaseConstructor : RoomDatabaseConstructor<MasterMemeDatabase> {
    override fun initialize(): MasterMemeDatabase
}