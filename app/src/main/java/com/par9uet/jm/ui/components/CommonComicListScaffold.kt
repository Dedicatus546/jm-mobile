package com.par9uet.jm.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.par9uet.jm.data.models.Comic
import com.par9uet.jm.ui.screens.LocalMainNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommonComicListScaffold(
    title: String,
    list: List<Comic>,
    isRefreshing: Boolean,
    isMoreLoading: Boolean,
    hasMore: Boolean,
    onRefresh: () -> Unit = {},
    onLoadMore: () -> Unit = {},
    columns: GridCells = GridCells.Fixed(3),
    verticalArrangement: Arrangement.HorizontalOrVertical = Arrangement.spacedBy(10.dp),
    horizontalArrangement: Arrangement.HorizontalOrVertical = Arrangement.spacedBy(10.dp),
    contentPadding: PaddingValues = PaddingValues(8.dp),
    stickyHeaderContent: @Composable (() -> Unit)? = null
) {
    val mainNavController = LocalMainNavController.current
    val gridState = rememberLazyGridState()
    val shouldLoadMore =
        remember(
            gridState.layoutInfo.visibleItemsInfo,
            gridState.layoutInfo.totalItemsCount,
            isRefreshing,
            hasMore
        ) {
            derivedStateOf {
                val layoutInfo = gridState.layoutInfo
                val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
                lastVisibleItem?.index == layoutInfo.totalItemsCount - 1 &&
                        !isRefreshing &&
                        hasMore
            }
        }
    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore.value) {
            onLoadMore()
        }
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            val scrollBehavior =
                TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                navigationIcon = {
                    IconButton(onClick = {
                        mainNavController.popBackStack()
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回上一页",
                            tint = MaterialTheme.colorScheme.surface,
                        )
                    }
                },
                title = {
                    Text(
                        title,
                        color = MaterialTheme.colorScheme.surface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            PullToRefreshBox(
                isRefreshing = isRefreshing && list.isNotEmpty(),
                state = rememberPullToRefreshState(),
                onRefresh = onRefresh,
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                LazyVerticalGrid(
                    state = gridState,
                    columns = columns,
                    verticalArrangement = verticalArrangement,
                    horizontalArrangement = horizontalArrangement,
                    contentPadding = contentPadding,
                ) {
                    if (stickyHeaderContent !== null) {
                        stickyHeader {
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surface)
                            ) {
                                stickyHeaderContent()
                            }
                        }
                    }
                    items(
                        items = list,
                        key = { it.id },
                    ) { item ->
                        Comic(item)
                    }
                    if (list.isNotEmpty()) {
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
                        .pointerInput(Unit) { },
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}