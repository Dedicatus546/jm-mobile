package com.par9uet.jm.ui.screens

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.par9uet.jm.ui.components.BottomNavigationBarComponent
import com.par9uet.jm.ui.components.TopBarComponent

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
                startDestination = tabName
            ) {
                composable(
                    "home",
                    enterTransition = { slideInHorizontally(initialOffsetX = { width -> -width }) },
                    exitTransition = { slideOutHorizontally(targetOffsetX = { width -> -width }) }) {
                    HomeScreen()
                }
                composable(
                    "person",
                    enterTransition = { slideInHorizontally(initialOffsetX = { width -> width }) },
                    exitTransition = { slideOutHorizontally(targetOffsetX = { width -> width }) }) {
                    PersonScreen()
                }
            }
        }
    }
}

val LocalTabNavController = staticCompositionLocalOf<NavHostController> {
    error("none")
}