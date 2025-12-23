package com.par9uet.jm.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.par9uet.jm.ui.components.Comic
import com.par9uet.jm.ui.components.ComicSkeleton
import com.par9uet.jm.ui.components.TabSkeleton
import com.par9uet.jm.ui.state.rememberTabIndexState
import com.par9uet.jm.ui.viewModel.ComicViewModel
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinActivityViewModel

@Composable
private fun HomeSkeleton() {
    val fakeTabSize = 6
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
                .height(48.dp)
                .horizontalScroll(rememberScrollState()), // 开启水平滚动
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (index in 0 until fakeTabSize) {
                key(index) {
                    TabSkeleton(index)
                }
            }
        }
        HorizontalDivider()
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            maxItemsInEachRow = 3,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.Top)
        ) {
            for (i in 0 until 18) {
                key(i) {
                    ComicSkeleton(
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun HomeScreen(
    comicViewModel: ComicViewModel = koinActivityViewModel()
) {
    val homeComicState by comicViewModel.homeComicState.collectAsState()
    LaunchedEffect(Unit) {
        if (homeComicState.list.isNotEmpty()) {
            return@LaunchedEffect
        }
        comicViewModel.getHomeComic()
    }
    if (homeComicState.list.isEmpty() && homeComicState.isLoading) {
        HomeSkeleton()
        return
    }

    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val selectedTabIndexState = rememberTabIndexState()
    val pagerState = rememberPagerState(initialPage = 0) {
        homeComicState.list.size
    }
    val onTabClick: (index: Int) -> Unit = {
        selectedTabIndexState.value = it
        coroutineScope.launch {
            pagerState.animateScrollToPage(selectedTabIndexState.value)
        }
    }
    LaunchedEffect(pagerState.currentPage) {
        selectedTabIndexState.value = pagerState.currentPage
    }
    Column {
        PrimaryScrollableTabRow(
            selectedTabIndex = selectedTabIndexState.value,
            edgePadding = 0.dp,
            scrollState = scrollState
        ) {
            homeComicState.list.forEachIndexed { index, item ->
                key(item.id) {
                    Tab(
                        selected = selectedTabIndexState.value == index,
                        onClick = {
                            onTabClick(index)
                        },
                        text = {
                            Text(
                                text = item.title,
                                maxLines = 1,
                            )
                        }
                    )
                }
            }
        }
        HorizontalPager(
            modifier = Modifier.weight(1f),
            state = pagerState
        ) { page ->
            val comicList = homeComicState.list.getOrNull(page)?.list ?: listOf()
            PullToRefreshBox(
                isRefreshing = homeComicState.isLoading,
                onRefresh = {
                    comicViewModel.getHomeComic()
                }
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.Top),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(10.dp)
                ) {
                    items(
                        items = comicList,
                        key = { it.id },
                    ) {
                        Comic(it)
                    }
                }
            }
        }
    }
}