package com.par9uet.jm.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.compose.currentBackStackEntryAsState
import com.par9uet.jm.R
import com.par9uet.jm.ui.screens.LocalTabNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopBarComponent() {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
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

@Composable
fun TopBarComponent() {
    val tabNavController = LocalTabNavController.current
    val backStackEntryState by tabNavController.currentBackStackEntryAsState()
    val currentRoute = backStackEntryState?.destination?.route
    when (currentRoute) {
        "home" -> HomeTopBarComponent()
        "person" -> PersonTopBarComponent()
        else -> {
            Text("none")
        }
    }
}