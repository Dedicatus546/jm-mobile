package com.par9uet.jm.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.par9uet.jm.ui.models.PageAppendUIState
import com.par9uet.jm.ui.screens.LocalMainNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> CommonListScaffold(
    title: String,
    uiState: PageAppendUIState<T>,
    onRefresh: () -> Unit = {},
    key: ((item: T) -> Any)?,
    itemContent: @Composable (item: T) -> Unit,
    state: LazyGridState = rememberLazyGridState(),
    columns: GridCells = GridCells.Fixed(3),
    verticalArrangement: Arrangement.HorizontalOrVertical = Arrangement.spacedBy(10.dp),
    horizontalArrangement: Arrangement.HorizontalOrVertical = Arrangement.spacedBy(10.dp),
    contentPadding: PaddingValues = PaddingValues(8.dp)
) {
    val mainNavController = LocalMainNavController.current
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
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回上一页")
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
        if (uiState.isInitializing) {
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            PullToRefreshBox(
                isRefreshing = uiState.isRefreshing,
                state = rememberPullToRefreshState(),
                onRefresh = onRefresh,
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                LazyVerticalGrid(
                    state = state,
                    columns = columns,
                    verticalArrangement = verticalArrangement,
                    horizontalArrangement = horizontalArrangement,
                    contentPadding = contentPadding,
                ) {
                    items(
                        items = uiState.list,
                        key = key,
                    ) { item ->
                        itemContent(item)
                    }
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        LoadMore(
                            isLoading = false,
                            hasMore = uiState.hasMore
                        )
                    }
                }
            }
        }
    }
}