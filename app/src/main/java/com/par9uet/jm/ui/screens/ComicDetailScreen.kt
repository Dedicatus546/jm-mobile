package com.par9uet.jm.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Message
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.par9uet.jm.R
import com.par9uet.jm.viewModel.rememberAppNavigateViewModel

// https://cdn-msp3.jmapinodeudzn.net/media/albums/467243_3x4.jpg
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalLayoutApi::class
)
@Composable
fun ComicDetailScreen() {
    val appNavigateViewModel = rememberAppNavigateViewModel()
    val scrollState = rememberScrollState()
    val authorList = listOf("岛村")
    val tagList = listOf(
        "全彩",
        "全年龄",
        "一个比较长的标签",
        "爱情",
        "校园",
        "剧情向",
        "长篇",
        "其他1",
        "其他2",
        "其他3",
        "其他3"
    )
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                title = {
                    Text(
                        "漫画详情",
                        color = MaterialTheme.colorScheme.surface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.LightGray)
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
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                IconButton(
                    onClick = {
                        // TODO
                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary, // 背景色
                        contentColor = MaterialTheme.colorScheme.onPrimary  // 图标颜色
                    )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Message,
                        contentDescription = "评论",
                        tint = Color.White
                    )
                }
                IconButton(
                    onClick = {
                        // TODO
                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary, // 背景色
                        contentColor = MaterialTheme.colorScheme.onPrimary  // 图标颜色
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = "喜欢",
                        tint = Color.White
                    )
                }
                IconButton(
                    onClick = {
                        // TODO
                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary, // 背景色
                        contentColor = MaterialTheme.colorScheme.onPrimary  // 图标颜色
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.Bookmark,
                        contentDescription = "收藏",
                        tint = Color.White,
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = {
                        // TODO
                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary, // 背景色
                        contentColor = MaterialTheme.colorScheme.onPrimary  // 图标颜色
                    )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.MenuBook,
                        contentDescription = "收藏",
                        tint = Color.White
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
        ) {
            Image(
                painter = painterResource(R.drawable.comic_cover_placeholder),
                contentDescription = "和歌酱今天也很腹黑的封面",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .aspectRatio(3f / 4f)
                    .fillMaxWidth()
            )
            Column(
                modifier = Modifier.padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // comic name
                Text(
                    text = "和歌酱今天也很腹黑 [攸望汉化组] [岛村] 和歌ちゃんは今日もあざとい",
                    fontSize = 18.sp,
                    lineHeight = 27.sp,
                    fontWeight = FontWeight.Bold,
//                maxLines = 1,
//                overflow = TextOverflow.Ellipsis,
                )
                // comic author list
                FlowRow(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    authorList.forEach {
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
                // comic code
                Text("JM1044155")
                // comic tag list
                FlowRow(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    tagList.forEach {
                        key(it) {
                            AssistChip(onClick = {
                                // TODO
                            }, label = {
                                Text(it)
                            })
                        }
                    }
                }
            }
        }
    }
}