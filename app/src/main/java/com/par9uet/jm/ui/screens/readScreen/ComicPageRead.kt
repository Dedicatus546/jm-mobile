package com.par9uet.jm.ui.screens.readScreen

import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.WindowManager
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.platform.ViewConfiguration
import androidx.compose.ui.unit.dp
import com.par9uet.jm.data.models.ComicPicImageState
import com.par9uet.jm.data.models.ImageResultState
import com.par9uet.jm.store.LocalSettingManager
import com.par9uet.jm.ui.viewModel.ComicReadViewModel
import com.par9uet.jm.utils.convertToSlider
import com.par9uet.jm.utils.log
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import me.saket.telephoto.zoomable.ZoomSpec
import me.saket.telephoto.zoomable.rememberZoomableState
import me.saket.telephoto.zoomable.zoomable
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.getKoin

@Composable
private fun ComicPicImage(
    modifier: Modifier = Modifier,
    comicPicImageState: ComicPicImageState,
    contentScale: ContentScale = ContentScale.FillBounds,
    onClickLeft: () -> Unit,
    onClickRight: () -> Unit,
    onClickCenter: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val imageResult = comicPicImageState.imageResultState

    val retryImageDecode = {
        coroutineScope.launch {
            comicPicImageState.decode(context)
        }
    }

    Box(modifier = modifier) {
        when (imageResult) {
            is ImageResultState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            is ImageResultState.Failure -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(imageResult.reason)
                    TextButton(
                        onClick = {
                            retryImageDecode()
                        }
                    ) {
                        Text("重试")
                    }
                }
            }

            is ImageResultState.Success -> {
                val zoomState = rememberZoomableState(
                    zoomSpec = ZoomSpec(maxZoomFactor = 3f)
                )
                var size by remember { mutableStateOf(Size.Zero) }
                val currentConfig = LocalViewConfiguration.current
                val customViewConfig = remember(currentConfig) {
                    object : ViewConfiguration by currentConfig {
                        // 双击检测改为 150 ms
                        override val doubleTapTimeoutMillis: Long
                            get() = 150L
                    }
                }
                CompositionLocalProvider(LocalViewConfiguration provides customViewConfig) {
                    Image(
                        modifier = Modifier
                            .fillMaxSize()
                            .onSizeChanged {
                                size = Size(it.width.toFloat(), it.height.toFloat())
                            }
                            .zoomable(
                                state = zoomState,
                                onClick = {
                                    log("ComicPicImage", "onClick $it")
                                    val clickX = it.x
                                    when {
                                        clickX < size.width / 3 -> onClickLeft()
                                        clickX > size.width * 2 / 3 -> onClickRight()
                                        else -> onClickCenter()
                                    }
                                },
                            ),
                        contentScale = contentScale,
                        bitmap = imageResult.decodeImageBitmap,
                        contentDescription = "第${comicPicImageState.index}张图片",
                    )
                }
            }
        }
    }
}

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
        state = pagerState,
        modifier = Modifier
            .fillMaxSize()

    ) { page ->
        val item = list[page]
        ComicPicImage(
            comicPicImageState = item,
            modifier = Modifier
                .fillMaxSize(),
            contentScale = ContentScale.Fit,
            onClickLeft = {
                if (localSetting.readMode == "pageReverse") {
                    // 在反转翻页下，点击左侧应该切换下一页
                    comicReadViewModel.next(context)
                } else {
                    comicReadViewModel.prev(context)
                }
                coroutineScope.launch {
                    pagerState.scrollToPage(currentIndexState)
                }
            },
            onClickRight = {
                if (localSetting.readMode == "pageReverse") {
                    // 在反转翻页下，点击右侧应该切换上一页
                    comicReadViewModel.prev(context)
                } else {
                    comicReadViewModel.next(context)
                }
                coroutineScope.launch {
                    pagerState.scrollToPage(currentIndexState)
                }
            },
            onClickCenter = {
                comicReadViewModel.triggerToolBar()
            }
        )
    }
}