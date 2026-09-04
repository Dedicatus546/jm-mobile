package com.par9uet.jm.database.converter

import androidx.room.TypeConverter
import com.par9uet.jm.utils.json

class ListStringToStringConverter {

    @TypeConverter
    fun fromList(list: List<String>): String {
        return json.encodeToString(list)
    }

    @TypeConverter
    fun toList(value: String): List<String> {
        return json.decodeFromString(value) ?: emptyList()
    }
}