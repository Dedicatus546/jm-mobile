package com.par9uet.jm.ui.components

import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.par9uet.jm.R
import com.par9uet.jm.data.models.Comic
import com.par9uet.jm.viewModel.AppNavigateViewModel
import com.par9uet.jm.viewModel.SettingViewModel

@Composable
fun ComicComponent(
    comic: Comic,
    modifier: Modifier = Modifier,
) {
    val settingViewModel: SettingViewModel = viewModel(LocalContext.current as ComponentActivity)
    val indexNavigateViewModel: AppNavigateViewModel =
        viewModel(LocalContext.current as ComponentActivity)
    Card(
        modifier = modifier,
        onClick = {
            indexNavigateViewModel.navigate("comicDetail/${comic.id}")
        }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            ComicImage(comic)
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
                text = comic.authorList.joinToString(","),
                fontSize = 12.sp,
                lineHeight = 12.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}