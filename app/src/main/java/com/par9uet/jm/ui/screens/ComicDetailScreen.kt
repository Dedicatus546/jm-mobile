package com.par9uet.jm.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

// https://cdn-msp3.jmapinodeudzn.net/media/albums/467243_3x4.jpg
@Composable
fun ComicDetailScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(3f / 4),
            model = "https://cdn-msp3.jmapinodeudzn.net/media/albums/467243_3x4.jpg",
            contentDescription = "",
            contentScale = ContentScale.FillBounds
        )
        // 上面的盒子
        Box(
            modifier = Modifier
                .offset(y = (-50).dp)
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier
                    .padding(16.dp)
                    .wrapContentHeight()
                    .fillMaxWidth()
            ) {
                Text("card content")
            }
        }
    }
}