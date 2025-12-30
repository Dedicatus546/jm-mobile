package com.par9uet.jm.di

import com.par9uet.jm.repository.LocalSettingRepository
import com.par9uet.jm.repository.RemoteSettingRepository
import com.par9uet.jm.repository.impl.LocalSettingRepositoryImpl
import com.par9uet.jm.repository.impl.RemoteSettingRepositoryImpl
import com.par9uet.jm.storage.CookieStorage
import com.par9uet.jm.storage.HistorySearchStorage
import com.par9uet.jm.storage.LocalSettingStorage
import com.par9uet.jm.storage.SecureStorage
import com.par9uet.jm.storage.UserStorage
import com.par9uet.jm.store.HistorySearchManager
import com.par9uet.jm.store.LocalSettingManager
import com.par9uet.jm.store.RemoteSettingManager
import com.par9uet.jm.store.ToastManager
import com.par9uet.jm.store.UserManager
import com.par9uet.jm.task.AppInitTask
import com.par9uet.jm.ui.viewModel.GlobalViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    single { SecureStorage(get()) }
    single { UserStorage(get()) }
    single { CookieStorage(get()) }
    single { LocalSettingStorage(get()) }
    single { HistorySearchStorage(get()) }

    single { RemoteSettingRepositoryImpl(get()) } bind RemoteSettingRepository::class
    single { LocalSettingRepositoryImpl() } bind LocalSettingRepository::class

    single { UserManager(get(), get(), get(), get()) } bind AppInitTask::class
    single { RemoteSettingManager(get()) } bind AppInitTask::class
    single { LocalSettingManager(get()) } bind AppInitTask::class
    single { HistorySearchManager(get()) } bind AppInitTask::class
    single { ToastManager() }

    viewModel { GlobalViewModel(getAll()) }
}