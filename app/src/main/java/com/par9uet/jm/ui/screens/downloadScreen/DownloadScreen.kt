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
import com.par9uet.jm.ui.components.CommonScaffold
import com.par9uet.jm.ui.components.FilterItem
import com.par9uet.jm.ui.components.PullRefreshAndLoadMoreGrid
import com.par9uet.jm.ui.viewModel.DownloadViewModel

private val tabList = listOf("downloading" to "下载中", "complete" to "已下载")

@Composable
fun DownloadScreen() {
    val downloadViewModel: DownloadViewModel = hiltViewModel()
    val downloadFilterState by downloadViewModel.downloadFilterState.collectAsState()
    val downloadComicLazyPagingItems =
        downloadViewModel.localComicPager.collectAsLazyPagingItems()
    val onTabClick: (tab: String) -> Unit = {
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
                DownloadListItem(localComic = it)
            }
        }
    }
}