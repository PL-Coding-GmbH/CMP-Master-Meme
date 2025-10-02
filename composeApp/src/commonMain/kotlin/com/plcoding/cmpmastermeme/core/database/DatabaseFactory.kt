package com.plcoding.cmpmastermeme.core.database

import androidx.room.RoomDatabase

expect class DatabaseFactory {
      fun create(): RoomDatabase.Builder<MasterMemeDatabase>
  }