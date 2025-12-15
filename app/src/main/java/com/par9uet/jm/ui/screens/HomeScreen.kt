package com.par9uet.jm.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.par9uet.jm.ui.components.Comic
import com.par9uet.jm.ui.viewModel.HomeViewModel
import org.koin.compose.viewmodel.koinActivityViewModel

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = koinActivityViewModel()
) {
    val state by homeViewModel.state.collectAsState()
    val isRefreshing = state.data.isNotEmpty() && state.isLoading
    val onRefresh = {
        homeViewModel.getPromoteComicList()
    }
    LaunchedEffect(Unit) {
        if (state.data.isNotEmpty()) {
            return@LaunchedEffect
        }
        homeViewModel.getPromoteComicList()
    }
    if (state.data.isEmpty() && state.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
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
                .fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
            ) {
                itemsIndexed(
                    state.data,
                    key = { _, item -> item.id }) { _, promoteComic ->
                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        Text(promoteComic.title, modifier = Modifier.padding(bottom = 8.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            items(promoteComic.list, key = { it.id }) { comic ->
                                Comic(
                                    comic = comic,
                                    modifier = Modifier.width(150.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}