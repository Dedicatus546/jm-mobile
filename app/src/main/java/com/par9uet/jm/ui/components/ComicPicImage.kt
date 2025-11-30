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
import com.par9uet.jm.viewModel.ComicPicImageViewModel
import com.par9uet.jm.viewModel.ImageResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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

    LaunchedEffect(context, comicId, src) {
        Log.d("pic image", state.hashCode().toString())
        when (imageResult) {
            is ImageResult.Success -> {
                Log.d(
                    "pic image",
                    "已解密，跳过 src: $src comicId: $comicId"
                )
            }

            is ImageResult.Pending -> {
                withContext(Dispatchers.IO) {
                    Log.d("pic image", "开始解密图片 src: $src comicId: $comicId")
                    state.decode(context)
                }
            }

            else -> {

            }
        }
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
