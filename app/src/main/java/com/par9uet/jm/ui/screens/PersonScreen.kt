package com.par9uet.jm.ui.screens

import androidx.activity.ComponentActivity
import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.par9uet.jm.R
import com.par9uet.jm.viewModel.SettingViewModel
import com.par9uet.jm.viewModel.UserViewModel

@Composable
private fun MenuItem(
    @DrawableRes
    icon: Int,
    label: String,
) {
    ListItem(
        modifier = Modifier.clickable(
            onClick = {
            }
        ),
        leadingContent = {
            Icon(
                painterResource(icon),
                contentDescription = "${label}的图标"
            )
        },
        headlineContent = {
            Text(label)
        },
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
        ),
        trailingContent = {
            Icon(
                painterResource(R.drawable.chevron_right_icon),
                "${label}的图标"
            )
        }
    )
}

@Composable
private fun DataItem(
    label: String,
    value: String
) {
    Card {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
            modifier = Modifier.aspectRatio(16f / 7)
        ) {
            Text(
                label,
                modifier = Modifier,
                fontSize = 13.sp,
            )
            Text(
                value,
                modifier = Modifier,
            )
        }
    }
}

@Composable
fun PersonScreen() {
    val settingViewModel: SettingViewModel = viewModel(LocalContext.current as ComponentActivity)
    val userViewModel: UserViewModel = viewModel(LocalContext.current as ComponentActivity)
    val userInfo = userViewModel.userInfo
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxHeight(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    model = "${settingViewModel.settingInfo.imgHost}/media/users/${userInfo.avatar}",
                    contentDescription = "${userInfo.username}的头像",
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                )
                Text(userInfo.username)
            }
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            content = {
                item(content = {
                    DataItem("经验值", "${userInfo.currentLevelExp}/${userInfo.nextLevelExp}")
                })
                item(content = {
                    DataItem("等级", "${userInfo.level}（${userInfo.levelName}）")
                })
                item(content = {
                    DataItem("J Coins", "${userInfo.jCoin}")
                })
                item(content = {
                    DataItem(
                        "可收藏数量",
                        "${userInfo.currentCollectCount}/${userInfo.maxCollectCount}"
                    )
                })
            }
        )
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .weight(1f)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth()
            ) {
                MenuItem(
                    icon = R.drawable.bookmarks_icon,
                    label = "我的收藏"
                )
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.surfaceContainerLowest
                )
                MenuItem(
                    icon = R.drawable.favorite_icon,
                    label = "我的喜爱"
                )
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.surfaceContainerLowest
                )
                MenuItem(
                    icon = R.drawable.comment_icon,
                    label = "我的评论"
                )
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.surfaceContainerLowest
                )
                MenuItem(
                    icon = R.drawable.logout_icon,
                    label = "退出登录"
                )
            }
        }
    }
}