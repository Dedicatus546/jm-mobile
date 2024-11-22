package com.par9uet.jm.ui.screens

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.par9uet.jm.viewModel.rememberAppNavigateViewModel

@Composable
fun AppScreen() {
    val navController = rememberNavController()
    val appNavigateViewModel = rememberAppNavigateViewModel()
    LaunchedEffect(navController) {
        appNavigateViewModel.setNavController(navController)
    }
    NavHost(
        navController = navController,
        startDestination = "tab"
    ) {
        composable(
            "tab",
            enterTransition = { slideInHorizontally(initialOffsetX = { width -> -width }) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { width -> -width }) }
        ) {
            TabScreen()
        }
        composable(
            "comicDetail",
            enterTransition = { slideInHorizontally(initialOffsetX = { width -> width }) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { width -> width }) }
        ) {
            ComicDetailScreen()
        }
        composable(
            "login",
            enterTransition = { slideInHorizontally(initialOffsetX = { width -> width }) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { width -> width }) }
        ) {
            LoginScreen()
        }
    }
}