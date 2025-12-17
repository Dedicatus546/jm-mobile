package com.par9uet.jm.di

import com.par9uet.jm.repository.ComicRepository
import com.par9uet.jm.repository.impl.ComicRepositoryImpl
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val comicModule = module {
    single { ComicRepositoryImpl(get()) } bind ComicRepository::class

    viewModel { ComicViewModel(get(), get()) }
}