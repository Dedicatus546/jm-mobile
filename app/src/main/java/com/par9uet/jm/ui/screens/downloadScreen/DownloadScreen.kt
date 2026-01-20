package com.par9uet.jm.ui.screens.downloadScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.par9uet.jm.ui.components.CommonScaffold
import com.par9uet.jm.ui.state.rememberTabIndexState
import com.par9uet.jm.ui.viewModel.DownloadViewModel
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinActivityViewModel

private val tabList = listOf("下载中" to "downloading", "已下载" to "complete")

@Composable
fun DownloadScreen(
    downloadViewModel: DownloadViewModel = koinActivityViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val selectedTabIndexState = rememberTabIndexState()
    val scrollState = rememberScrollState()
    val pagerState = rememberPagerState(initialPage = 0) {
        tabList.size
    }
    val downloadLazyPagingItems = downloadViewModel.downloadPager.collectAsLazyPagingItems()
    val onTabClick: (index: Int) -> Unit = {
        selectedTabIndexState.value = it
        downloadViewModel.updateDownloadStatusFilter(tabList[it].second)
        coroutineScope.launch {
            pagerState.animateScrollToPage(selectedTabIndexState.value)
        }
    }
    CommonScaffold(title = "下载") {
        Column {
            PrimaryScrollableTabRow(
                selectedTabIndex = selectedTabIndexState.value,
                edgePadding = 0.dp,
                scrollState = scrollState
            ) {
                tabList.forEachIndexed { index, item ->
                    key(item.second) {
                        Tab(
                            selected = selectedTabIndexState.value == index,
                            onClick = {
                                onTabClick(index)
                            },
                            text = {
                                Text(
                                    text = item.first,
                                    maxLines = 1,
                                )
                            }
                        )
                    }
                }
            }
            val isRefreshing = downloadLazyPagingItems.loadState.refresh is LoadState.Loading
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = {
                    downloadLazyPagingItems.refresh()
                }
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.Top),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(10.dp)
                ) {
                    items(
                        downloadLazyPagingItems.itemCount,
                        key = downloadLazyPagingItems.itemKey { it.id },
                    ) { index ->
                        val item = downloadLazyPagingItems[index]
                        if (item != null) {
                            DownloadListItem(comic = item)
                        }
                    }
//                    items(count = 4, key = { it }) {
//                        DownloadListItem(
//                            comic = DownloadComic(
//                                2,
//                                "test name",
//                                listOf("test author"),
//                                "none",
//                                listOf(),
//                                "",
//                                .5f,
//                                "pending"
//                            )
//                        )
//                    }
                }
            }

        }
    }
}