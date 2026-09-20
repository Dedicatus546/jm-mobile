package com.par9uet.jm.ui.screens.comicChapterScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.par9uet.jm.constant.downloadStatusIconMap
import com.par9uet.jm.constant.downloadStatusTextMap
import com.par9uet.jm.data.models.Comic
import com.par9uet.jm.data.models.ComicChapter
import com.par9uet.jm.ui.models.CommonUIState
import com.par9uet.jm.ui.provider.LocalDownloadManager
import com.par9uet.jm.ui.viewModel.ComicChapterViewModel

@Composable
private fun ActionListItem(
    enabled: Boolean = true,
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    overlineContent: @Composable (() -> Unit)? = null,
    supportingContent: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    ListItem(
        enabled = enabled,
        modifier = Modifier.clickable(onClick = {
            onClick?.invoke()
        }),
        content = content,
        leadingContent = leadingContent,
        trailingContent = trailingContent,
        overlineContent = overlineContent,
        supportingContent = supportingContent,
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComicChapterBottomActionSheet(
    comic: Comic,
    comicChapter: ComicChapter,
    sheetState: SheetState = rememberBottomSheetState(
        initialValue = SheetValue.Hidden
    ),
    onDismissRequest: () -> Unit,
    comicChapterViewModel: ComicChapterViewModel,
) {
    val downloadManager = LocalDownloadManager.current
    val downloadState by remember(comicChapter) {
        derivedStateOf {
            downloadManager.downloadStatusMap.getOrElse(comicChapter.id) {
                CommonUIState(
                    isLoading = false
                )
            }
        }
    }
    val localComic by comicChapterViewModel.localComic.collectAsState()

    LaunchedEffect(comicChapter) {
        comicChapterViewModel.loadDownloadComic(comicChapter.id)
    }

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismissRequest
    ) {
        if (localComic == null) {
            ActionListItem(
                enabled = !downloadState.isLoading,
                onClick = {
                    downloadManager.downloadComic(comic, comicChapter)
                },
                leadingContent = {
                    if (downloadState.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp))
                    } else {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "下载"
                        )
                    }
                },
            ) {
                Text("下载")
            }
        } else {
            ActionListItem(
                leadingContent = {
                    if (downloadState.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp))
                    } else {
                        Icon(
                            imageVector = downloadStatusIconMap[localComic!!.status]!!,
                            contentDescription = "下载"
                        )
                    }
                },
            ) {
                Text(downloadStatusTextMap[localComic!!.status]!!)
            }
        }
    }
}