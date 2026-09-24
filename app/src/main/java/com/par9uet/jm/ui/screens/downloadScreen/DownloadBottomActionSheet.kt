package com.par9uet.jm.ui.screens.downloadScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.par9uet.jm.data.models.DownloadStatus
import com.par9uet.jm.database.model.LocalComic
import com.par9uet.jm.ui.components.BottomSheetActionButton
import com.par9uet.jm.ui.models.CommonUIState
import com.par9uet.jm.ui.provider.LocalDownloadManager
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadBottomActionSheet(
    localComic: LocalComic,
    sheetState: SheetState = rememberBottomSheetState(
        initialValue = SheetValue.Hidden
    ),
    onDismissRequest: () -> Unit,
) {
    val downloadManager = LocalDownloadManager.current
    val coroutineScope = rememberCoroutineScope()
    val deleteStatus by remember {
        derivedStateOf {
            val comicId = localComic.comicId
            downloadManager.deleteStatusMap.getOrElse(comicId) {
                CommonUIState(
                    isLoading = false
                )
            }
        }
    }
    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismissRequest
    ) {
        LazyRow(
            modifier = Modifier.padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                BottomSheetActionButton(
                    enabled = !deleteStatus.isLoading,
                    loading = deleteStatus.isLoading,
                    onClick = {
                        downloadManager.delete(localComic.comicId)
                        coroutineScope.launch {
                            sheetState.hide()
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "删除"
                        )
                    }
                ) {
                    Text("删除")
                }
            }
            if (localComic.status === DownloadStatus.ERROR || localComic.status === DownloadStatus.PAUSE) {
                item {
                    BottomSheetActionButton(
                        onClick = {
                            downloadManager.restart(localComic.comicId)
                            coroutineScope.launch {
                                sheetState.hide()
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Replay,
                                contentDescription = "恢复"
                            )
                        }
                    ) {
                        Text("恢复")
                    }
                }
            }
        }
    }
}