package com.par9uet.jm.ui.components

import androidx.activity.ComponentActivity
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.viewmodel.compose.viewModel
import com.par9uet.jm.R
import com.par9uet.jm.data.models.BottomNavigationRoute
import com.par9uet.jm.viewModel.MainNavigateViewModel
import com.par9uet.jm.viewModel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopBarComponent() {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val mainNavigateViewModel: MainNavigateViewModel =
        viewModel(LocalContext.current as ComponentActivity)
    val userViewModel: UserViewModel = viewModel(LocalContext.current as ComponentActivity)
    TopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        title = {
            Text(
                "禁漫天堂",
                color = MaterialTheme.colorScheme.surface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        actions = {
            IconButton(onClick = { /* do something */ }) {
                Icon(
                    painterResource(R.drawable.search_icon),
                    "搜索",
                    tint = MaterialTheme.colorScheme.surface
                )
            }
            if (!userViewModel.isLogin) {
                IconButton(onClick = {
                    mainNavigateViewModel.navigate(BottomNavigationRoute.LOGIN.value)
                }) {
                    Icon(
                        painterResource(R.drawable.login_icon),
                        "登录",
                        tint = MaterialTheme.colorScheme.surface
                    )
                }
            }
        },
        scrollBehavior = scrollBehavior
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PersonTopBarComponent() {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    TopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        title = {
            Text(
                "个人中心",
                color = MaterialTheme.colorScheme.surface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        scrollBehavior = scrollBehavior
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoginTopBarComponent() {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val mainNavigateViewModel: MainNavigateViewModel =
        viewModel(LocalContext.current as ComponentActivity)
    TopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        title = {
            Text(
                "登录",
                color = MaterialTheme.colorScheme.surface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            IconButton(
                onClick = {
                    mainNavigateViewModel.back()
                }
            ) {
                Icon(
                    painterResource(R.drawable.chevron_left_icon),
                    "返回",
                    tint = MaterialTheme.colorScheme.surface
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarComponent() {
    val mainNavigateViewModel: MainNavigateViewModel =
        viewModel(LocalContext.current as ComponentActivity)
    val currentRoute by mainNavigateViewModel.currentRoute.collectAsState()
    when (currentRoute) {
        BottomNavigationRoute.HOME.value -> HomeTopBarComponent()
        BottomNavigationRoute.PERSON.value -> PersonTopBarComponent()
        BottomNavigationRoute.LOGIN.value -> LoginTopBarComponent()
        else -> {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                title = {
                    Text("")
                }
            )
        }
    }
}