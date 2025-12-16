package com.par9uet.jm.di

import com.par9uet.jm.repository.UserRepository
import com.par9uet.jm.repository.impl.UserRepositoryImpl
import com.par9uet.jm.ui.viewModel.UserViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val userModule = module {
    single<UserRepository> { UserRepositoryImpl(get()) }

    viewModel { UserViewModel(get(), get()) }
}