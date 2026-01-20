package com.par9uet.jm.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.par9uet.jm.data.models.DownloadComic
import com.par9uet.jm.database.converter.ListStringToStringConverter
import com.par9uet.jm.database.dao.DownloadComicDao

@Database(entities = [DownloadComic::class], version = 2)
@TypeConverters(ListStringToStringConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun downloadComicDao(): DownloadComicDao
}