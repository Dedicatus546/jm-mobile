package com.par9uet.jm.ui.screens

import android.annotation.SuppressLint
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
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
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
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.par9uet.jm.ui.components.ComicContentTag
import com.par9uet.jm.ui.components.ComicCoverImage
import com.par9uet.jm.ui.components.ComicRoleTag
import com.par9uet.jm.ui.components.ComicWorkTag
import com.par9uet.jm.ui.viewModel.ComicDetailViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ComicInfoListItem(
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
            modifier = Modifier
                .width(50.dp)
                .height(50.dp),
            colors = AssistChipDefaults.assistChipColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
            ),
            onClick = {
                // TODO
            },
            label = {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                )
            }
        )
        Column {
            Text(text = label, fontSize = 11.sp)
            Text(text = value)
        }
    }
}

// https://cdn-msp3.jmapinodeudzn.net/media/albums/467243_3x4.jpg
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalLayoutApi::class
)
@Composable
fun ComicDetailScreen(
    id: Int,
    comicDetailViewModel: ComicDetailViewModel = koinViewModel(),
) {
    val mainNavController = LocalMainNavController.current
    val scrollState = rememberScrollState()
    val uiState = comicDetailViewModel.comicDetailBaseUIState

    LaunchedEffect(Unit) {
        comicDetailViewModel.getComicDetail(id)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
        },
        bottomBar = {
            if (uiState.hasData) {
                val comic = uiState.data!!
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 60.dp)
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .drawBehind {
                            drawLine(
                                color = Color(192, 192, 192, 255),
                                start = Offset(0f, 0f),
                                end = Offset(size.width, 0f),
                                strokeWidth = 1.dp.toPx()
                            )
                        }
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(
                        10.dp,
                        Alignment.CenterHorizontally
                    )
                ) {
                    IconButton(
                        modifier = Modifier.height(36.dp),
                        onClick = {
                            if (!comic.isLike) {
                                comicDetailViewModel.likeComic(comic.id)
                            }
                        }
                    ) {
                        if (comic.isLike) {
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
                    IconButton(
                        modifier = Modifier.height(36.dp),
                        onClick = {
                            if (comic.isCollect) {
                                comicDetailViewModel.unCollect(comic.id)
                            } else {
                                comicDetailViewModel.collect(comic.id)
                            }
                        },
                    ) {
                        if (comic.isCollect) {
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
                    Spacer(modifier = Modifier.weight(1f))
                    Button(modifier = Modifier.height(36.dp), onClick = {
                        mainNavController.navigate("comicRead/${comic.id}")
                    }) {
                        Text("开始阅读")
                    }
                }
            }
        }
    ) { innerPadding ->
        if (uiState.isInitializing) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (uiState.hasData) {
            PullToRefreshBox(
                isRefreshing = uiState.isRefreshing,
                state = rememberPullToRefreshState(),
                onRefresh = {
                    comicDetailViewModel.getComicDetail(id)
                },
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                val comic = uiState.data!!
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
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
                                            mainNavController.navigate("comicQuickSearch/$it")
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
                        if (comic.tagList.isNotEmpty()) {
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(5.dp),
                                verticalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                comic.tagList.forEach {
                                    key(it) {
                                        ComicContentTag(it)
                                    }
                                }
                            }
                        }

                        // comic role list
                        if (comic.roleList.isNotEmpty()) {
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(5.dp),
                                verticalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                comic.roleList.forEach {
                                    key(it) {
                                        ComicRoleTag(it)
                                    }
                                }
                            }
                        }
                        if (comic.workList.isNotEmpty()) {
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(5.dp),
                                verticalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                comic.workList.forEach {
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