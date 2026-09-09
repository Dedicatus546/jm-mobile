package com.par9uet.jm.ui.screens

import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Message
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.Downloading
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.par9uet.jm.ui.components.ComicContentTag
import com.par9uet.jm.ui.components.ComicCoverImage
import com.par9uet.jm.ui.components.ComicRoleTag
import com.par9uet.jm.ui.components.ComicWorkTag
import com.par9uet.jm.ui.components.ErrorTips
import com.par9uet.jm.ui.provider.LocalMainNavController
import com.par9uet.jm.ui.provider.LocalToastManager
import com.par9uet.jm.ui.provider.LocalUserManager
import com.par9uet.jm.ui.viewModel.ComicDetailViewModel
import com.par9uet.jm.utils.json
import com.par9uet.jm.utils.shimmer


@Composable
private fun ComicInfoListItem(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    value: String,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AssistChip(
            border = null,
            modifier = Modifier
                .width(50.dp)
                .height(50.dp),
            colors = AssistChipDefaults.assistChipColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
            ),
            onClick = {},
            label = {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        )
        Column {
            Text(text = label, fontSize = 14.sp)
            Text(text = value)
        }
    }
}

@Composable
private fun ComicDetailSkeleton() {
    val scrollState = rememberScrollState()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.75f)
                    .background(color = MaterialTheme.colorScheme.surfaceContainerHighest)
                    .shimmer()
            )
            Column(
                modifier = Modifier.padding(horizontal = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.8f) // 标题长度通常不到头
                        .height(36.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(color = MaterialTheme.colorScheme.surfaceContainerHighest)
                        .shimmer()
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.4f) // 标题长度通常不到头
                        .height(34.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(color = MaterialTheme.colorScheme.surfaceContainerHighest)
                        .shimmer()
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val list = listOf(40.dp, 60.dp, 50.dp)
                    for (i in 0 until 6) {
                        key(i) {
                            Box(
                                modifier = Modifier
                                    .width(list[i % list.size])
                                    .height(32.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(color = MaterialTheme.colorScheme.surfaceContainerHighest)
                                    .shimmer()
                            )
                        }
                    }
                }
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val list = listOf(80.dp, 60.dp, 70.dp)
                    for (i in 0 until 4) {
                        key(i) {
                            Box(
                                modifier = Modifier
                                    .width(list[i % list.size])
                                    .height(32.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(color = MaterialTheme.colorScheme.surfaceContainerHighest)
                                    .shimmer()
                            )
                        }
                    }
                }
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val list = listOf(70.dp, 50.dp, 60.dp)
                    for (i in 0 until 5) {
                        key(i) {
                            Box(
                                modifier = Modifier
                                    .width(list[i % list.size])
                                    .height(32.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(color = MaterialTheme.colorScheme.surfaceContainerHighest)
                                    .shimmer()
                            )
                        }
                    }
                }
                Box {}
            }
        }
    }
}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class,
    ExperimentalLayoutApi::class
)
@Composable
fun ComicDetailScreen(
    id: Int,
) {
    val comicDetailViewModel: ComicDetailViewModel = hiltViewModel()
    val toastManager = LocalToastManager.current
    val userManager = LocalUserManager.current
    val mainNavController = LocalMainNavController.current
    val scrollState = rememberScrollState()
    val comicDetailState by comicDetailViewModel.comicDetailState.collectAsState()
    val isFirstLoading by comicDetailViewModel.isFirstLoading.collectAsState()
    val likeComicState by comicDetailViewModel.likeComicState.collectAsState()
    val collectComicState by comicDetailViewModel.collectComicState.collectAsState()
    val downloadComicId by comicDetailViewModel.downloadComicId.collectAsState()
    val downloadComic by comicDetailViewModel.downloadComicState.collectAsState()
    val downloadState by comicDetailViewModel.downloadState.collectAsState()
    val isLogin by userManager.isLoginState.collectAsState(false)

    LaunchedEffect(Unit) {
        if (comicDetailState.data != null) {
            return@LaunchedEffect
        }
        comicDetailViewModel.getComicDetail(id)
    }

    LaunchedEffect(Unit) {
        if (downloadComicId != 0) {
            return@LaunchedEffect
        }
        comicDetailViewModel.loadDownloadComic(id)
    }

    if (comicDetailState.isLoading && isFirstLoading) {
        ComicDetailSkeleton()
        return
    }

    if (comicDetailState.isError) {
        ErrorTips(
            errorMsg = comicDetailState.errorMsg
        ) {
            comicDetailViewModel.getComicDetail(id)
        }
        return
    }

    LaunchedEffect(Unit) {
        comicDetailViewModel.updateIsFirstLoading(false)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (comicDetailState.data != null) {
                val comic = comicDetailState.data!!
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 80.dp)
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(
                        10.dp,
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row {
                        IconButton(
                            enabled = !likeComicState.isLoading && !comic.isLike,
                            onClick = {
                                comicDetailViewModel.likeComic(comic.id)
                            }
                        ) {
                            if (likeComicState.isLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp))
                            } else if (comic.isLike) {
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
                        }
                        if (isLogin) {
                            IconButton(
                                enabled = !collectComicState.isLoading,
                                onClick = {
                                    if (comic.isCollect) {
                                        comicDetailViewModel.unCollect(comic.id)
                                    } else {
                                        comicDetailViewModel.collect(comic.id)
                                    }
                                },
                            ) {
                                if (collectComicState.isLoading) {
                                    CircularProgressIndicator(modifier = Modifier.size(16.dp))
                                } else if (comic.isCollect) {
                                    Icon(
                                        imageVector = Icons.Filled.Bookmark,
                                        contentDescription = "收藏",
                                        tint = Color.Yellow
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Filled.BookmarkBorder,
                                        contentDescription = "收藏",
                                    )
                                }
                            }
                        }
                        IconButton(
                            onClick = {
                                mainNavController.navigate("comment/${comic.id}")
                            },
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.Message,
                                contentDescription = "评论",
                            )
                        }
                        IconButton(
                            onClick = {
                                mainNavController.navigate(
                                    "comicRelate/${
                                        Uri.encode(
                                            json.encodeToString(
                                                comic.relateComicList
                                            )
                                        )
                                    }"
                                )
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "相关本子",
                            )
                        }
//                        IconButton(
//                            onClick = {
//                                // TODO 跳转到分享页
//                            },
//                        ) {
//                            Icon(
//                                imageVector = Icons.Default.Share,
//                                contentDescription = "分享",
//                            )
//                        }
                        if (comic.comicChapterList.orEmpty().isNotEmpty()) {
                            IconButton(
                                onClick = {
                                    mainNavController.navigate(
                                        "comicChapterDownload/${
                                            Uri.encode(
                                                json.encodeToString(
                                                    comic.comicChapterList
                                                )
                                            )
                                        }"
                                    )
                                },
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Download,
                                    contentDescription = "下载",
                                )
                            }
                        } else {
                            if (downloadComic != null) {
                                val status = downloadComic!!.status

                                when (status) {
                                    "pending" -> {
                                        IconButton(
                                            onClick = {
                                                toastManager.show("等待下载中，请勿重复点击")
                                            },
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Pending,
                                                contentDescription = "等待中",
                                            )
                                        }
                                    }

                                    "downloading" -> {
                                        IconButton(
                                            onClick = {
                                                toastManager.show("下载中，请勿重复点击")
                                            },
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Downloading,
                                                contentDescription = "下载中",
                                            )
                                        }
                                    }

                                    "complete" -> {
                                        IconButton(
                                            onClick = {
                                                toastManager.show("已下载，请勿重复下载")
                                                // TODO 提示重新下载
                                            },
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.DownloadDone,
                                                contentDescription = "已下载",
                                            )
                                        }
                                    }
                                }
                            } else {
                                IconButton(
                                    enabled = !downloadState.isLoading,
                                    onClick = {
                                        comicDetailViewModel.downloadComic(comic)
                                    },
                                ) {
                                    if (downloadState.isLoading) {
                                        CircularProgressIndicator(modifier = Modifier.size(16.dp))
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.Download,
                                            contentDescription = "下载",
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    if (comic.comicChapterList.orEmpty().isEmpty()) {
                        Button(onClick = {
                            mainNavController.navigate("comicRead/${comic.id}")
                        }) {
                            Text("开始阅读")
                        }
                    } else {
                        Row {
                            Button(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                onClick = {
                                    mainNavController.navigate(
                                        "comicChapter/${
                                            Uri.encode(
                                                json.encodeToString(
                                                    comic.comicChapterList
                                                )
                                            )
                                        }"
                                    )
                                },
                                shape = RoundedCornerShape(
                                    topStart = 25.dp,
                                    bottomStart = 25.dp,
                                    topEnd = 0.dp,
                                    bottomEnd = 0.dp
                                )
                            ) {
                                Text("章节")
                            }
                            VerticalDivider(modifier = Modifier.height(40.dp))
                            Button(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                onClick = {
                                    mainNavController.navigate("comicRead/${comic.id}")
                                },
                                shape = RoundedCornerShape(
                                    topStart = 0.dp,
                                    bottomStart = 0.dp,
                                    topEnd = 25.dp,
                                    bottomEnd = 25.dp
                                )
                            ) {
                                Text("第1话")
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        if (comicDetailState.data != null) {
            val comic = comicDetailState.data!!
            PullToRefreshBox(
                isRefreshing = comicDetailState.isLoading,
                state = rememberPullToRefreshState(),
                onRefresh = {
                    comicDetailViewModel.getComicDetail(id)
                },
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState),
                ) {
                    ComicCoverImage(
                        comic = comic,
                        showIdChip = true
                    )
                    Column(
                        modifier = Modifier.padding(horizontal = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // comic name
                        Text(
                            modifier = Modifier.padding(top = 10.dp),
                            text = comic.name,
                            fontSize = 18.sp,
                            lineHeight = 1.5.em,
                            fontWeight = FontWeight.Bold,
                        )
                        // comic author list
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                            comic.authorList.forEach {
                                key(it) {
                                    Text(
                                        modifier = Modifier.clickable(onClick = {
                                            mainNavController.navigate("comicSearchResult/$it")
                                        }),
                                        text = it,
                                        color = Color.Gray,
                                        fontSize = 18.sp,
                                        lineHeight = 27.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                            }
                        }
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ComicInfoListItem(
                                modifier = Modifier.weight(.5f),
                                icon = Icons.Default.Favorite,
                                label = "喜爱人数",
                                value = comic.likeCount.toString()
                            )
                            ComicInfoListItem(
                                modifier = Modifier.weight(.5f),
                                icon = Icons.Default.RemoveRedEye,
                                label = "浏览量",
                                value = comic.readCount.toString()
                            )
                        }
                        if (comic.tagList.orEmpty().isNotEmpty()) {
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(5.dp),
                                verticalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                comic.tagList!!.filter { it.isNotEmpty() }.forEach {
                                    key(it) {
                                        ComicContentTag(it)
                                    }
                                }
                            }
                        }

                        // comic role list
                        if (comic.roleList.orEmpty().isNotEmpty()) {
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(5.dp),
                                verticalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                comic.roleList!!.filter { it.isNotEmpty() }.forEach {
                                    key(it) {
                                        ComicRoleTag(it)
                                    }
                                }
                            }
                        }
                        if (comic.workList.orEmpty().isNotEmpty()) {
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(5.dp),
                                verticalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                comic.workList!!.filter { it.isNotEmpty() }.forEach {
                                    key(it) {
                                        ComicWorkTag(it)
                                    }
                                }
                            }

                        }
                        Box {}
                    }
                }
            }
        }
    }
}