package com.par9uet.jm.di

import androidx.room.Room
import com.par9uet.jm.database.AppDatabase
import com.par9uet.jm.store.DownloadManager
import com.par9uet.jm.ui.viewModel.DownloadViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module


val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "app_database"
        ).fallbackToDestructiveMigration(false).build()
    }
    single { get<AppDatabase>().downloadComicDao() }
    single { DownloadManager(get(), get(), androidContext(), get()) }
    viewModel { DownloadViewModel(get()) }
}