package com.par9uet.jm.di

import com.par9uet.jm.retrofit.ProxyRetrofit
import com.par9uet.jm.retrofit.Retrofit
import com.par9uet.jm.retrofit.service.ComicService
import com.par9uet.jm.retrofit.service.ProxyApiService
import com.par9uet.jm.retrofit.service.RemoteSettingService
import com.par9uet.jm.retrofit.service.UserService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {

    @Provides
    @Singleton
    fun provideComicService(
        retrofit: Retrofit
    ): ComicService {
        return retrofit.createService(ComicService::class.java)
    }

    @Provides
    @Singleton
    fun provideRemoteSettingService(
        retrofit: Retrofit
    ): RemoteSettingService {
        return retrofit.createService(RemoteSettingService::class.java)
    }

    @Provides
    @Singleton
    fun provideUserService(
        retrofit: Retrofit
    ): UserService {
        return retrofit.createService(UserService::class.java)
    }

    @Provides
    @Singleton
    fun provideProxyApiService(
        retrofit: ProxyRetrofit
    ): ProxyApiService {
        return retrofit.createService(ProxyApiService::class.java)
    }
}