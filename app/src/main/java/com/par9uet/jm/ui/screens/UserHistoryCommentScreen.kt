package com.par9uet.jm.ui.screens

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.paging.compose.collectAsLazyPagingItems
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
    val historyCommentLazyPagingItems = userViewModel.historyCommentPager.collectAsLazyPagingItems()
    CommonScaffold(
        title = "历史评论"
    ) {
        PullRefreshAndLoadMoreGrid(
            lazyPagingItems = historyCommentLazyPagingItems,
            key = { it.id },
            columns = GridCells.Fixed(1)
        ) {
            Comment(it)
        }
    }
}