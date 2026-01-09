package com.par9uet.jm.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import com.par9uet.jm.data.models.ComicPicImageState
import com.par9uet.jm.data.models.ImageResultState
import com.par9uet.jm.store.LocalSettingManager
import com.par9uet.jm.ui.components.ComicPicImage
import com.par9uet.jm.ui.viewModel.ComicReadViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.getKoin

@OptIn(FlowPreview::class)
@Composable
private fun ComicScrollRead(
    list: List<ComicPicImageState>,
    comicReadViewModel: ComicReadViewModel = koinViewModel(),
) {
    val context = LocalContext.current
    val lazyListState = rememberLazyListState()
    LaunchedEffect(lazyListState, list) {
        snapshotFlow { lazyListState.firstVisibleItemIndex }
            .distinctUntilChanged()
            .debounce(200L)
            .collect { index ->
                val item = list.getOrNull(index) ?: return@collect
                comicReadViewModel.decode(item, context)
            }
    }
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

@Composable
private fun ComicPageRead(
    list: List<ComicPicImageState>,
    comicReadViewModel: ComicReadViewModel = koinViewModel(),
) {
    val context = LocalContext.current
    val pagerState = rememberPagerState(initialPage = 0) {
        list.size
    }
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .collect { index ->
                val item = list.getOrNull(index) ?: return@collect
                comicReadViewModel.decode(item, context)
            }
    }
    HorizontalPager(
        modifier = Modifier.fillMaxSize(),
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
    val comicPicState by comicReadViewModel.comicPicState.collectAsState()
    val list = comicPicState.data ?: listOf()
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
            .statusBarsPadding()
    ) {
        if (loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            if (localSetting.readMode == "scroll") {
                ComicScrollRead(list)
            } else {
                ComicPageRead(list)
            }
        }
    }
}