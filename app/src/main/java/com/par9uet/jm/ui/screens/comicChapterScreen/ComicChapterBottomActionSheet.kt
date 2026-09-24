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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
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
import androidx.compose.ui.unit.dp
import com.par9uet.jm.constant.downloadStatusIconMap
import com.par9uet.jm.constant.downloadStatusTextMap
import com.par9uet.jm.data.models.Comic
import com.par9uet.jm.data.models.ComicChapter
import com.par9uet.jm.data.models.DownloadStatus
import com.par9uet.jm.ui.models.CommonUIState
import com.par9uet.jm.ui.provider.LocalDownloadManager
import com.par9uet.jm.ui.provider.LocalUserManager
import com.par9uet.jm.ui.viewModel.ComicChapterViewModel
import com.par9uet.jm.utils.log
import kotlin.math.roundToInt

@Composable
private fun ActionButton(
    enabled: Boolean = true,
    loading: Boolean = false,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    label: @Composable () -> Unit,
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
                icon()
            }
            label()
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
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = ""
            )
        },
        onClick = {
            downloadManager.downloadComic(comic, comicChapter)
        }
    ) {
        Text(text)
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
    val userManager = LocalUserManager.current
    val comicChapterState by comicChapterViewModel.comicDetailState.collectAsState()
    val collectComicState by comicChapterViewModel.collectComicState.collectAsState()
    val likeComicState by comicChapterViewModel.likeComicState.collectAsState()
    val isLogin by userManager.isLoginState.collectAsState(false)

    LaunchedEffect(comicChapter) {
        comicChapterViewModel.loadDownloadComic(comicChapter.id)
        comicChapterViewModel.getComicDetail(comicChapter.id)
        comicChapterViewModel.resetLikeComicState()
        comicChapterViewModel.resetCollectComicState()
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
            if (isLogin) {
                item {
                    ActionButton(
                        enabled = !collectComicState.isLoading,
                        loading = collectComicState.isLoading,
                        icon = {
                            if (comicChapterState.data?.isCollect ?: false) {
                                Icon(
                                    imageVector = Icons.Filled.Bookmark,
                                    contentDescription = "已收藏",
                                    tint = Color.Yellow
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Filled.BookmarkBorder,
                                    contentDescription = "收藏",
                                )
                            }
                        },
                        onClick = {
                            comicChapterState.data?.let {
                                if (it.isCollect) {
                                    comicChapterViewModel.unCollect(it.id)
                                } else {
                                    comicChapterViewModel.collect(it.id)
                                }
                            }
                        }
                    ) {
                        Text(if (comicChapterState.data?.isCollect ?: false) "已收藏" else "收藏")
                    }
                }
            }
            item {
                ActionButton(
                    enabled = !likeComicState.isLoading,
                    loading = likeComicState.isLoading,
                    icon = {
                        if (comicChapterState.data?.isLike ?: false) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "已喜欢",
                                tint = Color.Red
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.FavoriteBorder,
                                contentDescription = "喜欢",
                            )
                        }
                    },
                    onClick = {
                        comicChapterState.data?.let {
                            if (!it.isLike) {
                                comicChapterViewModel.likeComic(it.id)
                            }
                        }
                    }
                ) {
                    Text(if (comicChapterState.data?.isLike ?: false) "已喜欢" else "喜欢")
                }
            }
        }
    }
}