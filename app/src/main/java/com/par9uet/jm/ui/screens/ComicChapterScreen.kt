package com.par9uet.jm.ui.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.Downloading
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.par9uet.jm.data.models.ComicChapter
import com.par9uet.jm.ui.components.CommonScaffold
import com.par9uet.jm.ui.provider.LocalMainNavController
import com.par9uet.jm.ui.provider.LocalToastManager
import com.par9uet.jm.ui.state.rememberTabIndexState
import com.par9uet.jm.ui.viewModel.ComicChapterDownloadViewModel
import kotlinx.coroutines.launch

@Composable
fun ComicChapterReadScreen(
    comicChapterList: List<ComicChapter>
) {
    val mainNavController = LocalMainNavController.current
    CommonScaffold(title = "选择章节") {
        ComicChapterSelect(
            comicChapterList = comicChapterList,
            onClick = {
                mainNavController.navigate("comicRead/${it.id}")
            }
        )
    }
}

@Composable
fun ComicChapterDownloadScreen(
    comicChapterList: List<ComicChapter>,
) {
    val comicChapterDownloadViewModel: ComicChapterDownloadViewModel = hiltViewModel()
    val toastManager = LocalToastManager.current

    val downloadComicMap by comicChapterDownloadViewModel.downloadComicMapFlow.collectAsState()
    val waitDownloadComicId by comicChapterDownloadViewModel.waitDownloadComicIdFlow.collectAsState()
    LaunchedEffect(Unit) {
        comicChapterDownloadViewModel.updateComicIdListFilter(comicChapterList.map { it.id })
    }
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            comicChapterDownloadViewModel.downloadComic(waitDownloadComicId)
        } else {
            // TODO 提示？
        }
    }
    CommonScaffold(title = "下载章节") {
        ComicChapterSelect(
            comicChapterList = comicChapterList,
            onClick = {
                if (downloadComicMap.getOrElse(it.id) { null } != null) {
                    val downloadComic = downloadComicMap.getValue(it.id)
                    when (downloadComic.status) {
                        "pending" -> {
                            toastManager.show("等待下载中，请勿重复点击")
                        }

                        "downloading" -> {
                            toastManager.show("下载中，请勿重复点击")
                        }

                        "complete" -> {
                            toastManager.show("已下载，请勿重复下载")
                            // TODO 提示重新下载
                        }
                    }
                } else {
                    comicChapterDownloadViewModel.updateWaitDownloadComicId(it.id)
                    val per =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.POST_NOTIFICATIONS else "android.permission.POST_NOTIFICATIONS"
                    notificationPermissionLauncher.launch(per)
                }
            }
        ) { index, chapter, chapterGroup, page, list, groupSize ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = "第${index + (chapterGroup.size - page - 1) * groupSize + 1}话 ${chapter.name}",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (downloadComicMap.getOrElse(chapter.id) { null } != null) {
                    Spacer(modifier = Modifier.width(10.dp))
                    val downloadComic = downloadComicMap.getValue(chapter.id)
                    when (downloadComic.status) {
                        "pending" -> {
                            Icon(
                                imageVector = Icons.Default.Pending,
                                contentDescription = "等待中",
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("等待中")
                        }

                        "downloading" -> {
                            Icon(
                                imageVector = Icons.Default.Downloading,
                                contentDescription = "下载中",
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("下载中")
                        }

                        "complete" -> {
                            Icon(
                                imageVector = Icons.Default.DownloadDone,
                                contentDescription = "已下载",
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("已下载")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ComicChapterSelect(
    comicChapterList: List<ComicChapter>,
    onClick: (chapter: ComicChapter) -> Unit,
    content: (@Composable (index: Int, chapter: ComicChapter, chapterGroup: List<Pair<String, List<ComicChapter>>>, page: Int, list: List<ComicChapter>, groupSize: Int) -> Unit)? = null
) {
    val coroutineScope = rememberCoroutineScope()
    val groupSize = 30
    val chapterGroup by remember(comicChapterList) {
        derivedStateOf {
            comicChapterList.withIndex().groupBy {
                val start = it.index / groupSize * groupSize + 1
                val end = (start + groupSize - 1).coerceAtMost(comicChapterList.size)
                "第$start-${end}话"
            }.mapValues { it.value.map { item -> item.value } }.toList().reversed()
        }
    }
    val pagerState = rememberPagerState(initialPage = 0) {
        chapterGroup.size
    }
    val selectedTabIndexState = rememberTabIndexState()

    LaunchedEffect(pagerState.currentPage) {
        selectedTabIndexState.value = pagerState.currentPage
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        PrimaryScrollableTabRow(
            containerColor = Color.Transparent,
            selectedTabIndex = selectedTabIndexState.value,
            edgePadding = 0.dp,
            scrollState = rememberScrollState()
        ) {
            chapterGroup.forEachIndexed { index, item ->
                key(item.first) {
                    Tab(
                        selected = selectedTabIndexState.value == index,
                        onClick = {
                            selectedTabIndexState.value = index
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(selectedTabIndexState.value)
                            }
                        },
                        text = {
                            Text(
                                text = item.first,
                                maxLines = 1,
                            )
                        }
                    )
                }
            }
        }
        HorizontalPager(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            state = pagerState
        ) { page ->
            val list = chapterGroup[page].second
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                LazyVerticalGrid(
                    contentPadding = PaddingValues(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.Top),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    columns = GridCells.Fixed(1),
                ) {
                    itemsIndexed(list, key = { _, item -> item.id }) { index, item ->
                        ListItem(
                            modifier = Modifier.clickable(
                                onClick = {
                                    onClick(item)
                                }
                            ),
                            colors = ListItemDefaults.colors().copy(
                                containerColor = Color.Transparent
                            ),
                            headlineContent = {
                                if (content != null) {
                                    content(index, item, chapterGroup, page, list, groupSize)
                                } else {
                                    Text(
                                        modifier = Modifier.fillMaxWidth(),
                                        text = "第${index + (chapterGroup.size - page - 1) * groupSize + 1}话 ${item.name}",
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                }
                            },
                        )
                    }
                }
            }
        }
    }
}
