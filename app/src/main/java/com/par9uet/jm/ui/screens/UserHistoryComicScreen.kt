package com.par9uet.jm.ui.screens

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.par9uet.jm.ui.components.CommonComicListScaffold
import com.par9uet.jm.ui.viewModel.UserHistoryComicViewModel
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserHistoryComicScreen(
    userHistoryComicViewModel: UserHistoryComicViewModel = koinViewModel()
) {
    val list = userHistoryComicViewModel.list
    val isRefreshing = userHistoryComicViewModel.isRefreshing
    LaunchedEffect(Unit) {
        if (list.isNotEmpty()) {
            return@LaunchedEffect
        }
        userHistoryComicViewModel.refresh()
    }
    CommonComicListScaffold(
        title = "历史浏览",
        list = list,
        isRefreshing = isRefreshing,
        isLoadingMore = false,
        hasMore = false,
        onRefresh = {
            userHistoryComicViewModel.refresh()
        },
    )
}