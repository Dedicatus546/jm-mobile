package com.par9uet.jm.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.par9uet.jm.ui.components.ComicImage
import com.par9uet.jm.ui.components.ComicContentTag
import com.par9uet.jm.ui.components.ComicRoleTag
import com.par9uet.jm.ui.components.ComicWorkTag
import com.par9uet.jm.viewModel.ComicDetailViewModel
import com.par9uet.jm.viewModel.SettingViewModel
import org.koin.androidx.compose.koinViewModel

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
    val scrollState = rememberScrollState()
    val comic = comicDetailViewModel.comic
    val loading = comicDetailViewModel.loading

    LaunchedEffect(Unit) {
        comicDetailViewModel.getComicDetail(id)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
//            TopAppBar(
//                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = MaterialTheme.colorScheme.primary
//                ),
//                title = {
//                    Text(
//                        "漫画详情",
//                        color = MaterialTheme.colorScheme.surface,
//                        maxLines = 1,
//                        overflow = TextOverflow.Ellipsis
//                    )
//                }
//            )
        },
        bottomBar = {
            if (!loading) {
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
//                    .shadow(
//                        elevation = 16.dp,
//                        shape = RoundedCornerShape(12.dp),
//                        clip = false,
//                        spotColor = Color.Black.copy(alpha = 0.3f),
////                        offset = DpOffset(0.dp, (-4).dp) // 关键：负Y偏移实现上阴影
//                    )
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(
                        10.dp,
                        Alignment.CenterHorizontally
                    )
                ) {
//                    IconButton(
//                        onClick = {
//                            // TODO
//                        },
//                        colors = IconButtonDefaults.iconButtonColors(
//                            containerColor = MaterialTheme.colorScheme.primary, // 背景色
//                            contentColor = MaterialTheme.colorScheme.onPrimary  // 图标颜色
//                        )
//                    ) {
//                        Icon(
//                            imageVector = Icons.AutoMirrored.Filled.Message,
//                            contentDescription = "评论",
//                            tint = Color.White
//                        )
//                    }
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
//                        TODO
                    }) {
                        Text("开始阅读")
                    }
                }
            }
        }
    ) { innerPadding ->
        if (loading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(scrollState)
            ) {
                ComicImage(
                    comic = comic,
                    showLikeCountChip = true,
                    showReadCountChip = true,
                    showIdChip = true
                )
                Column(
                    modifier = Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // comic name
                    Text(
                        text = comic.name,
                        fontSize = 18.sp,
                        lineHeight = 27.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    // comic author list
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        comic.authorList.forEach {
                            key(it) {
                                Text(
                                    text = it,
                                    color = Color.Gray,
                                    fontSize = 18.sp,
                                    lineHeight = 27.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                    }
                    if (comic.tagList.isNotEmpty()) {
                        CompositionLocalProvider(
                            // Deprecated starting on Version 1.3.0-alpha04
                            // https://developer.android.com/jetpack/androidx/releases/compose-material3#1.3.0-alpha04
                            // LocalMinimumInteractiveComponentEnforcement provides false
                            // 去除 m3 默认的最小高度
                            LocalMinimumInteractiveComponentSize provides Dp.Unspecified
                        ) {
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
                    }
                    // comic role list
                    if (comic.roleList.isNotEmpty()) {
                        CompositionLocalProvider(
                            // Deprecated starting on Version 1.3.0-alpha04
                            // https://developer.android.com/jetpack/androidx/releases/compose-material3#1.3.0-alpha04
                            // LocalMinimumInteractiveComponentEnforcement provides false
                            // 去除 m3 默认的最小高度
                            LocalMinimumInteractiveComponentSize provides Dp.Unspecified
                        ) {
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
                    }
                    if (comic.workList.isNotEmpty()) {
                        CompositionLocalProvider(
                            // Deprecated starting on Version 1.3.0-alpha04
                            // https://developer.android.com/jetpack/androidx/releases/compose-material3#1.3.0-alpha04
                            // LocalMinimumInteractiveComponentEnforcement provides false
                            // 去除 m3 默认的最小高度
                            LocalMinimumInteractiveComponentSize provides Dp.Unspecified
                        ) {
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
                    }
                }
            }
        }
    }
}