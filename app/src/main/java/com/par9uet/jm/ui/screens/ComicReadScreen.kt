package com.par9uet.jm.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.par9uet.jm.ui.components.ComicPicImage
import com.par9uet.jm.viewModel.ComicReadViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ComicReadScreen(
    comicId: Int,
    comicReadViewModel: ComicReadViewModel = koinViewModel()
) {
    val list = comicReadViewModel.list
    val loading = comicReadViewModel.loading

    LaunchedEffect(Unit) {
        comicReadViewModel.getComicPicList(comicId)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        if (loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
//                .verticalScroll(rememberScrollState())
            ) {
                itemsIndexed(items = list, key = { _, item -> item }) { index, item ->
                    ComicPicImage(
                        comicId = comicId,
                        src = item,
                        contentDescription = "JM${comicId}的第${index + 1}张图片"
                    )
                }
            }
//            Box(
//                modifier = Modifier
//                    .align(Alignment.BottomEnd)
//                    .padding(16.dp)
//                    .background(Color.Gray)
//            ) {
//
//            }
        }
    }
}