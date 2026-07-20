package com.par9uet.jm.ui.screens.readScreen

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
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import me.saket.telephoto.zoomable.EnabledZoomGestures
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
    onClickLeft: suspend () -> Unit,
    onClickRight: suspend () -> Unit,
    onClickCenter: suspend () -> Unit,
    localSettingManager: LocalSettingManager = getKoin().get()
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val imageResult = comicPicImageState.imageResultState
    val localSetting by localSettingManager.localSettingState.collectAsState()

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
                LaunchedEffect(localSetting.supportZoom) {
                    if (!localSetting.supportZoom) {
                        zoomState.resetZoom()
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
                                gestures = if (localSetting.supportZoom) EnabledZoomGestures.ZoomAndPan else EnabledZoomGestures.None,
                                onClick = {
                                    val clickX = it.x
                                    when {
                                        clickX < size.width / 3 -> {
                                            coroutineScope.launch {
                                                if (zoomState.contentTransformation.scale.scaleX != 1.0f) {
                                                    zoomState.resetZoom()
                                                }
                                                onClickLeft()
                                            }

                                        }

                                        clickX > size.width * 2 / 3 -> {
                                            coroutineScope.launch {
                                                if (zoomState.contentTransformation.scale.scaleX != 1.0f) {
                                                    zoomState.resetZoom()
                                                }
                                                onClickRight()
                                            }
                                        }

                                        else -> {
                                            coroutineScope.launch {
                                                onClickCenter()
                                            }
                                        }
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

    val localSetting by localSettingManager.localSettingState.collectAsState()
    var currentIndexState by comicReadViewModel.currentIndexState
    val comicPicState by comicReadViewModel.comicPicState.collectAsState()
    val list = comicPicState.data ?: listOf()
    val context = LocalContext.current
    val pagerState = rememberPagerState(currentIndexState) {
        comicReadViewModel.size
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
                pagerState.scrollToPage(currentIndexState)
            },
            onClickRight = {
                if (localSetting.readMode == "pageReverse") {
                    // 在反转翻页下，点击右侧应该切换上一页
                    comicReadViewModel.prev(context)
                } else {
                    comicReadViewModel.next(context)
                }
                pagerState.scrollToPage(currentIndexState)
            },
            onClickCenter = {
                comicReadViewModel.triggerToolBar()
            }
        )
    }
}