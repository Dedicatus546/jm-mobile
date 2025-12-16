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
import com.par9uet.jm.repository.ComicRepository
import com.par9uet.jm.repository.LocalSettingRepository
import com.par9uet.jm.repository.RemoteSettingRepository
import com.par9uet.jm.repository.UserRepository
import com.par9uet.jm.repository.impl.ComicRepositoryImpl
import com.par9uet.jm.repository.impl.LocalSettingRepositoryImpl
import com.par9uet.jm.repository.impl.RemoteSettingRepositoryImpl
import com.par9uet.jm.repository.impl.UserRepositoryImpl
import com.par9uet.jm.retrofit.Retrofit
import com.par9uet.jm.retrofit.converter.PrimitiveToRequestBodyConverterFactory
import com.par9uet.jm.retrofit.converter.ResponseConverterFactory
import com.par9uet.jm.retrofit.interceptor.TokenInterceptor
import com.par9uet.jm.retrofit.service.ComicService
import com.par9uet.jm.retrofit.service.RemoteSettingService
import com.par9uet.jm.retrofit.service.UserService
import com.par9uet.jm.storage.CookieStorage
import com.par9uet.jm.storage.LocalSettingStorage
import com.par9uet.jm.storage.SecureStorage
import com.par9uet.jm.storage.UserStorage
import com.par9uet.jm.store.UserManager
import com.par9uet.jm.task.startTask.RemoteSettingTask
import com.par9uet.jm.task.startTask.TryAutoLoginTask
import com.par9uet.jm.ui.viewModel.ComicDetailViewModel
import com.par9uet.jm.ui.viewModel.ComicPicImageViewModel
import com.par9uet.jm.ui.viewModel.ComicQuickSearchViewModel
import com.par9uet.jm.ui.viewModel.ComicReadViewModel
import com.par9uet.jm.ui.viewModel.GlobalViewModel
import com.par9uet.jm.ui.viewModel.HomeViewModel
import com.par9uet.jm.ui.viewModel.LocalSettingViewModel
import com.par9uet.jm.ui.viewModel.RemoteSettingViewModel
import com.par9uet.jm.ui.viewModel.UserCollectComicViewModel
import com.par9uet.jm.ui.viewModel.UserHistoryComicViewModel
import com.par9uet.jm.ui.viewModel.UserHistoryCommentViewModel
import com.par9uet.jm.ui.viewModel.UserViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.converter.scalars.ScalarsConverterFactory

val appModule = module {
    single<ComicService> { get<Retrofit>().createService(ComicService::class.java) }
    single<RemoteSettingService> { get<Retrofit>().createService(RemoteSettingService::class.java) }
    single<UserService> { get<Retrofit>().createService(UserService::class.java) }
    single<UserRepository> { UserRepositoryImpl(get()) }
    single<ComicRepository> { ComicRepositoryImpl(get()) }
    single<RemoteSettingRepository> { RemoteSettingRepositoryImpl(get()) }
    single<LocalSettingRepository> { LocalSettingRepositoryImpl() }
    single { TokenInterceptor() }
    single { ResponseConverterFactory() }
    single { PrimitiveToRequestBodyConverterFactory() }
    single { UserStorage(get()) }
    single { CookieStorage(get()) }
    single { LocalSettingStorage(get()) }
    single { UserManager(get(), get()) }
    single { Retrofit(get(), get(), get(), get(), get()) }
    single { SecureStorage(get()) }
    single(named("AsyncImageLoader")) {
        createAsyncImageLoader(get())
    }
    single(named("PicImageLoader")) {
        createPicImageLoader(get())
    }
    single<ScalarsConverterFactory> { ScalarsConverterFactory.create() }

    single { RemoteSettingTask(get()) }
    single { TryAutoLoginTask(get(), get()) }

    viewModel { LocalSettingViewModel() }
    viewModel { HomeViewModel(get()) }
    viewModel { ComicDetailViewModel(get()) }
    viewModel { UserViewModel(get(), get()) }
    viewModel { GlobalViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { UserCollectComicViewModel(get()) }
    viewModel { UserHistoryComicViewModel(get()) }
    viewModel { UserHistoryCommentViewModel(get()) }
    viewModel { ComicReadViewModel(get()) }
    viewModel { ComicPicImageViewModel(get(qualifier = named("PicImageLoader"))) }
    viewModel { ComicQuickSearchViewModel(get()) }
    viewModel { RemoteSettingViewModel(get()) }
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