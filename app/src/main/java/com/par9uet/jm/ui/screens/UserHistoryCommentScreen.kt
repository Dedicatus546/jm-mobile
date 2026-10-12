package com.par9uet.jm.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.par9uet.jm.ui.components.Comment
import com.par9uet.jm.ui.components.CommentSkeleton
import com.par9uet.jm.ui.components.CommonScaffold
import com.par9uet.jm.ui.components.PullRefreshAndLoadMoreGrid
import com.par9uet.jm.ui.viewModel.UserViewModel
import com.par9uet.jm.utils.hiltActivityViewModel

@Composable
private fun UserHistoryCommentSkeleton() {
    LazyColumn(
        modifier = Modifier.padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        for (i in 1..10) {
            item {
                CommentSkeleton()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserHistoryCommentScreen() {
    val userViewModel: UserViewModel = hiltActivityViewModel()
    val historyCommentLazyPagingItems =
        userViewModel.historyCommentPager.collectAsLazyPagingItems()
    val isFirstLoading by userViewModel.isHistoryCommentFirstLoading.collectAsState()
    CommonScaffold(
        title = "历史评论"
    ) {
        if (historyCommentLazyPagingItems.loadState.refresh is LoadState.Loading && isFirstLoading) {
            UserHistoryCommentSkeleton()
            return@CommonScaffold
        }
        LaunchedEffect(Unit) {
            userViewModel.updateIsHistoryCommentFirstLoading(false)
        }
        PullRefreshAndLoadMoreGrid(
            lazyPagingItems = historyCommentLazyPagingItems,
            itemKey = { it.id },
            columns = GridCells.Fixed(1)
        ) {
            Comment(it)
        }
    }
}