package com.par9uet.jm.ui.screens

import androidx.activity.ComponentActivity
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.par9uet.jm.viewModel.IndexNavigateViewModel

@Composable
fun IndexScreen() {
    val navController = rememberNavController()
    val indexNavigateViewModel: IndexNavigateViewModel =
        viewModel(LocalContext.current as ComponentActivity)
    LaunchedEffect(navController) {
        indexNavigateViewModel.setNavController(navController)
    }
    NavHost(
        navController = navController,
        startDestination = "main"
    ) {
        composable(
            "main",
            enterTransition = { slideInHorizontally(initialOffsetX = { width -> -width }) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { width -> -width }) }
        ) {
            MainScreen()
        }
        composable(
            "comicDetail",
            enterTransition = { slideInHorizontally(initialOffsetX = { width -> width }) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { width -> width }) }
        ) {
            ComicDetailScreen()
        }
    }
}