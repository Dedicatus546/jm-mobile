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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.par9uet.jm.data.models.Comic
import com.par9uet.jm.data.models.WeekData
import com.par9uet.jm.ui.components.Comic
import com.par9uet.jm.ui.components.ComicSkeleton
import com.par9uet.jm.ui.components.CommonScaffold
import com.par9uet.jm.ui.components.ErrorTips
import com.par9uet.jm.ui.components.FilterItem
import com.par9uet.jm.ui.components.FilterItemSkeleton
import com.par9uet.jm.ui.components.PullRefreshAndLoadMoreGrid
import com.par9uet.jm.ui.components.SelectDialog
import com.par9uet.jm.ui.components.SelectOption
import com.par9uet.jm.ui.models.CommonUIState
import com.par9uet.jm.ui.pagingSource.WeekFilter
import com.par9uet.jm.ui.viewModel.ComicViewModel
import kotlinx.coroutines.flow.drop
import org.koin.compose.viewmodel.koinActivityViewModel

@Composable
private fun ComicWeekRecommendSkeleton() {
    Column {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(10.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(rememberScrollState())
            ) {
                for (index in 0 until 3) {
                    key(index) {
                        FilterItemSkeleton(
                            modifier = Modifier.width(50.dp)
                        )
                    }
                }
            }
            FilterItemSkeleton(
                modifier = Modifier.width(150.dp)
            )
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
private fun ComicWeekCategorySelect(
    category: Pair<String, String>,
    weekDataState: CommonUIState<WeekData>,
    weekFilterState: WeekFilter,
    weekRecommendComicPagingItems: LazyPagingItems<Comic>,
    comicViewModel: ComicViewModel = koinActivityViewModel(),
) {
    var showSelectDialog by remember { mutableStateOf(false) }
    val weekCategoryOptionList by remember(weekDataState) {
        derivedStateOf {
            val list = weekDataState.data?.categoryList ?: listOf()
            list.map { SelectOption(label = it.second, value = it.first) }
        }
    }
    FilterItem(
        enabled = weekRecommendComicPagingItems.loadState.refresh !is LoadState.Loading,
        label = category.second,
        onClick = {
            showSelectDialog = true
        },
        active = true
    )
    if (showSelectDialog) {
        SelectDialog(
            title = "选择日期",
            value = weekFilterState.categoryId,
            selectOptionList = weekCategoryOptionList,
            onSelect = {
                comicViewModel.changeWeekCategoryFilter(it)
                showSelectDialog = false
            },
            onDismissRequest = {
                showSelectDialog = false
            }
        )
    }
}

@Composable
fun ComicWeekRecommendScreen(
    comicViewModel: ComicViewModel = koinActivityViewModel()
) {
    // 过滤参数，期数和类别
    val weekFilterState by comicViewModel.weekFilterState.collectAsState()

    // 期数列表
    val weekDataState by comicViewModel.weekDataState.collectAsState()
    val refreshWeekDataTrigger = comicViewModel.refreshWeekDataTrigger

    val isFirstLoading by comicViewModel.isWeekComicFirstLoading.collectAsState()
    val weekRecommendComicPagingItems = comicViewModel.weekComicPager.collectAsLazyPagingItems()

    // 当前选中类别
    val weekCategoryFilter by remember(weekFilterState) {
        derivedStateOf {
            val categoryList = weekDataState.data?.categoryList ?: listOf()
            categoryList.find { it.first == weekFilterState.categoryId }
        }
    }

    LaunchedEffect(Unit) {
        if (weekDataState.data != null) {
            return@LaunchedEffect
        }
        comicViewModel.getWeekData()
    }

    LaunchedEffect(Unit) {
        refreshWeekDataTrigger.collect {
            weekRecommendComicPagingItems.refresh()
        }
    }

    CommonScaffold(
        title = "每周推荐"
    ) {
        if (weekDataState.isError) {
            ErrorTips(
                errorMsg = weekDataState.errorMsg
            ) {
                comicViewModel.getWeekData()
            }
            return@CommonScaffold
        }
        if (
            weekDataState.isLoading ||
            weekRecommendComicPagingItems.loadState.refresh is LoadState.Loading && isFirstLoading
        ) {
            ComicWeekRecommendSkeleton()
            return@CommonScaffold
        }
        LaunchedEffect(Unit) {
            comicViewModel.updateIsWeekComicFirstLoading(false)
        }
        Column {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(10.dp)
            ) {
                val typeList = weekDataState.data!!.typeList
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .horizontalScroll(rememberScrollState())
                ) {
                    typeList.forEach { item ->
                        key(item.first) {
                            FilterItem(
                                enabled = weekRecommendComicPagingItems.loadState.refresh !is LoadState.Loading,
                                label = item.second,
                                onClick = {
                                    comicViewModel.changeWeekTypeFilter(item.first)
                                },
                                active = weekFilterState.typeId == item.first
                            )
                        }
                    }
                }
                weekCategoryFilter?.let {
                    ComicWeekCategorySelect(
                        category = it,
                        weekDataState = weekDataState,
                        weekFilterState = weekFilterState,
                        weekRecommendComicPagingItems = weekRecommendComicPagingItems
                    )
                }
            }
            HorizontalDivider()
            val gridState = rememberLazyGridState()
            LaunchedEffect(Unit) {
                // 切换过滤参数时滚动到顶部
                comicViewModel.weekFilterState
                    .drop(1)
                    .collect {
                        gridState.animateScrollToItem(0)
                    }
            }
            PullRefreshAndLoadMoreGrid(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                lazyPagingItems = weekRecommendComicPagingItems,
                key = { it.id },
                columns = GridCells.Fixed(3),
                gridState = gridState
            ) {
                Comic(it)
            }
        }
    }
}