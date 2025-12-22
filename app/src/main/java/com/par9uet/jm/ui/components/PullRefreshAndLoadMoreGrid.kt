package com.par9uet.jm.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

@Composable
fun <T> PullRefreshAndLoadMoreGrid(
    modifier: Modifier = Modifier,
    list: List<T>,
    key: ((item: T) -> Any)? = null,
    isRefreshing: Boolean,
    isMoreLoading: Boolean,
    showLoadMore: Boolean = true,
    hasMore: Boolean,
    pullToRefreshState: PullToRefreshState = rememberPullToRefreshState(),
    gridState: LazyGridState = rememberLazyGridState(),
    onRefresh: () -> Unit = {},
    onLoadMore: () -> Unit = {},
    columns: GridCells,
    verticalArrangement: Arrangement.HorizontalOrVertical = Arrangement.spacedBy(10.dp),
    horizontalArrangement: Arrangement.HorizontalOrVertical = Arrangement.spacedBy(10.dp),
    stickyHeaderContent: @Composable (() -> Unit)? = null,
    itemContent: @Composable ((item: T) -> Unit),
) {
    val shouldLoadMore by
        remember(
            gridState,
            isRefreshing,
            hasMore
        ) {
            derivedStateOf {
                val layoutInfo = gridState.layoutInfo
                val totalItemsCount = layoutInfo.totalItemsCount
                val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                lastVisibleItemIndex == totalItemsCount - 1 &&
                        !isRefreshing &&
                        hasMore
            }
        }
    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) {
            onLoadMore()
        }
    }
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        state = pullToRefreshState,
        onRefresh = onRefresh,
        modifier = modifier
    ) {
        LazyVerticalGrid(
            state = gridState,
            columns = columns,
            verticalArrangement = verticalArrangement,
            horizontalArrangement = horizontalArrangement,
        ) {
//            if (stickyHeaderContent !== null) {
//                stickyHeader {
//                    Box(
//                        Modifier
//                            .fillMaxWidth()
//                            .background(MaterialTheme.colorScheme.surface)
//                    ) {
//                        stickyHeaderContent()
//                    }
//                }
//            }
            items(
                items = list,
                key = key,
            ) { item ->
                itemContent(item)
            }
            if (list.isNotEmpty() && showLoadMore) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    LoadMore(
                        isLoading = isMoreLoading,
                        hasMore = hasMore
                    )
                }
            }
        }
    }
    if (isRefreshing && list.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                // 禁止点击，让点击穿透
                .pointerInput(Unit) { },
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}