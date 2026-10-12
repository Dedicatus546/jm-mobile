package com.par9uet.jm.ui.screens.downloadScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.par9uet.jm.constant.downloadStatusTextMap
import com.par9uet.jm.data.models.DownloadStatus
import com.par9uet.jm.database.model.LocalComic
import com.par9uet.jm.router.LocalComicDetailRoute
import com.par9uet.jm.ui.components.CommonScaffold
import com.par9uet.jm.ui.components.FilterItem
import com.par9uet.jm.ui.components.PullRefreshAndLoadMoreGrid
import com.par9uet.jm.ui.models.CommonUIState
import com.par9uet.jm.ui.provider.LocalDownloadManager
import com.par9uet.jm.ui.provider.LocalMainNavController
import com.par9uet.jm.ui.provider.LocalToastManager
import com.par9uet.jm.ui.viewModel.DownloadViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadScreen() {
    val downloadViewModel: DownloadViewModel = hiltViewModel()

    val mainNavController = LocalMainNavController.current
    val downloadManager = LocalDownloadManager.current
    val toastManager = LocalToastManager.current

    val downloadFilterState by downloadViewModel.downloadFilterState.collectAsState()
    val downloadComicLazyPagingItems =
        downloadViewModel.localComicPager.collectAsLazyPagingItems()
    val onTabClick: (status: DownloadStatus) -> Unit = {
        downloadViewModel.updateDownloadStatusFilter(it)
    }
    var currentLocalComic by remember { mutableStateOf<LocalComic?>(null) }
    val sheetState = rememberBottomSheetState(
        initialValue = SheetValue.Hidden
    )
    var showBottomSheet by remember { mutableStateOf(false) }

    CommonScaffold(title = "下载") {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                downloadStatusTextMap.forEach { item ->
                    FilterItem(
                        label = item.value,
                        onClick = {
                            onTabClick(item.key)
                        },
                        active = downloadFilterState.status == item.key
                    )
                }
            }
            PullRefreshAndLoadMoreGrid(
                modifier = Modifier.fillMaxWidth(),
                lazyPagingItems = downloadComicLazyPagingItems,
                itemKey = { it.comicId },
                columns = GridCells.Fixed(3)
            ) {
                DownloadListItem(
                    localComic = it,
                    onClick = {
                        when (it.status) {
                            DownloadStatus.COMPLETE -> {
                                mainNavController.navigate(
                                    LocalComicDetailRoute(
                                        comicId = it.comicId
                                    )
                                )
                            }

                            DownloadStatus.PAUSE -> {
                                // TODO 弹框确认
                                downloadManager.restart(it.comicId)
                            }

                            DownloadStatus.DOWNLOADING -> {
                                toastManager.show("任务正在下载中，请稍等")
                            }

                            DownloadStatus.ERROR -> {
                                // TODO 弹框确认
                                downloadManager.restart(it.comicId)
                            }

                            DownloadStatus.PENDING -> {
                                toastManager.show("任务等待下载中")
                            }
                        }

                    },
                    onLongClick = {
                        currentLocalComic = it.copy()
                        showBottomSheet = true
                    }
                )
            }
        }
    }
    if (showBottomSheet && currentLocalComic != null) {
        DownloadBottomActionSheet(
            localComic = currentLocalComic!!,
            sheetState = sheetState,
            onDismissRequest = {
                showBottomSheet = false
            }
        )
    }
}