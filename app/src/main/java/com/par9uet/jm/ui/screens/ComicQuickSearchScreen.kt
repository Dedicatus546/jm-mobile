package com.par9uet.jm.ui.screens

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.par9uet.jm.ui.components.CommonComicListScaffold
import com.par9uet.jm.ui.viewModel.ComicViewModel
import org.koin.compose.viewmodel.koinActivityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComicQuickSearchScreen(
    searchContent: String,
    comicViewModel: ComicViewModel = koinActivityViewModel()
) {
    val comicQuickSearchState by comicViewModel.comicQuickSearchState.collectAsState()
    LaunchedEffect(Unit) {
        if (comicQuickSearchState.list.isNotEmpty()) {
            return@LaunchedEffect
        }
        comicViewModel.getComicQuickSearchList("refresh", searchContent)
    }
    CommonComicListScaffold(
        title = searchContent,
        list = comicQuickSearchState.list,
        isRefreshing = comicQuickSearchState.isRefreshing,
        isMoreLoading = comicQuickSearchState.isMoreLoading,
        hasMore = comicQuickSearchState.hasMore,
        onRefresh = {
            comicViewModel.getComicQuickSearchList("refresh", searchContent)
        },
        onLoadMore = {
            comicViewModel.getComicQuickSearchList("loadMore", searchContent)
        }
    )
}