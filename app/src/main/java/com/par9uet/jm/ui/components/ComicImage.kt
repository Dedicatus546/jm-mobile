package com.par9uet.jm.ui.components

import android.util.Log
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.par9uet.jm.data.models.Comic
import com.par9uet.jm.viewModel.SettingViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ComicImage(
    comic: Comic,
    settingViewModel: SettingViewModel = koinViewModel()
) {
//    val model = "${settingViewModel.setting.imgHost}/media/albums/${comic.id}_3x4.jpg"
    val model = "${settingViewModel.setting.imgHost}/media/albums/1230228_3x4.jpg"
    LaunchedEffect(Unit) {
        Log.d("ui setting", model)
        Log.d("ui setting", settingViewModel.setting.toString())
    }
    AsyncImage(
        model = model,
        contentDescription = "${comic.name}的封面",
        contentScale = ContentScale.FillBounds,
        modifier = Modifier
            .aspectRatio(3f / 4f)
            .fillMaxWidth()
    )
}