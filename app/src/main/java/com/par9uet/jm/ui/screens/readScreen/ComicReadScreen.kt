package com.par9uet.jm.ui.screens.readScreen

import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberSliderState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.par9uet.jm.data.models.ImageResultState
import com.par9uet.jm.store.LocalSettingManager
import com.par9uet.jm.ui.components.ComicPicImage
import com.par9uet.jm.ui.viewModel.ComicReadViewModel
import com.par9uet.jm.utils.log
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.getKoin

@OptIn(FlowPreview::class, ExperimentalMaterial3Api::class)
@Composable
private fun ComicScrollRead(
    comicReadViewModel: ComicReadViewModel = koinViewModel(),
) {
    val comicPicState by comicReadViewModel.comicPicState.collectAsState()
    val currentIndexState by comicReadViewModel.currentIndexState
    val list = comicPicState.data ?: listOf()
    val context = LocalContext.current
    val lazyListState = rememberLazyListState()
    LaunchedEffect(currentIndexState, lazyListState) {
        if (currentIndexState == lazyListState.firstVisibleItemIndex) {
            return@LaunchedEffect
        }
        lazyListState.animateScrollToItem(currentIndexState)
    }
    LaunchedEffect(lazyListState, list) {
        snapshotFlow { lazyListState.firstVisibleItemIndex }
            .distinctUntilChanged()
            .debounce(200L)
            .collect { index ->
                comicReadViewModel.changeIndex(context)
            }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        // 1. 在 Initial 阶段观察按下，不消耗事件，确保 Pager 能收到
                        val down =
                            awaitFirstDown(
                                requireUnconsumed = false,
                                pass = PointerEventPass.Initial
                            )
                        // 2. 等待抬起
                        val up = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                        // 3. 判定逻辑：只有在没被消费（说明不是滑动）且距离很短时触发
                        if (up != null && !up.isConsumed) {
                            val distance = (up.position - down.position).getDistance()
                            if (distance < 10.dp.toPx()) {
                                // --- 获取点击位置 ---
                                val screenHeight = size.height
                                val clickY = up.position.y

                                when {
                                    clickY < screenHeight / 3 -> {
                                        comicReadViewModel.prevIndex(context)
                                    }

                                    clickY > screenHeight * 2 / 3 -> {
                                        comicReadViewModel.nextIndex(context)
                                    }

                                    else -> {
                                        log("点击中间：菜单")
                                    }
                                }
                            }
                        }
                    }
                }
            }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ComicPageRead(
    comicReadViewModel: ComicReadViewModel = koinViewModel(),
) {
    var currentIndexState by comicReadViewModel.currentIndexState
    val comicPicState by comicReadViewModel.comicPicState.collectAsState()
    val list = comicPicState.data ?: listOf()
    val context = LocalContext.current
    val pagerState = rememberPagerState(initialPage = 0) {
        list.size
    }
    val sliderState = rememberSliderState(
        value = currentIndexState.toFloat(),
        steps = list.size - 2,
        valueRange = 0f..list.size.toFloat(),
    )
    LaunchedEffect(currentIndexState) {
        comicReadViewModel.changeIndex(context)
        sliderState.value = currentIndexState.toFloat()
        pagerState.animateScrollToPage(currentIndexState)
    }
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.isScrollInProgress to pagerState.currentPage }
            .filter { !it.first }
            .collect { pair ->
                currentIndexState = pair.second
            }
    }
    HorizontalPager(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        // 1. 在 Initial 阶段观察按下，不消耗事件，确保 Pager 能收到
                        val down =
                            awaitFirstDown(
                                requireUnconsumed = false,
                                pass = PointerEventPass.Initial
                            )
                        // 2. 等待抬起
                        val up = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                        // 3. 判定逻辑：只有在没被消费（说明不是滑动）且距离很短时触发
                        if (up != null && !up.isConsumed) {
                            val distance = (up.position - down.position).getDistance()
                            if (distance < 10.dp.toPx()) {
                                // --- 获取点击位置 ---
                                val screenWidth = size.width
                                val clickX = up.position.x

                                when {
                                    clickX < screenWidth / 3 -> {
                                        comicReadViewModel.prevIndex(context)
                                    }

                                    clickX > screenWidth * 2 / 3 -> {
                                        comicReadViewModel.nextIndex(context)
                                    }

                                    else -> {
                                        log("点击中间：菜单")
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


@OptIn(FlowPreview::class)
@Composable
fun ComicReadScreen(
    comicId: Int,
    comicReadViewModel: ComicReadViewModel = koinViewModel(),
    localSettingManager: LocalSettingManager = getKoin().get()
) {
    var showTip by remember { mutableStateOf(true) }
    val comicPicState by comicReadViewModel.comicPicState.collectAsState()
    val loading = comicPicState.isLoading
    val localSetting by localSettingManager.localSettingState.collectAsState()

    LaunchedEffect(Unit) {
        comicReadViewModel.getComicPicList(
            comicId,
            localSettingManager.localSettingState.value.shunt
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        if (loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            if (localSetting.readMode == "scroll") {
                ComicScrollRead()
            } else {
                ComicPageRead()
            }
            ToolsBar(
                modifier = Modifier.align(Alignment.BottomCenter),
                comicReadViewModel = comicReadViewModel
            )
            if (showTip) {
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
                        showTip = false
                    }
                )
            }
        }
    }
}