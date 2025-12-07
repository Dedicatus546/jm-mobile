package com.par9uet.jm.ui.components

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import com.par9uet.jm.data.models.Comic
import com.par9uet.jm.retrofit.repository.RemoteSettingRepository
import org.koin.compose.getKoin
import org.koin.core.qualifier.named

@Composable
fun ComicCoverImage(
    comic: Comic,
    showIdChip: Boolean = false,
    asyncImageLoader: ImageLoader = getKoin().get(qualifier = named("AsyncImageLoader")),
    settingRepository: RemoteSettingRepository = getKoin().get()
) {
    val remoteSetting = settingRepository.remoteSetting
//    val model = "https://placehold.co/300x400.png"
//    val model = "${settingState.remoteSetting.imgHost}/media/albums/1230228_3x4.jpg"
    val model = "${remoteSetting.imgHost}/media/albums/${comic.id}_3x4.jpg"
    Box(modifier = Modifier.fillMaxWidth()) {
        AsyncImage(
            model = model,
            imageLoader = asyncImageLoader,
            contentDescription = "${comic.name}的封面",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .aspectRatio(3f / 4f)
                .fillMaxWidth(),
            onError = {
                Log.d("cover err", it.result.throwable.stackTraceToString())
            }
        )
        if (showIdChip) {
            Box(modifier = Modifier.align(Alignment.BottomEnd)) {
                AssistChip(
                    border = null,
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    ),
                    modifier = Modifier.padding(end = 10.dp, bottom = 10.dp),
                    onClick = {

                    },
                    label = {
                        Text("JM${comic.id}")
                    }
                )
            }
        }
    }
}