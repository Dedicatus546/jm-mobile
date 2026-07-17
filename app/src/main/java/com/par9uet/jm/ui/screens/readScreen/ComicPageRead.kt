package com.par9uet.jm.ui.screens.readScreen

import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.WindowManager
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.par9uet.jm.store.LocalSettingManager
import com.par9uet.jm.ui.components.ComicPicImage
import com.par9uet.jm.ui.viewModel.ComicReadViewModel
import com.par9uet.jm.utils.convertToSlider
import com.par9uet.jm.utils.log
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.getKoin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComicPageRead(
    comicReadViewModel: ComicReadViewModel = koinViewModel(),
    localSettingManager: LocalSettingManager = getKoin().get()
) {
    val activity = LocalActivity.current
    val localSetting by localSettingManager.localSettingState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var currentIndexState by comicReadViewModel.currentIndexState
    val comicPicState by comicReadViewModel.comicPicState.collectAsState()
    val list = comicPicState.data ?: listOf()
    val context = LocalContext.current
    val pagerState = rememberPagerState(currentIndexState) {
        comicReadViewModel.size
    }

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

    // 隐藏工具栏
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.isScrollInProgress }
            .filter { it }
            .collect {
                comicReadViewModel.hideToolBar()
            }
    }

    // pager 带来的变化
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .collect {
                if (currentIndexState != it) {
                    currentIndexState = it
                    comicReadViewModel.decodeIndex(currentIndexState, context)
                }
            }
    }

    // currentIndexState 变化，即 slider 产生的变化
    LaunchedEffect(currentIndexState) {
        if (currentIndexState != pagerState.currentPage) {
            pagerState.scrollToPage(currentIndexState)
        }
    }

    HorizontalPager(
        reverseLayout = localSetting.readMode == "pageReverse",
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val down = awaitFirstDown(
                            requireUnconsumed = false,
                            pass = PointerEventPass.Initial
                        )

                        var upEvent: PointerInputChange?
                        while (true) {
                            val event = awaitPointerEvent(pass = PointerEventPass.Final)
                            val dragEvent = event.changes.firstOrNull()

                            if (dragEvent == null || dragEvent.changedToUp()) {
                                upEvent = dragEvent
                                break
                            }
                        }

                        if (upEvent != null) {
                            val distance = (upEvent.position - down.position).getDistance()

                            if (!upEvent.isConsumed && distance < 10.dp.toPx()) {
                                val screenWidth = size.width
                                val clickX = upEvent.position.x
                                log("click $screenWidth $clickX")

                                when {
                                    clickX < screenWidth / 3 -> {
                                        if (localSetting.readMode == "pageReverse") {
                                            // 在反转翻页下，点击左侧应该切换下一页
                                            comicReadViewModel.next(context)
                                        } else {
                                            comicReadViewModel.prev(context)
                                        }
                                        coroutineScope.launch {
                                            pagerState.scrollToPage(currentIndexState)
                                        }
                                    }

                                    clickX > screenWidth * 2 / 3 -> {
                                        if (localSetting.readMode == "pageReverse") {
                                            // 在反转翻页下，点击右侧应该切换上一页
                                            comicReadViewModel.prev(context)
                                        } else {
                                            comicReadViewModel.next(context)
                                        }
                                        coroutineScope.launch {
                                            pagerState.scrollToPage(currentIndexState)
                                        }
                                    }

                                    else -> {
                                        comicReadViewModel.triggerToolBar()
                                    }
                                }
                            }
                        }
                    }
                }
            },
        state = pagerState
    ) { page ->
        val item = list[page]
        ComicPicImage(
            comicPicImageState = item,
            modifier = Modifier
                .fillMaxSize(),
            contentScale = ContentScale.Fit
        )
    }
}