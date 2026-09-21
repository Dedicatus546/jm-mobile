package com.par9uet.jm.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.par9uet.jm.database.converter.DownloadStatusConverter
import com.par9uet.jm.database.converter.ListStringToStringConverter
import com.par9uet.jm.database.converter.UriConverter
import com.par9uet.jm.database.dao.LocalComicDao
import com.par9uet.jm.database.dao.LocalComicPicDao
import com.par9uet.jm.database.model.LocalComic
import com.par9uet.jm.database.model.LocalComicPic

@Database(entities = [LocalComic::class, LocalComicPic::class], version = 7)
@TypeConverters(
    ListStringToStringConverter::class,
    UriConverter::class,
    DownloadStatusConverter::class
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun localComicDao(): LocalComicDao

    abstract fun localComicPicDao(): LocalComicPicDao
}