package com.par9uet.jm.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.par9uet.jm.data.models.CollectComicOrderFilter
import com.par9uet.jm.ui.components.Comic
import com.par9uet.jm.ui.components.CommonScaffold
import com.par9uet.jm.ui.components.PullRefreshAndLoadMoreGrid
import com.par9uet.jm.ui.viewModel.UserViewModel
import org.koin.compose.viewmodel.koinActivityViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserCollectComicScreen(
    userViewModel: UserViewModel = koinActivityViewModel()
) {
    val collectComicState by userViewModel.collectComicState.collectAsState()
    var order by remember { mutableStateOf(CollectComicOrderFilter.COLLECT_TIME) }
    LaunchedEffect(Unit) {
        if (collectComicState.list.isNotEmpty()) {
            return@LaunchedEffect
        }
        userViewModel.getCollectComicList("refresh", order.value)
    }
    CommonScaffold(
        title = "我的收藏",
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                CollectComicOrderFilter.entries.forEachIndexed { index, item ->
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = CollectComicOrderFilter.entries.size
                        ),
                        onClick = {
                            order = item
                            userViewModel.getCollectComicList("refresh", order.value)
                        },
                        selected = item.value == order.value,
                        label = {
                            Text(item.label)
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            PullRefreshAndLoadMoreGrid(
                modifier = Modifier.weight(1f),
                list = collectComicState.list,
                isRefreshing = collectComicState.isRefreshing,
                isMoreLoading = collectComicState.isMoreLoading,
                hasMore = collectComicState.hasMore,
                onRefresh = {
                    userViewModel.getCollectComicList("refresh", order.value)
                },
                onLoadMore = {
                    userViewModel.getCollectComicList("loadMore", order.value)
                },
                columns = GridCells.Fixed(3),
            ) {
                Comic(it)
            }
        }
    }
}