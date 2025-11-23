package com.par9uet.jm.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.par9uet.jm.data.models.Comment
import com.par9uet.jm.viewModel.GlobalViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun Comment(
    comment: Comment,
    globalViewModel: GlobalViewModel = koinViewModel()
) {
    val settingState = globalViewModel.settingState
    val model = "${settingState.setting.imgHost}/media/users/${comment.avatar}"
    ListItem(
        leadingContent = {
            AsyncImage(
                model = model,
                contentDescription = "${comment.nickname}的头像",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
            )
        },
        headlineContent = {
            Text(comment.nickname)
        },
        supportingContent = {
            HtmlText(comment.content)
        },
    )
}