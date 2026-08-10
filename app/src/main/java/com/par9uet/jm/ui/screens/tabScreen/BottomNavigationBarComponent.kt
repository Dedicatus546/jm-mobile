package com.par9uet.jm.ui.screens.tabScreen

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavigationBarComponent() {
    val tabNavController = LocalTabNavController.current
    val backStackEntryState by tabNavController.currentBackStackEntryAsState()
    val currentRoute = backStackEntryState?.destination?.route

    fun navigate(name: String) {
        if (name == currentRoute) {
            return
        }
        tabNavController.navigate(name)
    }

    NavigationBar {
        bottomNavList.forEach {
            key(it.route) {
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = it.icon,
                            contentDescription = it.label
                        )
                    },
                    selected = currentRoute == it.route,
                    onClick = {
                        navigate(it.route)
                    }
                )
            }
        }
    }
}