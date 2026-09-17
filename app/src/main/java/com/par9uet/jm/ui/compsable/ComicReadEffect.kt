package com.par9uet.jm.ui.compsable

import android.app.Activity
import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.WindowManager
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.par9uet.jm.ui.provider.LocalLocalSettingManager
import com.par9uet.jm.ui.viewModel.BaseComicReadViewModel
import com.par9uet.jm.utils.convertToSlider

@Composable
fun ComicReadEffect(
    vm: BaseComicReadViewModel
) {
    val localSettingManager = LocalLocalSettingManager.current
    val activity = LocalActivity.current
    val context = LocalContext.current
    val view = LocalView.current

    val localSetting by localSettingManager.localSettingState.collectAsState()

    // 屏幕常亮
    DisposableEffect(localSetting.noLockScreen) {
        val window = activity?.window ?: return@DisposableEffect onDispose {}
        if (localSetting.noLockScreen) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            return@DisposableEffect onDispose {
                window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
        }
        return@DisposableEffect onDispose {}
    }

    // 亮度设置
    DisposableEffect(localSetting.brightnessFollowSystem) {
        val window = activity?.window ?: return@DisposableEffect onDispose {}

        val resolver = context.contentResolver
        if (localSetting.brightnessFollowSystem) {
            val lp = window.attributes
            lp.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
            window.attributes = lp

            val uri = Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS)

            val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
                override fun onChange(selfChange: Boolean) {
                    try {
                        val currentSystemBrightness = Settings.System.getInt(
                            resolver,
                            Settings.System.SCREEN_BRIGHTNESS
                        )
                        localSettingManager.updateBrightness(convertToSlider(currentSystemBrightness))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            resolver.registerContentObserver(uri, false, observer)

            // 初始化时主动触发一次，确保滑块位置立刻对齐当前系统亮度
            try {
                val initialBrightness =
                    Settings.System.getInt(resolver, Settings.System.SCREEN_BRIGHTNESS)
                localSettingManager.updateBrightness(convertToSlider(initialBrightness))
            } catch (e: Exception) {
                localSettingManager.updateBrightness(.5f)
            }

            onDispose {
                resolver.unregisterContentObserver(observer)
            }
        } else {
            val lp = window.attributes
            lp.screenBrightness = localSetting.brightness
            window.attributes = lp

            onDispose { }
        }
    }

    // 关闭跟随后，滚动了 slider
    LaunchedEffect(localSetting.brightness) {
        val window = activity?.window ?: return@LaunchedEffect
        if (!localSetting.brightnessFollowSystem) {
            val lp = window.attributes
            lp.screenBrightness = localSetting.brightness
            window.attributes = lp
        }
    }

    val controller = remember(view) {
        val window = (context as? Activity)?.window
        WindowInsetsControllerCompat(window!!, view).apply {
            systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
    val isShowToolbar by vm.isShowToolBar.collectAsState()
    LaunchedEffect(isShowToolbar) {
        if (isShowToolbar) {
            controller.show(WindowInsetsCompat.Type.statusBars())
        } else {
            controller.hide(WindowInsetsCompat.Type.statusBars())
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            controller.show(WindowInsetsCompat.Type.statusBars())
        }
    }
}