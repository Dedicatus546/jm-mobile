package com.par9uet.jm.database.converter

import androidx.room.TypeConverter
import com.par9uet.jm.database.model.DownloadStatus

class DownloadStatusConverter {

    @TypeConverter
    fun fromDownloadStatus(value: DownloadStatus): String {
        return value.name
    }

    @TypeConverter
    fun toDataString(value: String): DownloadStatus {
        return DownloadStatus.valueOf(value)
    }
}