package com.par9uet.jm.ui.screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.par9uet.jm.data.models.ComicFilterOrder
import com.par9uet.jm.ui.components.CommonComicListScaffold
import com.par9uet.jm.ui.viewModel.UserCollectComicViewModel
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserCollectComicScreen(
    userCollectComicViewModel: UserCollectComicViewModel = koinViewModel()
) {
    val list = userCollectComicViewModel.list
    val isLoadingMore = userCollectComicViewModel.isLoadingMore
    val order = userCollectComicViewModel.order
    val isRefreshing = userCollectComicViewModel.isRefreshing
    val hasMore = userCollectComicViewModel.hasMore
    LaunchedEffect(Unit) {
        if (list.isNotEmpty()) {
            return@LaunchedEffect
        }
        userCollectComicViewModel.refresh()
    }
    CommonComicListScaffold(
        title = "我的收藏",
        list = list,
        isRefreshing = isRefreshing,
        isLoadingMore = isLoadingMore,
        hasMore = hasMore,
        onRefresh = {
            userCollectComicViewModel.refresh()
        },
        onLoadMore = {
            userCollectComicViewModel.loadMore()
        }
    ) {
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            ComicFilterOrder.entries.forEachIndexed { index, item ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = ComicFilterOrder.entries.size
                    ),
                    onClick = {
                        userCollectComicViewModel.order = item
                        userCollectComicViewModel.list = listOf()
                        userCollectComicViewModel.refresh()
                    },
                    selected = item.value == order.value,
                    label = {
                        Text(item.label)
                    }
                )
            }
        }
    }
//    Scaffold(
//        modifier = Modifier.fillMaxSize(),
//        topBar = {
//            val scrollBehavior =
//                TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
//            TopAppBar(
//                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = MaterialTheme.colorScheme.primary
//                ),
//                title = {
//                    Text(
//                        "我的收藏",
//                        color = MaterialTheme.colorScheme.surface,
//                        maxLines = 1,
//                        overflow = TextOverflow.Ellipsis
//                    )
//                },
//                actions = {
//                    ExposedDropdownMenuBox(
//                        expanded = expanded,
//                        onExpandedChange = { expanded = !expanded }
//                    ) {
//                        FilterChip(
//                            border = null,
//                            selected = false,
//                            onClick = { expanded = true },
//                            label = {
//                                Text(
//                                    text = order.label,
//                                    fontSize = 18.sp
//                                )
//                            },
//                            trailingIcon = {
//                                Icon(
//                                    Icons.Default.ArrowDropDown,
//                                    tint = MaterialTheme.colorScheme.onPrimary,
//                                    contentDescription = "筛选",
//                                    modifier = Modifier.size(18.dp)
//                                )
//                            },
//                            colors = FilterChipDefaults.filterChipColors(
//                                containerColor = Color.Transparent,
//                                labelColor = MaterialTheme.colorScheme.onPrimary
//                            )
//                        )
//                        ExposedDropdownMenu(
//                            expanded = expanded,
//                            onDismissRequest = { expanded = false }
//                        ) {
//                            ComicFilterOrder.entries.forEach {
//                                DropdownMenuItem(
//                                    text = { Text(text = it.label, fontSize = 18.sp) },
//                                    onClick = {
//                                        onOrderChange(it)
//                                    }
//                                )
//                            }
//                        }
//                    }
//                },
//                scrollBehavior = scrollBehavior
//            )
//        }
//    ) { innerPadding ->
//        if (list.isEmpty() && isLoadingMore) {
//            Box(
//                modifier = Modifier
//                    .padding(innerPadding)
//                    .fillMaxSize(),
//                contentAlignment = Alignment.Center
//            ) {
//                CircularProgressIndicator()
//            }
//        } else {
//            PullToRefreshBox(
//                isRefreshing = isRefreshing,
//                state = rememberPullToRefreshState(),
//                onRefresh = onRefresh,
//                modifier = Modifier
//                    .padding(innerPadding)
//                    .fillMaxSize()
//            ) {
//                LazyVerticalGrid(
//                    state = gridState,
//                    columns = GridCells.Fixed(3),
//                    verticalArrangement = Arrangement.spacedBy(10.dp),
//                    horizontalArrangement = Arrangement.spacedBy(10.dp),
//                    contentPadding = PaddingValues(8.dp)
//                ) {
//                    items(items = list, key = {
//                        it.id
//                    }) { comic ->
//                        Comic(comic)
//                    }
//                    item(span = { GridItemSpan(maxLineSpan) }) {
//                        LoadMore(
//                            isLoading = isLoadingMore,
//                            hasMore = hasMore
//                        )
//                    }
//                }
//            }
//        }
//    }
}