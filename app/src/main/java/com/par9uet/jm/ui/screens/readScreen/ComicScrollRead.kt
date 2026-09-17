package com.par9uet.jm.ui.screens.readScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import com.par9uet.jm.ui.provider.LocalLocalSettingManager
import com.par9uet.jm.ui.viewModel.ComicReadViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import me.saket.telephoto.zoomable.EnabledZoomGestures
import me.saket.telephoto.zoomable.ZoomSpec
import me.saket.telephoto.zoomable.rememberZoomableState
import me.saket.telephoto.zoomable.zoomable
import kotlin.time.Duration.Companion.milliseconds

@Composable
private fun ComicPicImage(
    modifier: Modifier = Modifier,
    comicPicImage: ComicPicImage,
    contentScale: ContentScale = ContentScale.FillBounds,
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val imageResult by comicPicImage.imageResult.collectAsState()

    val retryImageDecode = {
        coroutineScope.launch {
            comicPicImage.decode(context)
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(
                when (val imageResultCopy = imageResult) {
                    is ImageResult.Success -> imageResultCopy.decodeImageAspectRatio
                    else -> 9f / 16
                }
            )
    ) {
        // by 对象无法智能转换，通过重新命名变量可以避免这种情况
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
                Image(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentScale = contentScale,
                    bitmap = imageResultCopy.decodeImageBitmap,
                    contentDescription = "第${comicPicImage.page}张图片",
                )
            }
        }
    }
}

@OptIn(FlowPreview::class, ExperimentalMaterial3Api::class)
@Composable
fun ComicScrollRead(
    comicReadViewModel: ComicReadViewModel
) {
    val localSettingManager = LocalLocalSettingManager.current
    val context = LocalContext.current

    val coroutineScope = rememberCoroutineScope()
    val currentIndex by comicReadViewModel.currentIndex.collectAsState()
    val lazyListState = rememberLazyListState(
        initialFirstVisibleItemIndex = currentIndex
    )
    val comicPicState by comicReadViewModel.comicPicState.collectAsState()
    val list = comicPicState.data ?: listOf()
    val localSetting by localSettingManager.localSettingState.collectAsState()

    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.isScrollInProgress }
            .filter { it }
            .collect {
                comicReadViewModel.hideToolBar()
            }
    }

    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.firstVisibleItemIndex }
            .distinctUntilChanged()
            .debounce(1000.milliseconds)
            .collect {
                if (currentIndex != it) {
                    comicReadViewModel.updateCurrentIndex(it)
                    comicReadViewModel.decodeIndex(currentIndex, context)
                }
            }
    }

    // currentIndexState 变化，即 slider 产生的变化
    LaunchedEffect(currentIndex) {
        if (currentIndex != lazyListState.firstVisibleItemIndex) {
            lazyListState.scrollToItem(currentIndex)
        }
    }
    val zoomState = rememberZoomableState(
        zoomSpec = ZoomSpec(maxZoomFactor = 3f)
    )
    var size by remember { mutableStateOf(Size.Zero) }
    LaunchedEffect(localSetting.supportZoom) {
        if (!localSetting.supportZoom) {
            zoomState.resetZoom()
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged {
                size = Size(it.width.toFloat(), it.height.toFloat())
            }
            .zoomable(
                state = zoomState,
                gestures = if (localSetting.supportZoom) EnabledZoomGestures.ZoomAndPan else EnabledZoomGestures.None,
                onClick = {
                    val clickY = it.y
                    when {
                        clickY < size.height / 3 -> {
                            coroutineScope.launch {
                                if (zoomState.contentTransformation.scale.scaleX != 1.0f) {
                                    zoomState.resetZoom()
                                }
                                comicReadViewModel.hideToolBar()
                                lazyListState.scrollBy(-size.height)
                                if (lazyListState.firstVisibleItemIndex != currentIndex) {
                                    comicReadViewModel.updateCurrentIndex(lazyListState.firstVisibleItemIndex)
                                    comicReadViewModel.decodeIndex(currentIndex, context)
                                }
                            }
                        }

                        clickY > size.height * 2 / 3 -> {
                            coroutineScope.launch {
                                if (zoomState.contentTransformation.scale.scaleX != 1.0f) {
                                    zoomState.resetZoom()
                                }
                                comicReadViewModel.hideToolBar()
                                lazyListState.scrollBy(size.height)
                                if (lazyListState.firstVisibleItemIndex != currentIndex) {
                                    comicReadViewModel.updateCurrentIndex(lazyListState.firstVisibleItemIndex)
                                    comicReadViewModel.decodeIndex(currentIndex, context)
                                }
                            }
                        }

                        else -> {
                            comicReadViewModel.triggerToolBar()
                        }
                    }
                }
            )
    ) {
        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .fillMaxSize()
        ) {
            items(list, key = {
                "${it.comicId}_${it.originSrc}"
            }) {
                ComicPicImage(
                    comicPicImage = it,
                )
            }
        }

    }
}