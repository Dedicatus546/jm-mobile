package com.par9uet.jm.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.par9uet.jm.R
import com.par9uet.jm.store.RemoteSettingManager
import com.par9uet.jm.store.UserManager
import com.par9uet.jm.ui.viewModel.UserViewModel
import org.koin.compose.getKoin
import org.koin.compose.viewmodel.koinActivityViewModel

@Composable
private fun MenuItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit = {}
) {
    ListItem(
        modifier = Modifier.clickable(
            onClick = onClick
        ),
        leadingContent = {
            Icon(
                imageVector = icon,
                contentDescription = "${label}的图标"
            )
        },
        headlineContent = {
            Text(label)
        },
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
    icon: ImageVector,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(imageVector = icon, contentDescription = "")
        Text(
            value,
            modifier = Modifier,
            fontSize = 12.sp,
        )
    }
}

@Composable
fun UserScreen(
    userManager: UserManager = getKoin().get(),
    remoteSettingManager: RemoteSettingManager = getKoin().get(),
    userViewModel: UserViewModel = koinActivityViewModel()
) {
    val user by userManager.userState.collectAsState()
    val isLogin by userManager.isLoginState.collectAsState(false)
    val remoteSetting by remoteSettingManager.remoteSettingState.collectAsState()
    val mainNavController = LocalMainNavController.current
    if (!isLogin) {
        Button(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            onClick = {
                mainNavController.navigate("login")
            }
        ) {
            Text("请先登录")
        }
    } else {
        PullToRefreshBox(
            isRefreshing = false,
            state = rememberPullToRefreshState(),
            onRefresh = {
                // TODO
            },
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AsyncImage(
                            model = "${remoteSetting!!.imgHost}/media/users/${user!!.avatar}",
                            contentDescription = "${user!!.username}的头像",
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                        )
                        Text(user!!.username)
                    }
                    LazyVerticalGrid(
                        modifier = Modifier.weight(1f),
                        columns = GridCells.Fixed(2),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        content = {
                            item(content = {
                                DataItem(
                                    Icons.AutoMirrored.Filled.TrendingUp,
                                    "${user!!.currentLevelExp}/${user!!.nextLevelExp}"
                                )
                            })
                            item(content = {
                                DataItem(
                                    Icons.Default.Leaderboard,
                                    "${user!!.level}（${user!!.levelName}）"
                                )
                            })
                            item(content = {
                                DataItem(Icons.Default.Savings, "${user!!.jCoin}")
                            })
                            item(content = {
                                DataItem(
                                    Icons.Default.Bookmark,
                                    "${user!!.currentCollectCount}/${user!!.maxCollectCount}"
                                )
                            })
                        }
                    )
                }
                HorizontalDivider()
                Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .verticalScroll(rememberScrollState())
                        .fillMaxWidth()
                ) {
                    MenuItem(
                        icon = Icons.Default.Bookmarks,
                        label = "我的收藏",
                        onClick = {
                            mainNavController.navigate("userCollectComic")
                        }
                    )
                    MenuItem(
                        icon = Icons.Default.History,
                        label = "历史观看",
                        onClick = {
                            mainNavController.navigate("userHistoryComic")
                        }
                    )
                    MenuItem(
                        icon = Icons.AutoMirrored.Filled.Comment,
                        label = "我的评论",
                        onClick = {
                            mainNavController.navigate("userHistoryComment")
                        }
                    )
                    MenuItem(
                        icon = Icons.Default.CalendarMonth,
                        label = "签到",
                        onClick = {
                            mainNavController.navigate("sign")
                        }
                    )
                    MenuItem(
                        icon = Icons.Default.Settings,
                        label = "设置",
                        onClick = {
                            mainNavController.navigate("appLocalSetting")
                        }
                    )
                    MenuItem(
                        icon = Icons.AutoMirrored.Filled.Logout,
                        label = "退出登录",
                        onClick = {
                            userViewModel.logout()
                        }
                    )
                }
            }
        }

    }
}