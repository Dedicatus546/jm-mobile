package com.par9uet.jm.ui.screens

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.par9uet.jm.ui.components.BottomNavigationBarComponent
import com.par9uet.jm.ui.components.TopBarComponent
import com.par9uet.jm.viewModel.rememberTabNavigateViewModel

@Composable
fun TabScreen() {
    val navController = rememberNavController()
    val tabNavigateViewModel = rememberTabNavigateViewModel()
    LaunchedEffect(navController) {
        tabNavigateViewModel.setNavController(navController)
    }
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
            navController = navController,
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