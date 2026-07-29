package com.par9uet.jm.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.par9uet.jm.data.models.ComicSearchOrderFilter
import com.par9uet.jm.ui.components.Comic
import com.par9uet.jm.ui.components.ComicSkeleton
import com.par9uet.jm.ui.components.CommonScaffold
import com.par9uet.jm.ui.components.FilterItem
import com.par9uet.jm.ui.components.PullRefreshAndLoadMoreGrid
import com.par9uet.jm.ui.viewModel.ComicSearchResultViewModel
import kotlinx.coroutines.flow.drop
import org.koin.compose.viewmodel.koinViewModel

@Composable
private fun ComicSearchResultSkeleton(
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(10.dp)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComicSearchResultScreen(
    searchContent: String,
    comicSearchResultViewModel: ComicSearchResultViewModel = koinViewModel()
) {
    LaunchedEffect(searchContent) {
        // 放到 filter 对象里，可以触发 pager 重建
        comicSearchResultViewModel.changeSearchComicContentFilter(searchContent)
    }

    val isFirstLoading by comicSearchResultViewModel.isSearchComicFirstLoading.collectAsState()
    val comicSearchLazyPagingItems =
        comicSearchResultViewModel.searchComicPager.collectAsLazyPagingItems()
    val comicSearchFilterState by comicSearchResultViewModel.searchComicFilterState.collectAsState()

    CommonScaffold(title = "搜索：${comicSearchFilterState.searchContent}") {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                ComicSearchOrderFilter.entries.forEach { item ->
                    key(item.label) {
                        FilterItem(
                            enabled = comicSearchLazyPagingItems.loadState.refresh !is LoadState.Loading,
                            label = item.label,
                            onClick = {
                                comicSearchResultViewModel.changeSearchComicOrderFilter(item)
                            },
                            active = item.value == comicSearchFilterState.order.value
                        )
                    }
                }
            }
            HorizontalDivider()
            if (comicSearchLazyPagingItems.loadState.refresh is LoadState.Loading && isFirstLoading) {
                ComicSearchResultSkeleton(
                    modifier = Modifier.weight(1f)
                )
                return@CommonScaffold
            }
            LaunchedEffect(Unit) {
                comicSearchResultViewModel.updateIsSearchComicFirstLoading(false)
            }
            val gridState = rememberLazyGridState()
            LaunchedEffect(Unit) {
                // 切换过滤参数时滚动到顶部
                comicSearchResultViewModel.searchComicFilterState
                    .drop(1)
                    .collect {
                        gridState.animateScrollToItem(0)
                    }
            }
            PullRefreshAndLoadMoreGrid(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                lazyPagingItems = comicSearchLazyPagingItems,
                itemKey = { it.comicKey },
                columns = GridCells.Fixed(3),
                gridState = gridState,
            ) {
                Comic(it)
            }
        }
    }
}