package com.par9uet.jm.di

import com.par9uet.jm.repository.ComicRepository
import com.par9uet.jm.repository.UserRepository
import com.par9uet.jm.repository.impl.ComicRepositoryImpl
import com.par9uet.jm.repository.impl.UserRepositoryImpl
import org.koin.dsl.module

val RepositoryModule = module {
    single<UserRepository> { UserRepositoryImpl(get()) }
    single<ComicRepository> { ComicRepositoryImpl(get()) }
}