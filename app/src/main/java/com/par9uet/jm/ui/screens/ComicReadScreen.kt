package com.par9uet.jm.ui.screens

import android.util.Log
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
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
            ) {
                items(list, key = {
                    "${comicId}_${it}"
                }) {
                    ComicPicImage(
                        comicId = comicId,
                        src = it,
                        contentDescription = "JM${comicId}的图片"
                    )
                }
            }
        }
    }
}