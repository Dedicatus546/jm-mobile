package com.par9uet.jm.ui.screens.downloadScreen

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.par9uet.jm.data.models.DownloadStatus
import com.par9uet.jm.router.LocalComicDetailRoute
import com.par9uet.jm.ui.components.CommonScaffold
import com.par9uet.jm.ui.components.FilterItem
import com.par9uet.jm.ui.components.PullRefreshAndLoadMoreGrid
import com.par9uet.jm.ui.provider.LocalMainNavController
import com.par9uet.jm.ui.viewModel.DownloadViewModel

private val tabList = listOf(
    DownloadStatus.COMPLETE to "完成",
    DownloadStatus.PENDING to "等待中",
    DownloadStatus.DOWNLOADING to "下载中",
    DownloadStatus.ERROR to "出错",
    DownloadStatus.PAUSE to "暂停"
)

@Composable
fun DownloadScreen() {
    val downloadViewModel: DownloadViewModel = hiltViewModel()
    val mainNavController = LocalMainNavController.current
    val downloadFilterState by downloadViewModel.downloadFilterState.collectAsState()
    val downloadComicLazyPagingItems =
        downloadViewModel.localComicPager.collectAsLazyPagingItems()
    val onTabClick: (status: DownloadStatus) -> Unit = {
        downloadViewModel.updateDownloadStatusFilter(it)
    }
    CommonScaffold(title = "下载") {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                tabList.forEach { item ->
                    key(item.first) {
                        FilterItem(
                            label = item.second,
                            onClick = {
                                onTabClick(item.first)
                            },
                            active = downloadFilterState.status == item.first
                        )
                    }
                }
            }
            PullRefreshAndLoadMoreGrid(
                modifier = Modifier.fillMaxWidth(),
                lazyPagingItems = downloadComicLazyPagingItems,
                itemKey = { it.comicId },
                columns = GridCells.Fixed(3)
            ) {
                DownloadListItem(localComic = it, onClick = {
                    mainNavController.navigate(
                        LocalComicDetailRoute(
                            comicId = it.comicId
                        )
                    )
                })
            }
        }
    }
}