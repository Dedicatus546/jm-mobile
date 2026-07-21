package com.par9uet.jm.ui.screens.readScreen

import android.app.Activity
import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.WindowManager
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.par9uet.jm.store.LocalSettingManager
import com.par9uet.jm.ui.components.ErrorTips
import com.par9uet.jm.ui.viewModel.ComicReadViewModel
import com.par9uet.jm.utils.convertToSlider
import com.par9uet.jm.utils.log
import kotlinx.coroutines.FlowPreview
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.getKoin

@OptIn(FlowPreview::class, ExperimentalMaterial3Api::class)
@Composable
fun ComicReadScreen(
    comicId: Int,
    comicReadViewModel: ComicReadViewModel = koinViewModel(),
    localSettingManager: LocalSettingManager = getKoin().get()
) {
    val activity = LocalActivity.current
    val context = LocalContext.current
    val isShowToolbar by comicReadViewModel.isShowToolBar
    val size by comicReadViewModel.sizeState.collectAsState()
    var currentIndexState by comicReadViewModel.currentIndexState
    val localSetting by localSettingManager.localSettingState.collectAsState()
    val comicPicState by comicReadViewModel.comicPicState.collectAsState()

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
        log("comicPageRead", "${localSetting.brightnessFollowSystem}")
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
                        log(
                            "comicPageRead",
                            "update currentSystemBrightness $currentSystemBrightness"
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

    // 获取图片列表并且解码第一张图片
    LaunchedEffect(localSetting.shunt) {
        comicReadViewModel.resetDecodePrefetchCache()
        comicReadViewModel.getComicPicList(
            comicId,
            localSetting.shunt
        )
    }

    LaunchedEffect(Unit) {
        comicReadViewModel.refreshComicPicTrigger.collect {
            comicReadViewModel.decodeIndex(0, context)
        }
    }

    val view = LocalView.current
    val controller = remember(view) {
        val window = (context as? Activity)?.window
        WindowInsetsControllerCompat(window!!, view).apply {
            systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
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

    Box(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.background
            )
            .fillMaxSize()
    ) {
        if (comicPicState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (comicPicState.isError) {
            ErrorTips(
                errorMsg = comicPicState.errorMsg
            ) {
                comicReadViewModel.getComicPicList(
                    comicId,
                    localSettingManager.localSettingState.value.shunt
                )
            }
        } else {
            if (localSetting.readMode == "scroll") {
                ComicScrollRead()
            } else {
                ComicPageRead()
            }
        }
        // 页码显示
        if (localSetting.showPageNumber && comicPicState.isOk) {
            SuggestionChip(
                border = null,
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
                modifier = Modifier
                    .statusBarsPadding()
                    .align(Alignment.TopEnd)
                    .padding(top = 10.dp, end = 10.dp),
                onClick = {},
                label = {
                    Text("${currentIndexState + 1} / $size")
                }
            )
        }
        AnimatedVisibility(
            modifier = Modifier.align(Alignment.BottomCenter),
            visible = isShowToolbar,
            enter = slideInVertically(
                initialOffsetY = { fullHeight -> fullHeight },
                animationSpec = tween(durationMillis = 300)
            ) + fadeIn(),
            exit = slideOutVertically(
                targetOffsetY = { fullHeight -> fullHeight },
                animationSpec = tween(durationMillis = 300)
            ) + fadeOut()
        ) {
            ToolsBar()
        }
        if (localSetting.showComicPageReadTip && localSetting.readMode == "page" || localSetting.showComicScrollReadTip && localSetting.readMode == "scroll") {
            Tip(
                readMode = localSetting.readMode,
            )
            TipCloseButton(
                modifier = Modifier.align(
                    if (localSetting.readMode == "scroll") Alignment.CenterEnd else Alignment.BottomCenter
                ).let {
                    if (localSetting.readMode == "scroll") {
                        it.padding(end = 40.dp)
                    } else {
                        it.padding(bottom = 40.dp)
                    }
                },
                onClick = {
                    if (localSetting.readMode == "scroll") {
                        localSettingManager.closeShowComicScrollReadTip()
                    } else {
                        localSettingManager.closeShowComicPageReadTip()
                    }
                }
            )
        }
    }
}