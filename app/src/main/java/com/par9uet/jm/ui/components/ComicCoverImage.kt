package com.par9uet.jm.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.par9uet.jm.coil.createAsyncImageLoader
import com.par9uet.jm.data.models.Comic
import com.par9uet.jm.viewModel.GlobalViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ComicCoverImage(
    comic: Comic,
    showIdChip: Boolean = false,
    globalViewModel: GlobalViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val settingState = globalViewModel.settingState
//    val model = "https://placehold.co/300x400.png"
//    val model = "${settingState.remoteSetting.imgHost}/media/albums/1230228_3x4.jpg"
    val model = "${settingState.remoteSetting.imgHost}/media/albums/${comic.id}_3x4.jpg"
    Box(modifier = Modifier.fillMaxWidth()) {
        AsyncImage(
            model = model,
            imageLoader = createAsyncImageLoader(context),
            contentDescription = "${comic.name}的封面",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .aspectRatio(3f / 4f)
                .fillMaxWidth()
        )
        if (showIdChip) {
            Box(modifier = Modifier.align(Alignment.BottomEnd)) {
                CompositionLocalProvider(
                    // 去除 m3 默认的最小高度
                    LocalMinimumInteractiveComponentSize provides Dp.Unspecified
                ) {
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
}