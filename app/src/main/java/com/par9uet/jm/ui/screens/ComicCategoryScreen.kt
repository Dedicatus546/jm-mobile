package com.par9uet.jm.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.par9uet.jm.data.models.Category
import com.par9uet.jm.data.models.ComicCategoryOrderFilter
import com.par9uet.jm.data.models.ComicSearchOrderFilter
import com.par9uet.jm.ui.components.Comic
import com.par9uet.jm.ui.components.ComicSkeleton
import com.par9uet.jm.ui.components.CommonScaffold
import com.par9uet.jm.ui.components.FilterItem
import com.par9uet.jm.ui.components.FilterItemSkeleton
import com.par9uet.jm.ui.components.PullRefreshAndLoadMoreGrid
import com.par9uet.jm.ui.viewModel.ComicCategoryViewModel
import com.par9uet.jm.ui.viewModel.ComicSearchResultViewModel
import kotlinx.coroutines.flow.drop
import org.koin.compose.viewmodel.koinViewModel

@Composable
private fun ComicCategorySkeleton(
    modifier: Modifier = Modifier
) {
    Column {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp, top = 10.dp)
                .horizontalScroll(rememberScrollState())
        ) {
            val widths = listOf(60, 40, 50, 70)
            for (index in 0 until 4) {
                key(index) {
                    FilterItemSkeleton(
                        modifier = Modifier.width(widths[index].dp)
                    )
                }
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .horizontalScroll(rememberScrollState())
        ) {
            val widths = listOf(50, 60, 40)
            for (index in 0 until 3) {
                key(index) {
                    FilterItemSkeleton(
                        modifier = Modifier.width(widths[index].dp)
                    )
                }
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp, bottom = 10.dp)
                .horizontalScroll(rememberScrollState())
        ) {
            val widths = listOf(60, 40, 50, 70)
            for (index in 0 until 4) {
                key(index) {
                    FilterItemSkeleton(
                        modifier = Modifier.width(widths[index].dp)
                    )
                }
            }
        }
        HorizontalDivider()
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
}

@Composable
fun ComicCategoryScreen(
    comicCategoryViewModel: ComicCategoryViewModel = koinViewModel()
) {
    val isFirstLoading by comicCategoryViewModel.isComicCategoryFirstLoading.collectAsState()
    val comicCategoryLazyPagingItems =
        comicCategoryViewModel.comicCategoryPager.collectAsLazyPagingItems()
    val comicCategoryFilterState by comicCategoryViewModel.comicCategoryFilterState.collectAsState()
    val filterState by comicCategoryViewModel.filterListState.collectAsState()
    val subCategoryList by remember {
        derivedStateOf {
            filterState.data?.categoryList?.find { it.slug == comicCategoryFilterState.category }?.subCategoryList
                ?: listOf()
        }
    }
    val mainNavController = LocalMainNavController.current

    LaunchedEffect(Unit) {
        if (comicCategoryViewModel.filterListState.value.data != null) {
            return@LaunchedEffect
        }
        comicCategoryViewModel.getCategory()
    }

    CommonScaffold(title = "分类") {
        Column {
            if (
                filterState.isLoading
                || comicCategoryLazyPagingItems.loadState.refresh is LoadState.Loading
                && isFirstLoading
            ) {
                ComicCategorySkeleton(
                    modifier = Modifier.weight(1f)
                )
                return@CommonScaffold
            }
            Column(
                modifier = Modifier.padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    ComicCategoryOrderFilter.entries.forEach { item ->
                        key(item.label) {
                            FilterItem(
                                enabled = comicCategoryLazyPagingItems.loadState.refresh !is LoadState.Loading,
                                label = item.label,
                                onClick = {
                                    comicCategoryViewModel.changeComicCategoryOrderFilter(item)
                                },
                                active = item.value == comicCategoryFilterState.order.value
                            )
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    filterState.data!!.categoryList.forEach { item ->
                        key(item.id) {
                            FilterItem(
                                enabled = comicCategoryLazyPagingItems.loadState.refresh !is LoadState.Loading,
                                label = item.name,
                                onClick = {
                                    if (item.type == "slug" || item.id == "0") {
                                        comicCategoryViewModel.changeComicSubCategoryFilter("")
                                        comicCategoryViewModel.changeComicCategoryFilter(item.slug)
                                    } else {
                                        mainNavController.navigate("comicSearchResult/${item.name}")
                                    }
                                },
                                active = item.slug == comicCategoryFilterState.category
                            )
                        }
                    }
                }
                if (subCategoryList.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        subCategoryList.forEach { item ->
                            key(item.id) {
                                FilterItem(
                                    enabled = comicCategoryLazyPagingItems.loadState.refresh !is LoadState.Loading,
                                    label = item.name,
                                    onClick = {
                                        comicCategoryViewModel.changeComicSubCategoryFilter(item.slug)
                                    },
                                    active = item.slug == comicCategoryFilterState.subCategory
                                )
                            }
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    filterState.data!!.tagList.forEach { item ->
                        key(item) {
                            FilterItem(
                                enabled = comicCategoryLazyPagingItems.loadState.refresh !is LoadState.Loading,
                                label = item,
                                onClick = {
                                    mainNavController.navigate("comicSearchResult/${item}")
                                },
                                active = false
                            )
                        }
                    }
                }
            }
            HorizontalDivider()
            if (comicCategoryLazyPagingItems.loadState.refresh is LoadState.Loading && isFirstLoading) {
                ComicCategorySkeleton(
                    modifier = Modifier.weight(1f)
                )
                return@CommonScaffold
            }
            LaunchedEffect(Unit) {
                comicCategoryViewModel.updateIsComicCategoryFirstLoading(false)
            }
            val gridState = rememberLazyGridState()
            LaunchedEffect(Unit) {
                // 切换过滤参数时滚动到顶部
                comicCategoryViewModel.comicCategoryFilterState
                    .drop(1)
                    .collect {
                        gridState.animateScrollToItem(0)
                    }
            }
            PullRefreshAndLoadMoreGrid(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                lazyPagingItems = comicCategoryLazyPagingItems,
                itemKey = { it.comicKey },
                columns = GridCells.Fixed(3),
                gridState = gridState,
            ) {
                Comic(it)
            }
        }
    }
}