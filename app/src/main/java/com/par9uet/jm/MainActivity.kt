package com.par9uet.jm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.unit.Dp
import coil3.ImageLoader
import com.par9uet.jm.store.CacheManager
import com.par9uet.jm.store.LocalSettingManager
import com.par9uet.jm.store.RemoteSettingManager
import com.par9uet.jm.store.ToastManager
import com.par9uet.jm.store.UserManager
import com.par9uet.jm.ui.provider.LocalCacheManager
import com.par9uet.jm.ui.provider.LocalImageLoader
import com.par9uet.jm.ui.provider.LocalLocalSettingManager
import com.par9uet.jm.ui.provider.LocalMainActivity
import com.par9uet.jm.ui.provider.LocalRemoteSettingManager
import com.par9uet.jm.ui.provider.LocalToastManager
import com.par9uet.jm.ui.provider.LocalUserManager
import com.par9uet.jm.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var toastManager: ToastManager

    @Inject
    lateinit var userManager: UserManager

    @Inject
    lateinit var remoteSettingManager: RemoteSettingManager

    @Inject
    lateinit var localSettingManager: LocalSettingManager

    @Inject
    lateinit var cacheManager: CacheManager

    @Inject
    lateinit var imageLoader: ImageLoader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            CompositionLocalProvider(LocalCacheManager provides cacheManager) {
                CompositionLocalProvider(LocalMainActivity provides this) {
                    CompositionLocalProvider(LocalLocalSettingManager provides localSettingManager) {
                        CompositionLocalProvider(LocalRemoteSettingManager provides remoteSettingManager) {
                            CompositionLocalProvider(LocalImageLoader provides imageLoader) {
                                CompositionLocalProvider(LocalUserManager provides userManager) {
                                    CompositionLocalProvider(LocalToastManager provides toastManager) {
                                        CompositionLocalProvider(
                                            // Deprecated starting on Version 1.3.0-alpha04
                                            // https://developer.android.com/jetpack/androidx/releases/compose-material3#1.3.0-alpha04
                                            // LocalMinimumInteractiveComponentEnforcement provides false
                                            // 去除 m3 默认的最小高度
                                            LocalMinimumInteractiveComponentSize provides Dp.Unspecified
                                        ) {
                                            AppTheme {
                                                App()
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}