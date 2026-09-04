package com.par9uet.jm.di

import com.par9uet.jm.repository.ComicRepository
import com.par9uet.jm.repository.RemoteSettingRepository
import com.par9uet.jm.repository.UserRepository
import com.par9uet.jm.repository.impl.ComicRepositoryImpl
import com.par9uet.jm.repository.impl.RemoteSettingRepositoryImpl
import com.par9uet.jm.repository.impl.UserRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun provideComicRepository(impl: ComicRepositoryImpl): ComicRepository

    @Binds
    abstract fun provideRemoteSettingRepository(impl: RemoteSettingRepositoryImpl): RemoteSettingRepository

    @Binds
    abstract fun provideUserRepository(impl: UserRepositoryImpl): UserRepository
}