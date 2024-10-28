package com.par9uet.jm.ui.components

import androidx.activity.ComponentActivity
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.par9uet.jm.viewModel.IndexNavigateViewModel
import com.par9uet.jm.viewModel.SettingViewModel

@Composable
fun ComicComponent(
    name: String,
    author: String?,
    id: Int,
    modifier: Modifier = Modifier,
) {
    val settingViewModel: SettingViewModel = viewModel(LocalContext.current as ComponentActivity)
    val indexNavigateViewModel: IndexNavigateViewModel =
        viewModel(LocalContext.current as ComponentActivity)
    Card(
        modifier = modifier,
        onClick = {
//            indexNavigateViewModel.navigate("comicDetail")
        }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            AsyncImage(
                model = "${settingViewModel.settingInfo.imgHost}/media/albums/${id}_3x4.jpg",
                contentDescription = "${name}的封面",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .aspectRatio(3f / 4f)
                    .fillMaxWidth()
            )
            Text(
                modifier = Modifier
                    .padding(horizontal = 8.dp),
                text = name,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                fontSize = 13.sp,
                lineHeight = 16.sp,
            )
            Text(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .padding(bottom = 8.dp),
                text = author ?: "未知",
                fontSize = 12.sp,
                lineHeight = 12.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}