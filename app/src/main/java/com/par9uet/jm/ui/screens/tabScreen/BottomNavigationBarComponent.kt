package com.par9uet.jm.ui.screens.tabScreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.navigation.compose.currentBackStackEntryAsState
import com.par9uet.jm.R


@Composable
fun BottomNavigationBarComponent() {
    val tabNavController = LocalTabNavController.current
    val backStackEntryState by tabNavController.currentBackStackEntryAsState()
    val currentRoute = backStackEntryState?.destination?.route

    fun navigate(name: String) {
        if (name === currentRoute) {
            return
        }
        tabNavController.navigate(name)
    }

    AnimatedVisibility(visible = currentRoute != "login") {
        NavigationBar {
            NavigationBarItem(
                icon = {
                    Icon(
                        painterResource(R.drawable.home_icon),
                        contentDescription = "首页"
                    )
                },
                selected = currentRoute == "home",
                onClick = {
                    navigate("home")
                }
            )
            NavigationBarItem(
                icon = {
                    Icon(
                        painterResource(R.drawable.person_icon),
                        contentDescription = "个人中心"
                    )
                },
                selected = currentRoute == "user",
                onClick = {
                    navigate("user")
                }
            )
        }
    }
}