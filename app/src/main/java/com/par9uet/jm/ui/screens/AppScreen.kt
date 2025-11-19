package com.par9uet.jm.ui.screens

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

val LocalMainNavController = staticCompositionLocalOf<NavHostController> {
    error("none")
}
val LocalTabNavController = staticCompositionLocalOf<NavHostController> {
    error("none")
}

@Composable
fun AppScreen() {
    val mainNavController = rememberNavController()
    val tabNavController = rememberNavController()
    CompositionLocalProvider(
        LocalMainNavController provides mainNavController,
        LocalTabNavController provides tabNavController
    ) {
        NavHost(
            navController = mainNavController,
            startDestination = "tab"
            // startDestination = "comicDetail/1230228"
        ) {
            composable(
                "tab",
                enterTransition = { slideInHorizontally(initialOffsetX = { width -> -width }) },
                exitTransition = { slideOutHorizontally(targetOffsetX = { width -> -width }) }
            ) {
                TabScreen()
            }
            composable(
                route = "comicDetail/{id}",
                arguments = listOf(
                    navArgument(name = "id") { type = NavType.IntType; defaultValue = -1 }
                ),
                enterTransition = { slideInHorizontally(initialOffsetX = { width -> width }) },
                exitTransition = { slideOutHorizontally(targetOffsetX = { width -> width }) }
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("id") ?: -1
                ComicDetailScreen(id = id)
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
}