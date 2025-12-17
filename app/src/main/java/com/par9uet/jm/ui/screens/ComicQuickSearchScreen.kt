package com.par9uet.jm.ui.screens

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.par9uet.jm.ui.components.CommonComicListScaffold
import com.par9uet.jm.ui.viewModel.ComicQuickSearchViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComicQuickSearchScreen(
    searchContent: String,
    comicQuickSearchViewModel: ComicQuickSearchViewModel = koinViewModel()
) {
    val list = comicQuickSearchViewModel.list
    val isLoadingMore = comicQuickSearchViewModel.isLoadingMore
    val isRefreshing = comicQuickSearchViewModel.isRefreshing
    val hasMore = comicQuickSearchViewModel.hasMore
    LaunchedEffect(Unit) {
        if (list.isNotEmpty()) {
            return@LaunchedEffect
        }
        comicQuickSearchViewModel.refresh(searchContent)
    }
    CommonComicListScaffold(
        title = searchContent,
        list = list,
        isRefreshing = isRefreshing,
        isMoreLoading = isLoadingMore,
        hasMore = hasMore,
        onRefresh = {
            comicQuickSearchViewModel.refresh(searchContent)
        },
        onLoadMore = {
            comicQuickSearchViewModel.loadMore(searchContent)
        }
    )
}