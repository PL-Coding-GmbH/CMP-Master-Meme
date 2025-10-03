package com.plcoding.cmpmastermeme.core.database

import androidx.room.TypeConverter
import kotlin.uuid.Uuid

class UuidTypeConverter {
    @TypeConverter
    fun fromUuid(uuid: Uuid?): String? {
        return uuid?.toString()
    }

    @TypeConverter
    fun toUuid(uuid: String?): Uuid? {
        return uuid?.let { Uuid.parse(it) }
    }
}