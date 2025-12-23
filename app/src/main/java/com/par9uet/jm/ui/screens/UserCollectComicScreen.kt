package com.par9uet.jm.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.par9uet.jm.data.models.CollectComicOrderFilter
import com.par9uet.jm.ui.components.Comic
import com.par9uet.jm.ui.components.ComicSkeleton
import com.par9uet.jm.ui.components.CommonScaffold
import com.par9uet.jm.ui.components.PullRefreshAndLoadMoreGrid2
import com.par9uet.jm.ui.viewModel.UserViewModel
import org.koin.compose.viewmodel.koinActivityViewModel


@Composable
private fun UserCollectComicSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            CollectComicOrderFilter.entries.forEachIndexed { index, item ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = CollectComicOrderFilter.entries.size
                    ),
                    onClick = {

                    },
                    selected = item.value == CollectComicOrderFilter.COLLECT_TIME.value,
                    label = {
                        Text(item.label)
                    }
                )
            }
        }
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            maxItemsInEachRow = 3,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.Top)
        ) {
            for (i in 0 until 18) {
                key(i) {
                    ComicSkeleton(
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserCollectComicScreen(
    userViewModel: UserViewModel = koinActivityViewModel()
) {
    val collectComicLazyPagingItems = userViewModel.collectComicPager.collectAsLazyPagingItems()
    val order by userViewModel.collectComicOrder.collectAsState()
    CommonScaffold(
        title = "我的收藏",
    ) {
        if (collectComicLazyPagingItems.loadState.refresh is LoadState.Loading && collectComicLazyPagingItems.itemCount == 0) {
            UserCollectComicSkeleton()
            return@CommonScaffold
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                CollectComicOrderFilter.entries.forEachIndexed { index, item ->
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = CollectComicOrderFilter.entries.size
                        ),
                        onClick = {
                            userViewModel.changeCollectComicOrder(item)
                        },
                        selected = item.value == order.value,
                        label = {
                            Text(item.label)
                        }
                    )
                }
            }
            PullRefreshAndLoadMoreGrid2(
                modifier = Modifier.weight(1f),
                lazyPagingItems = collectComicLazyPagingItems,
                key = { it.id },
                columns = GridCells.Fixed(3),
            ) {
                Comic(it)
            }
        }
    }
}