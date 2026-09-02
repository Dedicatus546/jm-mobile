package com.par9uet.jm.di

import com.par9uet.jm.controller.DownloadConcurrencyController
import com.par9uet.jm.ui.viewModel.ComicChapterDownloadViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val downloadModule = module {
    single {
        DownloadConcurrencyController(3)
    }

    viewModel { ComicChapterDownloadViewModel(get(), get(), get(), get()) }
}