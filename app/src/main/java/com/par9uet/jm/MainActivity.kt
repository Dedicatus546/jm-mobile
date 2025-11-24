package com.par9uet.jm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.par9uet.jm.retrofit.repository.ComicRepository
import com.par9uet.jm.retrofit.repository.GlobalRepository
import com.par9uet.jm.retrofit.repository.SettingRepository
import com.par9uet.jm.retrofit.repository.UserRepository
import com.par9uet.jm.storage.SecureStorage
import com.par9uet.jm.viewModel.ComicDetailViewModel
import com.par9uet.jm.viewModel.GlobalViewModel
import com.par9uet.jm.viewModel.UserCollectComicViewModel
import com.par9uet.jm.viewModel.SettingViewModel
import com.par9uet.jm.viewModel.UserHistoryComicViewModel
import com.par9uet.jm.viewModel.UserHistoryCommentViewModel
import com.par9uet.jm.viewModel.UserViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single {
        UserRepository()
    }
    single {
        ComicRepository()
    }
    single {
        SettingRepository()
    }
    single {
        GlobalRepository()
    }
    single {
        SecureStorage(get())
    }
    viewModel {
        ComicDetailViewModel(get())
    }
    viewModel {
        SettingViewModel(get(), get())
    }
    viewModel {
        UserViewModel(get(), get())
    }
    viewModel {
        GlobalViewModel(get())
    }
    viewModel {
        UserCollectComicViewModel(get())
    }
    viewModel {
        UserHistoryComicViewModel(get())
    }
    viewModel {
        UserHistoryCommentViewModel(get(), get())
    }
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
            App()
        }
    }
}