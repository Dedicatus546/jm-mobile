package com.par9uet.jm.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.par9uet.jm.router.ComicSearchResultRoute
import com.par9uet.jm.router.LocalComicReadRoute
import com.par9uet.jm.ui.components.ComicContentTag
import com.par9uet.jm.ui.components.ComicCoverImage
import com.par9uet.jm.ui.components.ComicRoleTag
import com.par9uet.jm.ui.components.ComicWorkTag
import com.par9uet.jm.ui.components.ErrorTips
import com.par9uet.jm.ui.provider.LocalMainNavController
import com.par9uet.jm.ui.viewModel.LocalComicDetailViewModel
import com.par9uet.jm.utils.shimmer

@Composable
private fun LocalComicDetailSkeleton() {
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

@Composable
fun LocalComicDetailScreen(
    comicId: Int,
) {
    val localComicDetailViewModel: LocalComicDetailViewModel = hiltViewModel()
    val mainNavController = LocalMainNavController.current
    val scrollState = rememberScrollState()
    val comicDetailState by localComicDetailViewModel.comicDetailState.collectAsState()
    val isFirstLoading by localComicDetailViewModel.isFirstLoading.collectAsState()

    LaunchedEffect(Unit) {
        if (comicDetailState.data != null) {
            return@LaunchedEffect
        }
        localComicDetailViewModel.getComicDetail(comicId)
    }

    if (comicDetailState.isLoading && isFirstLoading) {
        LocalComicDetailSkeleton()
        return
    }

    if (comicDetailState.isError) {
        ErrorTips(
            errorMsg = comicDetailState.errorMsg
        ) {
            localComicDetailViewModel.getComicDetail(comicId)
        }
        return
    }

    LaunchedEffect(Unit) {
        localComicDetailViewModel.updateIsFirstLoading(false)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (comicDetailState.data != null) {
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
                    Spacer(modifier = Modifier.weight(1f))
                    Button(onClick = {
                        mainNavController.navigate(
                            LocalComicReadRoute(
                                comicId = comicId
                            )
                        )
                    }) {
                        Text("开始阅读")
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
                    localComicDetailViewModel.getComicDetail(comicId)
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
                        localComic = comicDetailState.data!!,
                        showIdChip = true
                    )
                    Column(
                        modifier = Modifier.padding(horizontal = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // comic name
                        Text(
                            modifier = Modifier.padding(top = 10.dp),
                            text = comic.name + if (comic.chapterName.isEmpty()) "" else " ${comic.chapterName}",
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
                                            mainNavController.navigate(
                                                ComicSearchResultRoute(
                                                    searchContent = it
                                                )
                                            )
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
                        // TODO 是否要显示？
                        // Row(
                        //     horizontalArrangement = Arrangement.spacedBy(8.dp)
                        // ) {
                        //     ComicInfoListItem(
                        //         modifier = Modifier.weight(.5f),
                        //         icon = Icons.Default.Favorite,
                        //         label = "喜爱人数",
                        //         value = comic.likeCount.toString()
                        //     )
                        //     ComicInfoListItem(
                        //         modifier = Modifier.weight(.5f),
                        //         icon = Icons.Default.RemoveRedEye,
                        //         label = "浏览量",
                        //         value = comic.readCount.toString()
                        //     )
                        // }
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