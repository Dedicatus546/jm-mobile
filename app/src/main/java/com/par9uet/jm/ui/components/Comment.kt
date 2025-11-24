package com.par9uet.jm.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.icons.filled.Reply
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    Row(
        modifier = Modifier.padding(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        AsyncImage(
            model = model,
            contentDescription = "${comment.nickname}的头像",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(comment.nickname)
            Text(comment.time, fontSize = 12.sp, modifier = Modifier.padding(bottom = 5.dp))
            HtmlText(comment.content)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = {

                }) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(
                            5.dp
                        ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Reply,
                            contentDescription = "回复",
                            modifier = Modifier.size(14.dp)
                        )
                        Text(text = "回复", fontSize = 12.sp)
                    }
                }
                TextButton(
                    onClick = {

                    }
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(
                            5.dp
                        ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ThumbUp,
                            contentDescription = "点赞",
                            modifier = Modifier.size(14.dp)
                        )
                        Text(text = "点赞", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}