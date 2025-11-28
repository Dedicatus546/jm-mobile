package com.par9uet.jm.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.par9uet.jm.viewModel.ComicPicImageViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ComicPicImage(
    comicId: Int,
    src: String,
    contentDescription: String,
    modifier: Modifier = Modifier,
    comicPicImageViewModel: ComicPicImageViewModel = koinViewModel()
) {
    val loading = comicPicImageViewModel.loading
    val decodeSrc = comicPicImageViewModel.decodeSrc

    LaunchedEffect(Unit) {
        comicPicImageViewModel.decode(src, comicId)
    }

    if (loading) {
        Box(
            modifier
                .aspectRatio(9f / 16f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        AsyncImage(
            model = decodeSrc,
            contentDescription = contentDescription,
            contentScale = ContentScale.FillBounds,
        )
    }
}