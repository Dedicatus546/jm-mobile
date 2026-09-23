package com.par9uet.jm.ui.screens.comicChapterScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.par9uet.jm.constant.downloadStatusIconMap
import com.par9uet.jm.constant.downloadStatusTextMap
import com.par9uet.jm.data.models.Comic
import com.par9uet.jm.data.models.ComicChapter
import com.par9uet.jm.data.models.DownloadStatus
import com.par9uet.jm.ui.models.CommonUIState
import com.par9uet.jm.ui.provider.LocalDownloadManager
import com.par9uet.jm.ui.viewModel.ComicChapterViewModel
import com.par9uet.jm.utils.log
import kotlin.math.roundToInt

@Composable
private fun ActionButton(
    enabled: Boolean = true,
    loading: Boolean = false,
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Surface(
        enabled = enabled,
        onClick = onClick,
        color = Color.Transparent,
        shape = CircleShape
    ) {
        Column(
            modifier = Modifier
                .width(80.dp)
                .padding(5.dp)
                .aspectRatio(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            if (loading) {
                CircularProgressIndicator(modifier = Modifier.size(30.dp))
            } else {
                Icon(
                    modifier = Modifier.size(30.dp),
                    imageVector = icon,
                    // TODO
                    contentDescription = ""
                )
            }
            Text(
                text = text,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun DownloadActionButton(
    comic: Comic,
    comicChapter: ComicChapter,
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
    val icon = when (localComic) {
        null -> Icons.Default.Download
        else -> downloadStatusIconMap[localComic!!.status]!!
    }
    val text = when (localComic) {
        null -> "下载"
        else -> {
            when (localComic!!.status) {
                DownloadStatus.DOWNLOADING -> {
                    val text = ((localComic!!.progress ?: 0f) * 100).roundToInt()
                    "$text%"
                }

                else -> downloadStatusTextMap[localComic!!.status]!!
            }
        }
    }

    ActionButton(
        enabled = !downloadState.isLoading,
        loading = downloadState.isLoading,
        icon = icon,
        text = text
    ) {
        log("actionbutton", "click download btn")
        downloadManager.downloadComic(comic, comicChapter)
    }
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
    LaunchedEffect(comicChapter) {
        comicChapterViewModel.loadDownloadComic(comicChapter.id)
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
                DownloadActionButton(
                    comic = comic,
                    comicChapter = comicChapter,
                    comicChapterViewModel = comicChapterViewModel
                )
            }
            item {
                ActionButton(
                    icon = Icons.Filled.BookmarkBorder,
                    text = "收藏"
                ) {
                    // TODO
                }
            }
            item {
                ActionButton(
                    icon = Icons.Filled.FavoriteBorder,
                    text = "喜爱"
                ) {
                    // TODO
                }
            }
        }
    }
}