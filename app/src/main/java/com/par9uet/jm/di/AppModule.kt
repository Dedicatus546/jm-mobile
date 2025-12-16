package com.par9uet.jm.di

import com.par9uet.jm.storage.CookieStorage
import com.par9uet.jm.storage.SecureStorage
import com.par9uet.jm.storage.UserStorage
import com.par9uet.jm.store.UserManager
import org.koin.dsl.module

val appModule = module {
    single { SecureStorage(get()) }
    single { UserStorage(get()) }
    single { CookieStorage(get()) }
    single { UserManager(get(), get()) }
}