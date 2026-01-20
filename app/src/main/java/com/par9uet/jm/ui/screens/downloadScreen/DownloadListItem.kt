package com.par9uet.jm.ui.screens.downloadScreen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.AsyncImage
import com.par9uet.jm.data.models.DownloadComic
import org.koin.compose.getKoin
import java.io.File

@Composable
private fun ComicCoverImage(
    comic: DownloadComic,
    imageLoader: ImageLoader = getKoin().get()
) {
    val file = File(comic.coverPath)
    Box(modifier = Modifier.fillMaxWidth()) {
        AsyncImage(
            model = file,
            imageLoader = imageLoader,
            contentDescription = "${comic.name}的封面",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .aspectRatio(3f / 4f)
                .fillMaxWidth(),
        )
    }
}

@Composable
fun DownloadListItem(
    modifier: Modifier = Modifier,
    comic: DownloadComic
) {

    Card(
        onClick = {
            // TODO
        }
    ) {
        Box(modifier = modifier) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                ComicCoverImage(comic)
                Text(
                    modifier = Modifier
                        .padding(horizontal = 8.dp),
                    text = comic.name,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    fontSize = 13.sp,
                    lineHeight = 16.sp,
                )
                Text(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .padding(bottom = 8.dp),
                    text = comic.authorList.joinToString(",").ifBlank { "暂无作者" },
                    fontSize = 12.sp,
                    lineHeight = 12.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.Black.copy(alpha = 0.3f))
            )
            val animatedProgress by animateFloatAsState(
                targetValue = comic.progress,
                animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
                label = "progressAnimation"
            )
            CircularProgressIndicator(
                progress = {
                    animatedProgress
                },
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}