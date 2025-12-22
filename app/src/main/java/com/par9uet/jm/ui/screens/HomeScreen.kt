package com.par9uet.jm.ui.screens

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.par9uet.jm.ui.components.Comic
import com.par9uet.jm.ui.components.PullRefreshAndLoadMoreGrid
import com.par9uet.jm.ui.state.rememberTabIndexState
import com.par9uet.jm.ui.viewModel.ComicViewModel
import org.koin.compose.viewmodel.koinActivityViewModel

@Composable
fun HomeScreen(
    comicViewModel: ComicViewModel = koinActivityViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val lazyPagingItems = comicViewModel.promoteComicPager.collectAsLazyPagingItems()
    val refreshState = rememberPullToRefreshState()
    val isRefreshing = lazyPagingItems.loadState.refresh is LoadState.Loading
    val selectedTabIndexState = rememberTabIndexState()
    val scrollState = rememberScrollState()

    val onTabClick: (index: Int) -> Unit = {
        selectedTabIndexState.value = it
//        coroutineScope.launch {
//            pagerState.animateScrollToPage(selectedTabIndexState.value)
//        }
    }

    // 当 Paging 刷新结束时，停止 PullToRefresh 动画
    LaunchedEffect(isRefreshing) {
        if (!isRefreshing) {
            refreshState.endRefresh()
        }
    }
    if (lazyPagingItems.itemCount > 0) {
        PrimaryScrollableTabRow(
            selectedTabIndex = selectedTabIndexState.value,
            edgePadding = 0.dp,
            scrollState = scrollState
        ) {
            for (index in 0 until lazyPagingItems.itemCount) {
                val item = lazyPagingItems[index]
                if (item != null) {
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
        }
    }
    HorizontalPager(
        state = pagerState
    ) { page ->
        val comicList = promoteComicState.list.getOrNull(page)?.list ?: listOf()
        PullRefreshAndLoadMoreGrid(
            list = comicList,
            key = { item -> item.id },
            isRefreshing = promoteComicState.isLoading,
            onRefresh = {
                comicViewModel.getPromoteComicList()
            },
            isMoreLoading = false,
            hasMore = false,
            showLoadMore = false,
            columns = GridCells.Fixed(3),
        ) { comic ->
            Comic(comic)
        }
    }
}