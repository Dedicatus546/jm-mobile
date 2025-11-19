package com.par9uet.jm.ui.screens

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.par9uet.jm.ui.components.BottomNavigationBarComponent
import com.par9uet.jm.ui.components.TopBarComponent

@Composable
fun TabScreen() {
    val tabNavController = LocalTabNavController.current
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
            startDestination = "home"
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
//            composable(
//                "login",
//                enterTransition = { slideInHorizontally(initialOffsetX = { width -> width }) },
//                exitTransition = { slideOutHorizontally(targetOffsetX = { width -> width }) }) {
//                LoginScreen()
//            }
//            composable(
//                "comicDetail",
//                enterTransition = { slideInHorizontally(initialOffsetX = { width -> width }) },
//                exitTransition = { slideOutHorizontally(targetOffsetX = { width -> width }) }) {
//                ComicDetailScreen()
//            }
        }
    }
}