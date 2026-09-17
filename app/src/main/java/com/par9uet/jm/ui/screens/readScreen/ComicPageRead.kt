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
import androidx.compose.ui.unit.dp
import com.par9uet.jm.data.models.ReadMode
import com.par9uet.jm.ui.provider.LocalLocalSettingManager
import com.par9uet.jm.ui.viewModel.ComicReadViewModel
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import me.saket.telephoto.zoomable.EnabledZoomGestures
import me.saket.telephoto.zoomable.ZoomSpec
import me.saket.telephoto.zoomable.rememberZoomableState
import me.saket.telephoto.zoomable.zoomable

@Composable
private fun ComicPicImage(
    modifier: Modifier = Modifier,
    comicPicImage: ComicPicImage,
    contentScale: ContentScale = ContentScale.FillBounds,
    onClickLeft: suspend () -> Unit,
    onClickRight: suspend () -> Unit,
    onClickCenter: suspend () -> Unit,
) {
    val localSettingManager = LocalLocalSettingManager.current
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val imageResult by comicPicImage.imageResult.collectAsState()
    val localSetting by localSettingManager.localSettingState.collectAsState()

    val retryImageDecode = {
        coroutineScope.launch {
            comicPicImage.decode(context)
        }
    }

    Box(modifier = modifier) {
        when (val imageResultCopy = imageResult) {
            is ImageResult.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            is ImageResult.Failure -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(imageResultCopy.reason)
                    TextButton(
                        onClick = {
                            retryImageDecode()
                        }
                    ) {
                        Text("重试")
                    }
                }
            }

            is ImageResult.Success -> {
                val zoomState = rememberZoomableState(
                    zoomSpec = ZoomSpec(maxZoomFactor = 3f)
                )
                var size by remember { mutableStateOf(Size.Zero) }
                LaunchedEffect(localSetting.supportZoom) {
                    if (!localSetting.supportZoom) {
                        zoomState.resetZoom()
                    }
                }
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
                    bitmap = imageResultCopy.decodeImageBitmap,
                    contentDescription = "第${comicPicImage.page}张图片",
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComicPageRead(
    comicReadViewModel: ComicReadViewModel
) {
    val localSettingManager = LocalLocalSettingManager.current
    val context = LocalContext.current

    val localSetting by localSettingManager.localSettingState.collectAsState()
    val currentIndex by comicReadViewModel.currentIndex.collectAsState()
    val comicPicState by comicReadViewModel.comicPicState.collectAsState()
    val list = comicPicState.data ?: listOf()
    val pagerState = rememberPagerState(currentIndex) {
        comicReadViewModel.sizeState.value
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
                if (currentIndex != it) {
                    comicReadViewModel.updateCurrentIndex(it)
                    comicReadViewModel.decodeIndex(currentIndex, context)
                }
            }
    }

    // currentIndexState 变化，即 slider 产生的变化
    LaunchedEffect(currentIndex) {
        if (currentIndex != pagerState.currentPage) {
            pagerState.scrollToPage(currentIndex)
        }
    }

    HorizontalPager(
        reverseLayout = localSetting.readMode == ReadMode.PAGE_RIGHT,
        state = pagerState,
        modifier = Modifier
            .fillMaxSize()

    ) { page ->
        val item = list[page]
        ComicPicImage(
            comicPicImage = item,
            modifier = Modifier
                .fillMaxSize(),
            contentScale = ContentScale.Fit,
            onClickLeft = {
                if (localSetting.readMode == ReadMode.PAGE_RIGHT) {
                    // 在反转翻页下，点击左侧应该切换下一页
                    comicReadViewModel.next()
                } else {
                    comicReadViewModel.prev()
                }
                pagerState.scrollToPage(currentIndex)
            },
            onClickRight = {
                if (localSetting.readMode == ReadMode.PAGE_RIGHT) {
                    // 在反转翻页下，点击右侧应该切换上一页
                    comicReadViewModel.prev()
                } else {
                    comicReadViewModel.next()
                }
                pagerState.scrollToPage(currentIndex)
            },
            onClickCenter = {
                comicReadViewModel.triggerToolBar()
            }
        )
    }
}