package com.par9uet.jm.ui.screens.readScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberSliderState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.par9uet.jm.store.LocalSettingManager
import com.par9uet.jm.ui.viewModel.ComicReadViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.getKoin

@OptIn(FlowPreview::class, ExperimentalMaterial3Api::class)
@Composable
fun ComicReadScreen(
    comicId: Int,
    comicReadViewModel: ComicReadViewModel = koinViewModel(),
    localSettingManager: LocalSettingManager = getKoin().get()
) {
    val context = LocalContext.current
    val size = comicReadViewModel.size

    var currentIndexState by comicReadViewModel.currentIndexState

    val localSetting by localSettingManager.localSettingState.collectAsState()

    val comicPicState by comicReadViewModel.comicPicState.collectAsState()
    val loading = comicPicState.isLoading

    LaunchedEffect(Unit) {
        comicReadViewModel.getComicPicList(
            comicId,
            localSettingManager.localSettingState.value.shunt
        ) {
            comicReadViewModel.decodeIndex(0, context)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        if (loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            val lazyListState = rememberLazyListState()
            val pagerState = rememberPagerState(initialPage = 0) {
                size
            }
            val sliderState = rememberSliderState(
                value = currentIndexState.toFloat(),
                steps = size - 2,
                valueRange = 0f..(size - 1).toFloat(),
            )

            LaunchedEffect(sliderState) {
                snapshotFlow { sliderState.value }
                    .debounce { 1000 }
                    .collect {
                        val value = it.toInt()
                        if (currentIndexState != value) {
                            currentIndexState = value
                            pagerState.scrollToPage(value)
                            lazyListState.scrollToItem(value)
                            comicReadViewModel.decodeIndex(currentIndexState, context)
                        }
                    }
            }
            if (localSetting.readMode == "scroll") {
                LaunchedEffect(lazyListState) {
                    snapshotFlow { lazyListState.firstVisibleItemIndex }
                        .distinctUntilChanged()
                        .debounce(1000)
                        .collect {
                            if (currentIndexState != it) {
                                currentIndexState = it
                                sliderState.value = it.toFloat()
                                comicReadViewModel.decodeIndex(currentIndexState, context)
                            }
                        }
                }
                LaunchedEffect(currentIndexState, lazyListState, sliderState) {
                    if (currentIndexState != lazyListState.firstVisibleItemIndex) {
                        lazyListState.scrollToItem(currentIndexState)
                    }
                    if (currentIndexState != sliderState.value.toInt()) {
                        sliderState.value = currentIndexState.toFloat()
                    }
                }
                ComicScrollRead(
                    lazyListState = lazyListState
                )
            } else {
                LaunchedEffect(pagerState) {
                    snapshotFlow { pagerState.currentPage }
                        .collect {
                            if (currentIndexState != it) {
                                currentIndexState = it
                                sliderState.value = it.toFloat()
                                comicReadViewModel.decodeIndex(currentIndexState, context)
                            }
                        }
                }
                LaunchedEffect(currentIndexState, pagerState, sliderState) {
                    if (currentIndexState != pagerState.currentPage) {
                        pagerState.scrollToPage(currentIndexState)
                    }
                    if (currentIndexState != sliderState.value.toInt()) {
                        sliderState.value = currentIndexState.toFloat()
                    }
                }
                ComicPageRead(
                    pagerState = pagerState
                )
            }
            ToolsBar(
                modifier = Modifier.align(Alignment.BottomCenter),
                sliderState = sliderState,
                comicReadViewModel = comicReadViewModel
            )
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
}