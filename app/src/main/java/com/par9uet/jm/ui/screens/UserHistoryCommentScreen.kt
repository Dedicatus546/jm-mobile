package com.par9uet.jm.ui.screens

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.par9uet.jm.ui.components.Comment
import com.par9uet.jm.ui.components.CommonScaffold
import com.par9uet.jm.ui.components.PullRefreshAndLoadMoreGrid
import com.par9uet.jm.ui.viewModel.UserViewModel
import org.koin.compose.viewmodel.koinActivityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserHistoryCommentScreen(
    userViewModel: UserViewModel = koinActivityViewModel()
) {
    val historyCommentState by userViewModel.historyCommentState.collectAsState()
    LaunchedEffect(Unit) {
        if (historyCommentState.list.isNotEmpty()) {
            return@LaunchedEffect
        }
        userViewModel.getHistoryCommentList("refresh")
    }
    CommonScaffold(
        title = "历史评论"
    ) {
        PullRefreshAndLoadMoreGrid(
            list = historyCommentState.list,
            isRefreshing = historyCommentState.isRefreshing,
            isMoreLoading = historyCommentState.isMoreLoading,
            hasMore = historyCommentState.hasMore,
            onRefresh = {
                userViewModel.getHistoryCommentList("refresh")
            },
            onLoadMore = {
                userViewModel.getHistoryCommentList("loadMore")
            },
            columns = GridCells.Fixed(1)
        ) {
            Comment(it)
        }
    }
}