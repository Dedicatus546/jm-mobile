package com.par9uet.jm.di

import com.par9uet.jm.repository.ComicRepository
import com.par9uet.jm.repository.impl.ComicRepositoryImpl
import com.par9uet.jm.ui.viewModel.ComicCategoryViewModel
import com.par9uet.jm.ui.viewModel.ComicCommentViewModel
import com.par9uet.jm.ui.viewModel.ComicDetailViewModel
import com.par9uet.jm.ui.viewModel.ComicReadViewModel
import com.par9uet.jm.ui.viewModel.ComicSearchResultViewModel
import com.par9uet.jm.ui.viewModel.ComicSearchViewModel
import com.par9uet.jm.ui.viewModel.ComicViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val comicModule = module {
    single { ComicRepositoryImpl(get(), get()) } bind ComicRepository::class

    viewModel { ComicViewModel(get()) }
    viewModel { ComicDetailViewModel(get(), get(), get(), get()) }
    viewModel { ComicReadViewModel(get(), get(), get()) }
    viewModel { ComicSearchResultViewModel(get()) }
    viewModel { ComicSearchViewModel(get(), get()) }
    viewModel { ComicCommentViewModel(get(), get()) }
    viewModel { ComicCategoryViewModel(get()) }
}