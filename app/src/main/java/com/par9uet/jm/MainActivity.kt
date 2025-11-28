package com.par9uet.jm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.par9uet.jm.retrofit.LoginCookieJar
import com.par9uet.jm.retrofit.Retrofit
import com.par9uet.jm.retrofit.converter.PrimitiveToRequestBodyConverterFactory
import com.par9uet.jm.retrofit.converter.ResponseConverterFactory
import com.par9uet.jm.retrofit.interceptor.TokenInterceptor
import com.par9uet.jm.retrofit.repository.ComicRepository
import com.par9uet.jm.retrofit.repository.GlobalRepository
import com.par9uet.jm.retrofit.repository.RemoteSettingRepository
import com.par9uet.jm.retrofit.repository.UserRepository
import com.par9uet.jm.storage.SecureStorage
import com.par9uet.jm.viewModel.ComicDetailViewModel
import com.par9uet.jm.viewModel.ComicPicImageViewModel
import com.par9uet.jm.viewModel.ComicReadViewModel
import com.par9uet.jm.viewModel.GlobalViewModel
import com.par9uet.jm.viewModel.HomeViewModel
import com.par9uet.jm.viewModel.LocalSettingViewModel
import com.par9uet.jm.viewModel.SettingViewModel
import com.par9uet.jm.viewModel.UserCollectComicViewModel
import com.par9uet.jm.viewModel.UserHistoryComicViewModel
import com.par9uet.jm.viewModel.UserHistoryCommentViewModel
import com.par9uet.jm.viewModel.UserViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { UserRepository(get()) }
    single { ComicRepository(get()) }
    single { RemoteSettingRepository(get()) }
    single { GlobalRepository(get()) }
    single { TokenInterceptor() }
    single { ResponseConverterFactory() }
    single { PrimitiveToRequestBodyConverterFactory() }
    single { LoginCookieJar(get()) }
    single { Retrofit(get(), get(), get(), get()) }
    single { SecureStorage(get()) }
    viewModel { LocalSettingViewModel(get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { ComicDetailViewModel(get()) }
    viewModel { SettingViewModel(get(), get()) }
    viewModel { UserViewModel(get(), get(), get()) }
    viewModel { GlobalViewModel(get()) }
    viewModel { UserCollectComicViewModel(get()) }
    viewModel { UserHistoryComicViewModel(get()) }
    viewModel { UserHistoryCommentViewModel(get(), get()) }
    viewModel { ComicReadViewModel(get()) }
    viewModel { ComicPicImageViewModel(get()) }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        startKoin {
            androidContext(this@MainActivity)
            modules(appModule)
            modules()
        }

        enableEdgeToEdge()
        setContent {
            App()
        }
    }
}