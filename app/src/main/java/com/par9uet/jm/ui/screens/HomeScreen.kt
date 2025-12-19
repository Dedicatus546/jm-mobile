package com.par9uet.jm.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.par9uet.jm.ui.components.Comic
import com.par9uet.jm.ui.components.PullRefreshAndLoadMoreGrid
import com.par9uet.jm.ui.viewModel.ComicViewModel
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinActivityViewModel

@Composable
fun HomeScreen(
    comicViewModel: ComicViewModel = koinActivityViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    var selectedTabIndex by remember { mutableStateOf(0) }
    val promoteComicState by comicViewModel.promoteComicState.collectAsState()
    val pagerState = rememberPagerState(initialPage = 0) { promoteComicState.list.size }
    val onTabClick: (index: Int) -> Unit = {
        selectedTabIndex = it
        coroutineScope.launch {
            pagerState.animateScrollToPage(selectedTabIndex)
        }
    }
    LaunchedEffect(pagerState.currentPage) {
        selectedTabIndex = pagerState.currentPage
    }
    LaunchedEffect(Unit) {
        if (promoteComicState.list.isNotEmpty()) {
            return@LaunchedEffect
        }
        comicViewModel.getPromoteComicList()
    }
    if (promoteComicState.isFirstInit) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        Column {
            if (promoteComicState.list.isNotEmpty()) {
                PrimaryScrollableTabRow(
                    selectedTabIndex = selectedTabIndex,
                    edgePadding = 0.dp,
                    scrollState = rememberScrollState()
                ) {
                    promoteComicState.list.forEachIndexed { index, item ->
                        key(item.id) {
                            Tab(
                                selected = selectedTabIndex == index,
                                onClick = {
                                    onTabClick(index)
                                },
                                text = {
                                    Text(
                                        text = item.title,
                                        maxLines = 1,
                                    )
                                }
                            )
                        }
                    }
                }
            }
            HorizontalPager(
                state = pagerState
            ) { page ->
                val comicList = promoteComicState.list.getOrNull(page)?.list ?: listOf()
                PullRefreshAndLoadMoreGrid(
                    list = comicList,
                    key = { item -> item.id },
                    isRefreshing = promoteComicState.isLoading,
                    onRefresh = {
                        comicViewModel.getPromoteComicList()
                    },
                    isMoreLoading = false,
                    hasMore = false,
                    showLoadMore = false,
                    columns = GridCells.Fixed(3),
                ) { comic ->
                    Comic(comic)
                }
            }
        }
    }
}