package com.par9uet.jm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.unit.Dp
import com.par9uet.jm.coil.createAsyncImageLoader
import com.par9uet.jm.coil.createPicImageLoader
import com.par9uet.jm.retrofit.LoginCookieJar
import com.par9uet.jm.retrofit.Retrofit
import com.par9uet.jm.retrofit.converter.PrimitiveToRequestBodyConverterFactory
import com.par9uet.jm.retrofit.converter.ResponseConverterFactory
import com.par9uet.jm.retrofit.interceptor.TokenInterceptor
import com.par9uet.jm.retrofit.repository.ComicRepository
import com.par9uet.jm.retrofit.repository.LocalSettingRepository
import com.par9uet.jm.retrofit.repository.RemoteSettingRepository
import com.par9uet.jm.retrofit.repository.UserRepository
import com.par9uet.jm.storage.SecureStorage
import com.par9uet.jm.ui.viewModel.ComicDetailViewModel
import com.par9uet.jm.ui.viewModel.ComicPicImageViewModel
import com.par9uet.jm.ui.viewModel.ComicQuickSearchViewModel
import com.par9uet.jm.ui.viewModel.ComicReadViewModel
import com.par9uet.jm.ui.viewModel.GlobalViewModel
import com.par9uet.jm.ui.viewModel.HomeViewModel
import com.par9uet.jm.ui.viewModel.LocalSettingViewModel
import com.par9uet.jm.ui.viewModel.LoginViewModel
import com.par9uet.jm.ui.viewModel.UserCollectComicViewModel
import com.par9uet.jm.ui.viewModel.UserHistoryComicViewModel
import com.par9uet.jm.ui.viewModel.UserHistoryCommentViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule = module {
    single { UserRepository(get()) }
    single { ComicRepository(get()) }
    single { RemoteSettingRepository(get()) }
    single { TokenInterceptor() }
    single { ResponseConverterFactory() }
    single { PrimitiveToRequestBodyConverterFactory() }
    single { LoginCookieJar(get()) }
    single { Retrofit(get(), get(), get(), get()) }
    single { SecureStorage(get()) }
    single(named("AsyncImageLoader")) {
        createAsyncImageLoader(get())
    }
    single(named("PicImageLoader")) {
        createPicImageLoader(get())
    }
    single { LocalSettingRepository() }
    viewModel { LocalSettingViewModel() }
    viewModel { HomeViewModel(get()) }
    viewModel { ComicDetailViewModel(get()) }
    viewModel { LoginViewModel(get(), get()) }
    viewModel { GlobalViewModel(get(), get(), get(), get(), get()) }
    viewModel { UserCollectComicViewModel(get()) }
    viewModel { UserHistoryComicViewModel(get()) }
    viewModel { UserHistoryCommentViewModel(get()) }
    viewModel { ComicReadViewModel(get()) }
    viewModel { ComicPicImageViewModel(get(qualifier = named("PicImageLoader"))) }
    viewModel { ComicQuickSearchViewModel(get()) }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startKoin {
            androidContext(this@MainActivity)
            modules(appModule)
            modules()
        }

        enableEdgeToEdge()
        setContent {
            CompositionLocalProvider(
                // Deprecated starting on Version 1.3.0-alpha04
                // https://developer.android.com/jetpack/androidx/releases/compose-material3#1.3.0-alpha04
                // LocalMinimumInteractiveComponentEnforcement provides false
                // 去除 m3 默认的最小高度
                LocalMinimumInteractiveComponentSize provides Dp.Unspecified
            ) {
                App()
            }
        }
    }
}