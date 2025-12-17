package com.par9uet.jm.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.par9uet.jm.ui.components.Comment
import com.par9uet.jm.ui.viewModel.UserViewModel
import org.koin.compose.viewmodel.koinActivityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserHistoryCommentScreen(
    userViewModel: UserViewModel = koinActivityViewModel()
) {
    val historyCommentState by userViewModel.historyCommentState.collectAsState()
    val gridState = rememberLazyListState()
    val loadMore = {
        userViewModel.getHistoryCommentList("loadMore")
    }
    val onRefresh = {
        userViewModel.getHistoryCommentList("refresh")
    }
    val shouldLoadMore =
        remember(
            gridState.layoutInfo.visibleItemsInfo,
            gridState.layoutInfo.totalItemsCount,
            historyCommentState.isRefreshing,
            historyCommentState.hasMore
        ) {
            derivedStateOf {
                val layoutInfo = gridState.layoutInfo
                val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
                lastVisibleItem?.index == layoutInfo.totalItemsCount - 1 &&
                        !historyCommentState.isRefreshing &&
                        historyCommentState.hasMore
            }
        }
    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore.value) {
            loadMore()
        }
    }
    LaunchedEffect(Unit) {
        userViewModel.getHistoryCommentList("refresh")
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
                        "我的收藏",
                        color = MaterialTheme.colorScheme.surface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        if (historyCommentState.isRefreshing && historyCommentState.list.isEmpty()) {
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
                isRefreshing = historyCommentState.isRefreshing,
                state = rememberPullToRefreshState(),
                onRefresh = onRefresh,
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                LazyColumn(
                    state = gridState,
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    itemsIndexed(items = historyCommentState.list, key = { _, item ->
                        item.id
                    }) { index, comment ->
                        if (index > 0) {
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.surfaceContainerLowest
                            )
                        }
                        Comment(comment)
                    }
                }
            }
        }
    }
}