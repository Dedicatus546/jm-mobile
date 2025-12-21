package com.par9uet.jm.ui.screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.par9uet.jm.data.models.ComicSearchOrderFilter
import com.par9uet.jm.ui.components.Comic
import com.par9uet.jm.ui.components.CommonScaffold
import com.par9uet.jm.ui.components.PullRefreshAndLoadMoreGrid
import com.par9uet.jm.ui.viewModel.ComicViewModel
import org.koin.compose.viewmodel.koinActivityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComicSearchResultScreen(
    searchContent: String,
    comicViewModel: ComicViewModel = koinActivityViewModel()
) {
    val comicSearchResultState by comicViewModel.comicSearchResultState.collectAsState()
    var order by remember { mutableStateOf(ComicSearchOrderFilter.NEWEST) }
    LaunchedEffect(Unit) {
        if (comicSearchResultState.list.isNotEmpty()) {
            return@LaunchedEffect
        }
        comicViewModel.getComicSearchList("refresh", searchContent, order)
    }
    CommonScaffold(title = "搜索：$searchContent") {
        PullRefreshAndLoadMoreGrid(
            list = comicSearchResultState.list,
            isRefreshing = comicSearchResultState.isRefreshing,
            isMoreLoading = comicSearchResultState.isMoreLoading,
            hasMore = comicSearchResultState.hasMore,
            onRefresh = {
                comicViewModel.getComicSearchList("refresh", searchContent, order)
            },
            onLoadMore = {
                comicViewModel.getComicSearchList("loadMore", searchContent, order)
            },
            columns = GridCells.Fixed(3),
            stickyHeaderContent = {
                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    ComicSearchOrderFilter.entries.forEachIndexed { index, item ->
                        key(
                            item.value
                        ) {
                            SegmentedButton(
                                shape = SegmentedButtonDefaults.itemShape(
                                    index = index,
                                    count = ComicSearchOrderFilter.entries.size
                                ),
                                onClick = {
                                    order = item
                                    comicViewModel.getComicSearchList("refresh", searchContent, order)
                                },
                                selected = item.value == order.value,
                                label = {
                                    Text(item.label)
                                }
                            )
                        }
                    }
                }
            }
        ) {
            Comic(it)
        }
    }
}