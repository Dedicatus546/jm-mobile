package com.par9uet.jm.ui.screens

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Api
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.par9uet.jm.data.models.ComicFilterOrder
import com.par9uet.jm.ui.components.Comic
import com.par9uet.jm.ui.components.LoadMore
import com.par9uet.jm.ui.components.SettingSelectDialog
import com.par9uet.jm.viewModel.ComicQuickSearchViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComicQuickSearchScreen(
    searchContent: String,
    comicQuickSearchViewModel: ComicQuickSearchViewModel = koinViewModel()
) {
    var expanded by remember { mutableStateOf(false) }
    val gridState = rememberLazyGridState()
    val comicList = comicQuickSearchViewModel.list
    val loading = comicQuickSearchViewModel.loading
    val order = comicQuickSearchViewModel.order
    val isRefreshing = comicList.isNotEmpty() && loading
    val hasMore = comicList.size < comicQuickSearchViewModel.total
    val loadMore = {
        comicQuickSearchViewModel.getComicList(comicQuickSearchViewModel.page + 1, searchContent)
    }
    val onRefresh = {
        comicQuickSearchViewModel.getComicList(nPage = 1, searchContent, clearList = true)
    }
    val onOrderChange: (ComicFilterOrder) -> Unit = {
        comicQuickSearchViewModel.order = it
        comicQuickSearchViewModel.list = mutableListOf()
        comicQuickSearchViewModel.getComicList(nPage = 1, searchContent)
        expanded = false
    }
    val shouldLoadMore =
        remember(
            gridState.layoutInfo.visibleItemsInfo,
            gridState.layoutInfo.totalItemsCount,
            loading,
            hasMore
        ) {
            derivedStateOf {
                val layoutInfo = gridState.layoutInfo
                val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
                lastVisibleItem?.index == layoutInfo.totalItemsCount - 1 &&
                        !loading &&
                        hasMore
            }
        }
    LaunchedEffect(shouldLoadMore) {
        Log.d("shouldLoadMore", shouldLoadMore.value.toString())
        if (shouldLoadMore.value) {
            loadMore()
        }
    }
    LaunchedEffect(Unit) {
        comicQuickSearchViewModel.getComicList(1, searchContent)
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
                title = {
                    Text(
                        searchContent,
                        color = MaterialTheme.colorScheme.surface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        if (comicList.isEmpty() && loading) {
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
                isRefreshing = isRefreshing,
                state = rememberPullToRefreshState(),
                onRefresh = onRefresh,
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                LazyVerticalGrid(
                    state = gridState,
                    columns = GridCells.Fixed(3),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    items(items = comicList, key = {
                        it.id
                    }) { comic ->
                        Comic(comic)
                    }
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        LoadMore(
                            isLoading = loading,
                            hasMore = hasMore
                        )
                    }
                }
            }
        }
    }
}