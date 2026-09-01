package com.par9uet.jm.ui.screens.downloadScreen

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.par9uet.jm.database.model.DownloadComic
import com.par9uet.jm.ui.components.CommonScaffold
import com.par9uet.jm.ui.components.FilterItem
import com.par9uet.jm.ui.components.PullRefreshAndLoadMoreGrid
import com.par9uet.jm.ui.state.rememberTabIndexState
import com.par9uet.jm.ui.viewModel.DownloadViewModel
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinActivityViewModel
import java.util.Date
import java.util.TimeZone
import kotlin.time.Clock
import kotlin.time.Instant

private val tabList = listOf("downloading" to "下载中", "complete" to "已下载")

@Composable
fun DownloadScreen(
    downloadViewModel: DownloadViewModel = koinActivityViewModel()
) {
    val downloadFilterState by downloadViewModel.downloadFilterState.collectAsState()
    val downloadComicLazyPagingItems =
        downloadViewModel.downloadComicPager.collectAsLazyPagingItems()
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
                itemKey = { it.id },
                columns = GridCells.Fixed(3)
            ) {
                DownloadListItem(comic = it)
            }
        }
    }
}