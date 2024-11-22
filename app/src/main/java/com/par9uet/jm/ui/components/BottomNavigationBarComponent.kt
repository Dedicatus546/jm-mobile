package com.par9uet.jm.ui.components

import com.par9uet.jm.R
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import com.par9uet.jm.viewModel.rememberTabNavigateViewModel


@Composable
fun BottomNavigationBarComponent() {
    val tabNavigateViewModel = rememberTabNavigateViewModel()
    val currentRoute by tabNavigateViewModel.currentRoute.collectAsState()

    fun navigate(name: String) {
        if (name === currentRoute) {
            return
        }
        tabNavigateViewModel.navigate(name)
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
                selected = currentRoute == "person",
                onClick = {
                    navigate("person")
                }
            )
        }
    }
}