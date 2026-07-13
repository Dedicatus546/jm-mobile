package com.par9uet.jm.ui.screens.readScreen

import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
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
import com.par9uet.jm.ui.components.ComicPicImage
import com.par9uet.jm.ui.viewModel.ComicReadViewModel
import com.par9uet.jm.utils.log
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComicPageRead(
    comicReadViewModel: ComicReadViewModel = koinViewModel(),
) {
    val coroutineScope = rememberCoroutineScope()
    var currentIndexState by comicReadViewModel.currentIndexState
    val comicPicState by comicReadViewModel.comicPicState.collectAsState()
    val list = comicPicState.data ?: listOf()
    val context = LocalContext.current
    val pagerState = rememberPagerState(0) {
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
                                        comicReadViewModel.prev(context)
                                        coroutineScope.launch {
                                            pagerState.scrollToPage(currentIndexState)
                                        }
                                    }

                                    clickX > screenWidth * 2 / 3 -> {
                                        comicReadViewModel.next(context)
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