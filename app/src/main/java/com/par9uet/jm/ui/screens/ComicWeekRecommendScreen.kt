package com.par9uet.jm.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.par9uet.jm.ui.components.Comic
import com.par9uet.jm.ui.components.ComicSkeleton
import com.par9uet.jm.ui.components.CommonScaffold
import com.par9uet.jm.ui.components.ErrorTips
import com.par9uet.jm.ui.components.FilterItem
import com.par9uet.jm.ui.components.FilterItemSkeleton
import com.par9uet.jm.ui.components.PullRefreshAndLoadMoreGrid
import com.par9uet.jm.ui.viewModel.ComicViewModel
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ComicWeekCategorySelect(comicViewModel: ComicViewModel) {
    val coroutineScope = rememberCoroutineScope()
    var showWeekSelectBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val weekDataState by comicViewModel.weekDataState.collectAsState()
    val weekFilterState by comicViewModel.weekFilterState.collectAsState()
    val weekRecommendComicPagingItems = comicViewModel.weekComicPager.collectAsLazyPagingItems()

    // 当前选中类别
    val weekCategoryFilter by remember(weekFilterState) {
        derivedStateOf {
            val categoryList = weekDataState.data?.categoryList ?: listOf()
            categoryList.find { it.first == weekFilterState.categoryId }
        }
    }

    if (weekCategoryFilter != null) {
        FilterItem(
            enabled = weekRecommendComicPagingItems.loadState.refresh !is LoadState.Loading,
            label = weekCategoryFilter!!.second,
            onClick = {
                showWeekSelectBottomSheet = true
            },
            active = true
        )
        if (showWeekSelectBottomSheet) {
            val items = weekDataState.data?.categoryList ?: listOf()
            val lazyColumnState = rememberLazyListState()
            LaunchedEffect(Unit) {
                // 滚动到对应行
                val index = items.indexOfFirst { it.first == weekCategoryFilter!!.first }
                lazyColumnState.scrollToItem(index)
            }
            ModalBottomSheet(
                sheetState = sheetState,
                onDismissRequest = {
                    showWeekSelectBottomSheet = false
                }
            ) {
                LazyColumn(
                    state = lazyColumnState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(.5f)
                ) {
                    items(
                        items = items,
                        key = { it.first }
                    ) {
                        ListItem(
                            colors = ListItemDefaults.colors().copy(
                                containerColor = Color.Transparent
                            ),
                            modifier = Modifier
                                .clickable(onClick = {
                                    comicViewModel.changeWeekCategoryFilter(it.first)
                                    coroutineScope.launch {
                                        sheetState.hide()
                                    }
                                }),
                            headlineContent = {
                                Text(it.second)
                            },
                            trailingContent = {
                                if (it.first == weekCategoryFilter!!.first) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "选中$it"
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
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
                ComicWeekCategorySelect(comicViewModel = comicViewModel)
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
                itemKey = { it.comicKey },
                columns = GridCells.Fixed(3),
                gridState = gridState
            ) {
                Comic(it)
            }
        }
    }
}