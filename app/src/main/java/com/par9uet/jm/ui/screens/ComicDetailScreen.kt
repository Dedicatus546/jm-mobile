package com.par9uet.jm.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.par9uet.jm.R
import com.par9uet.jm.viewModel.rememberAppNavigateViewModel

// https://cdn-msp3.jmapinodeudzn.net/media/albums/467243_3x4.jpg
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ComicDetailScreen() {
    val appNavigateViewModel = rememberAppNavigateViewModel()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            stickyHeader {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    TopAppBar(
                        colors = TopAppBarDefaults.topAppBarColors()
                            .copy(containerColor = Color.Transparent),
                        modifier = Modifier.align(Alignment.TopCenter),
                        title = {
                            Text("test")
                        },
                        navigationIcon = {
                            IconButton(onClick = {

                            }) {
                                Icon(
                                    painter = painterResource(R.drawable.chevron_left_icon),
                                    contentDescription = "返回"
                                )
                            }
                        }
                    )
                }
            }
            items(1) {
                AsyncImage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(3f / 4),
                    model = "https://cdn-msp3.jmapinodeudzn.net/media/albums/467243_3x4.jpg",
                    contentDescription = "",
                    contentScale = ContentScale.FillBounds
                )
            }
            // 列表项
            items(20) { index ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(
                        text = "美龍艷笑譚～自我肯定感超低的龍級美少女魔王、勇者用愛將其擊敗的故事～[ ブラック木蓮 郊外の某] 美龍艶笑譚～自己肯定感が激低なドラゴン級美少女魔王を、勇者がイチャラブで退治するお話～",
                        fontWeight = FontWeight(900),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 20.sp
                    )
                }
            }
        }
    }


//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//    ) {
//        AsyncImage(
//            modifier = Modifier
//                .fillMaxWidth()
//                .aspectRatio(3f / 4),
//            model = "https://cdn-msp3.jmapinodeudzn.net/media/albums/467243_3x4.jpg",
//            contentDescription = "",
//            contentScale = ContentScale.FillBounds
//        )
//        // 上面的盒子
//        Box(
//            modifier = Modifier
//                .offset(y = (-50).dp)
//                .padding(16.dp)
//        ) {
//            Card(
//                modifier = Modifier
//                    .padding(16.dp)
//                    .wrapContentHeight()
//                    .fillMaxWidth()
//            ) {
//                Text("card content")
//            }
//        }
//    }
}