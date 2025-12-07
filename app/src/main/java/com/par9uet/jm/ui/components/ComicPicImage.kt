package com.par9uet.jm.ui.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.par9uet.jm.ui.viewModel.ComicPicImageViewModel
import com.par9uet.jm.ui.viewModel.ImageResult
import org.koin.androidx.compose.koinViewModel

@Composable
fun ComicPicImage(
    comicId: Int,
    src: String,
    contentDescription: String,
    modifier: Modifier = Modifier,
    comicPicImageViewModel: ComicPicImageViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val state = comicPicImageViewModel.getComicPicImageState(comicId, src)
    val imageResult = state.imageResult

    val retryImageDecode = {
        Log.d("pic image", "开始重新解密图片 src: $src comicId: $comicId")
        state.imageResult = ImageResult.Pending()
        comicPicImageViewModel.decode(comicId, src, context)
    }

    LaunchedEffect(Unit) {
        Log.d("pic image", "state 对象地址 ${state.hashCode()}")
        comicPicImageViewModel.decode(comicId, src, context)
    }
    Box(
        modifier
            .fillMaxWidth()
            .aspectRatio(
                when (imageResult) {
                    is ImageResult.Success -> {
                        imageResult.decodeImageAspectRatio
                    }

                    else -> {
                        9f / 16
                    }
                }
            )
    ) {
        when (imageResult) {
            is ImageResult.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            is ImageResult.Failure -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(imageResult.reason)
                    TextButton(
                        onClick = {
                            retryImageDecode()
                        }
                    ) {
                        Text("重试")
                    }
                }
            }

            is ImageResult.Success -> {
                Image(
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillBounds,
                    bitmap = imageResult.decodeImageBitmap,
                    contentDescription = contentDescription,
                )
            }

            else -> {

            }
        }
    }
}
