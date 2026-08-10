package com.par9uet.jm.ui.screens.tabScreen

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun TabScreen(tabName: String) {
    val tabNavController = rememberNavController()
    CompositionLocalProvider(
        LocalTabNavController provides tabNavController,
    ) {
        Scaffold(
            bottomBar = {
                BottomNavigationBarComponent()
            },
            topBar = {
                TopBarComponent()
            }
        ) { innerPadding ->
            NavHost(
                modifier = Modifier.padding(innerPadding),
                navController = tabNavController,
                startDestination = tabName,
            ) {
                bottomNavList.forEach { nav ->
                    composable(nav.route) {
                        nav.Content()
                    }
                }
            }
        }
    }
}

val LocalTabNavController = staticCompositionLocalOf<NavHostController> {
    error("none")
}