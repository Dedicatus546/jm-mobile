package com.par9uet.jm.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalGridApi
import androidx.compose.foundation.layout.Grid
import androidx.compose.foundation.layout.GridTrackSize
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.par9uet.jm.router.DownloadRoute
import com.par9uet.jm.router.LocalSettingRoute
import com.par9uet.jm.router.LoginRoute
import com.par9uet.jm.router.SignInRoute
import com.par9uet.jm.router.UserCollectComicRoute
import com.par9uet.jm.router.UserHistoryComicRoute
import com.par9uet.jm.router.UserHistoryCommentRoute
import com.par9uet.jm.ui.components.SettingGroup
import com.par9uet.jm.ui.components.SettingListItem
import com.par9uet.jm.ui.provider.LocalImageLoader
import com.par9uet.jm.ui.provider.LocalMainNavController
import com.par9uet.jm.ui.provider.LocalRemoteSettingManager
import com.par9uet.jm.ui.provider.LocalUserManager
import com.par9uet.jm.ui.viewModel.UserViewModel
import com.par9uet.jm.utils.createAvatarImageRequest
import com.par9uet.jm.utils.hiltActivityViewModel
import kotlinx.coroutines.launch

@Composable
private fun MenuItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit = {}
) {
    ListItem(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = onClick
            )
            .padding(horizontal = 8.dp),
        leadingContent = {
            Icon(
                imageVector = icon,
                contentDescription = "${label}的图标"
            )
        },
        trailingContent = {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                "${label}的图标"
            )
        }
    ) {
        Text(text = label)
    }
}

@Composable
private fun DataItem(
    icon: ImageVector,
    value: String
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
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

@OptIn(ExperimentalGridApi::class)
@Composable
fun UserScreen() {
    val remoteSettingManager = LocalRemoteSettingManager.current
    val userManager = LocalUserManager.current
    val userViewModel: UserViewModel = hiltActivityViewModel()
    val coroutineScope = rememberCoroutineScope()
    val userState by userManager.userState.collectAsState()
    val isLogin by userManager.isLoginState.collectAsState(false)
    val remoteSetting by remoteSettingManager.remoteSettingState.collectAsState()
    val mainNavController = LocalMainNavController.current
    val imageLoader = LocalImageLoader.current
    fun checkLoginThenDo(onDo: () -> Unit) {
        if (!isLogin) {
            mainNavController.navigate(LoginRoute)
            return
        }
        onDo()
    }
    PullToRefreshBox(
        isRefreshing = userState.isLoading,
        state = rememberPullToRefreshState(),
        onRefresh = {
            if (isLogin) {
                coroutineScope.launch {
                    userManager.autoLogin(userState.data!!.username, userState.data!!.password)
                }
            }
        },
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
        ) {
            if (isLogin) {
                val user = userState.data!!
                Row(
                    modifier = Modifier
                        .padding(horizontal = 30.dp, vertical = 20.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AsyncImage(
                            model = createAvatarImageRequest(
                                context = LocalContext.current,
                                url = "${remoteSetting.imgHost}/media/users/${user.avatar}",
                                avatar = user.avatar
                            ),
                            imageLoader = imageLoader,
                            contentDescription = "${user.username}的头像",
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                        )
                        Text(user.username)
                    }
                    Grid(
                        config = {
                            repeat(2) {
                                column(GridTrackSize.Auto)
                            }
                            repeat(2) {
                                row(GridTrackSize.Auto)
                            }
                            columnGap(32.dp)
                            rowGap(16.dp)
                        }
                    ) {
                        DataItem(
                            Icons.AutoMirrored.Filled.TrendingUp,
                            "${user.currentLevelExp}/${user.nextLevelExp}"
                        )
                        DataItem(
                            Icons.Default.Leaderboard,
                            "${user.level}（${user.levelName}）"
                        )
                        DataItem(Icons.Default.Savings, "${user.jCoin}")
                        DataItem(
                            Icons.Default.Bookmark,
                            "${user.currentCollectCount}/${user.maxCollectCount}"
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .padding(vertical = 20.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceContainer)
                    )
                    TextButton(onClick = {
                        mainNavController.navigate(LoginRoute)
                    }) {
                        Text("点击登录", fontSize = 16.sp)
                    }
                }
            }
            SettingGroup(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
            ) {
                SettingListItem(
                    icon = Icons.Default.Bookmarks,
                    iconContentDescription = "我的收藏",
                    title = "我的收藏",
                    onClick = {
                        checkLoginThenDo { mainNavController.navigate(UserCollectComicRoute) }
                    }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.background)
                SettingListItem(
                    icon = Icons.Default.History,
                    iconContentDescription = "历史观看",
                    title = "历史观看",
                    onClick = {
                        checkLoginThenDo { mainNavController.navigate(UserHistoryComicRoute) }
                    }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.background)
                SettingListItem(
                    icon = Icons.AutoMirrored.Filled.Comment,
                    iconContentDescription = "我的评论",
                    title = "我的评论",
                    onClick = {
                        checkLoginThenDo { mainNavController.navigate(UserHistoryCommentRoute) }
                    }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.background)
                SettingListItem(
                    icon = Icons.Default.CalendarMonth,
                    iconContentDescription = "签到",
                    title = "签到",
                    onClick = {
                        checkLoginThenDo { mainNavController.navigate(SignInRoute) }
                    }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.background)
                SettingListItem(
                    icon = Icons.Default.Download,
                    iconContentDescription = "下载",
                    title = "下载",
                    onClick = {
                        mainNavController.navigate(DownloadRoute)
                    }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.background)
                SettingListItem(
                    icon = Icons.Default.Settings,
                    iconContentDescription = "设置",
                    title = "设置",
                    onClick = {
                        mainNavController.navigate(LocalSettingRoute)
                    }
                )
                if (isLogin) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.background)
                    SettingListItem(
                        icon = Icons.AutoMirrored.Filled.Logout,
                        iconContentDescription = "退出登录",
                        title = "退出登录",
                        onClick = {
                            userViewModel.logout()
                        }
                    )
                }
            }
        }
    }
}