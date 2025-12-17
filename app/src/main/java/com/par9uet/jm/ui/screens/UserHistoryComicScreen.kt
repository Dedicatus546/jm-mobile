package com.par9uet.jm.ui.screens

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.par9uet.jm.ui.components.CommonComicListScaffold
import com.par9uet.jm.ui.viewModel.UserViewModel
import org.koin.compose.viewmodel.koinActivityViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserHistoryComicScreen(
    userViewModel: UserViewModel = koinActivityViewModel()
) {
    val historyComicState by userViewModel.historyComicState.collectAsState()
    LaunchedEffect(Unit) {
        if (historyComicState.list.isNotEmpty()) {
            return@LaunchedEffect
        }
        userViewModel.getHistoryComicList("refresh")
    }
    CommonComicListScaffold(
        title = "历史浏览",
        list = historyComicState.list,
        isRefreshing = historyComicState.isRefreshing,
        isMoreLoading = false,
        hasMore = false,
        onRefresh = {
            userViewModel.getHistoryComicList("refresh")
        },
    )
}