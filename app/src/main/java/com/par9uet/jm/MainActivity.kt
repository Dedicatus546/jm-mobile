package com.par9uet.jm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.unit.Dp
import com.par9uet.jm.di.appModule
import com.par9uet.jm.di.coilModule
import com.par9uet.jm.di.comicModule
import com.par9uet.jm.di.retrofitModule
import com.par9uet.jm.di.userModule
import com.par9uet.jm.ui.theme.AppTheme
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

val moduleList = listOf(
    appModule,
    coilModule,
    comicModule,
    retrofitModule,
    userModule,
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startKoin {
            androidContext(this@MainActivity)
            modules(moduleList)
        }

        enableEdgeToEdge()
        setContent {
            AppTheme {
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
}