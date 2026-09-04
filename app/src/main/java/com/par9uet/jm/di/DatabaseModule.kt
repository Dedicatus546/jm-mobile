package com.par9uet.jm.di

import android.content.Context
import androidx.room.Room
import com.par9uet.jm.database.AppDatabase
import com.par9uet.jm.database.dao.DownloadComicDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_database"
        ).fallbackToDestructiveMigration(false).build()
    }

    @Provides
    @Singleton
    fun provideDownloadComicDao(
        appDatabase: AppDatabase
    ): DownloadComicDao {
        return appDatabase.downloadComicDao()
    }
}