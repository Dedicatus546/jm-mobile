package com.par9uet.jm.di

import com.par9uet.jm.repository.LocalSettingRepository
import com.par9uet.jm.repository.RemoteSettingRepository
import com.par9uet.jm.repository.impl.LocalSettingRepositoryImpl
import com.par9uet.jm.repository.impl.RemoteSettingRepositoryImpl
import com.par9uet.jm.storage.CookieStorage
import com.par9uet.jm.storage.LocalSettingStorage
import com.par9uet.jm.storage.SecureStorage
import com.par9uet.jm.storage.UserLoginInfoStorage
import com.par9uet.jm.storage.UserStorage
import com.par9uet.jm.store.LocalSettingManager
import com.par9uet.jm.store.RemoteSettingManager
import com.par9uet.jm.store.UserManager
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    single { SecureStorage(get()) }
    single { UserStorage(get()) }
    single { CookieStorage(get()) }
    single { LocalSettingStorage(get()) }
    single { UserLoginInfoStorage(get()) }

    single { RemoteSettingRepositoryImpl(get()) } bind RemoteSettingRepository::class
    single { LocalSettingRepositoryImpl() } bind LocalSettingRepository::class

    single { UserManager(get(), get(), get()) }
    single { RemoteSettingManager(get()) }
    single { LocalSettingManager(get()) }
}