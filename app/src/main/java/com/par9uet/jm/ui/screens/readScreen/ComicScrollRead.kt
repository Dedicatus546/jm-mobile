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
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import me.saket.telephoto.zoomable.EnabledZoomGestures
import me.saket.telephoto.zoomable.ZoomSpec
import me.saket.telephoto.zoomable.rememberZoomableState
import me.saket.telephoto.zoomable.zoomable
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.getKoin
import kotlin.time.Duration.Companion.milliseconds

@Composable
private fun ComicPicImage(
    modifier: Modifier = Modifier,
    comicPicImageState: ComicPicImageState,
    contentScale: ContentScale = ContentScale.FillBounds,
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
                Image(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentScale = contentScale,
                    bitmap = imageResult.decodeImageBitmap,
                    contentDescription = "第${comicPicImageState.index}张图片",
                )
            }
        }
    }
}

@OptIn(FlowPreview::class, ExperimentalMaterial3Api::class)
@Composable
fun ComicScrollRead(
    comicReadViewModel: ComicReadViewModel = koinViewModel(),
    localSettingManager: LocalSettingManager = getKoin().get()
) {
    val coroutineScope = rememberCoroutineScope()
    var currentIndexState by comicReadViewModel.currentIndexState
    val comicPicState by comicReadViewModel.comicPicState.collectAsState()
    val list = comicPicState.data ?: listOf()
    val context = LocalContext.current
    val lazyListState = rememberLazyListState(
        initialFirstVisibleItemIndex = currentIndexState
    )
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
                if (currentIndexState != it) {
                    currentIndexState = it
                    comicReadViewModel.decodeIndex(currentIndexState, context)
                }
            }
    }

    // currentIndexState 变化，即 slider 产生的变化
    LaunchedEffect(currentIndexState) {
        if (currentIndexState != lazyListState.firstVisibleItemIndex) {
            lazyListState.scrollToItem(currentIndexState)
        }
    }

    val currentConfig = LocalViewConfiguration.current
    val customViewConfig = remember(currentConfig) {
        object : ViewConfiguration by currentConfig {
            // 双击检测改为 150 ms
            override val doubleTapTimeoutMillis: Long
                get() = 150L
        }
    }
    CompositionLocalProvider(LocalViewConfiguration provides customViewConfig) {
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
                                    if (lazyListState.firstVisibleItemIndex != currentIndexState) {
                                        currentIndexState = lazyListState.firstVisibleItemIndex
                                        comicReadViewModel.decodeIndex(currentIndexState, context)
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
                                    if (lazyListState.firstVisibleItemIndex != currentIndexState) {
                                        currentIndexState = lazyListState.firstVisibleItemIndex
                                        comicReadViewModel.decodeIndex(currentIndexState, context)
                                    }
                                }
                            }

                            else -> {
                                coroutineScope.launch {
                                    comicReadViewModel.triggerToolBar()
                                }
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
                        comicPicImageState = it,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(
                                when (val state = it.imageResultState) {
                                    is ImageResultState.Success -> {
                                        state.decodeImageAspectRatio
                                    }

                                    else -> {
                                        9f / 16
                                    }
                                }
                            )
                    )
                }
            }
        }
    }
}