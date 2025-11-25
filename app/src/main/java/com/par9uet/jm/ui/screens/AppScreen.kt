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

@Composable
fun AppScreen() {
    val mainNavController = rememberNavController()
    CompositionLocalProvider(
        LocalMainNavController provides mainNavController,
    ) {
        NavHost(
            navController = mainNavController,
            startDestination = "tab/home"
            // startDestination = "comicDetail/1230228"
        ) {
            composable(
                route = "tab/{tabName}?",
                arguments = listOf(
                    navArgument(name = "tabName") {
                        type = NavType.StringType; defaultValue = null; nullable = true
                    }
                ),
                enterTransition = { slideInHorizontally(initialOffsetX = { width -> -width }) },
                exitTransition = { slideOutHorizontally(targetOffsetX = { width -> -width }) }
            ) { backStackEntry ->
                val tabName = backStackEntry.arguments?.getString("tabName") ?: "home"
                TabScreen(tabName = tabName)
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
            composable(
                route = "userCollectComic",
                enterTransition = { slideInHorizontally(initialOffsetX = { width -> width }) },
                exitTransition = { slideOutHorizontally(targetOffsetX = { width -> width }) }
            ) {
                UserCollectComicScreen()
            }
            composable(
                route = "userHistoryComic",
                enterTransition = { slideInHorizontally(initialOffsetX = { width -> width }) },
                exitTransition = { slideOutHorizontally(targetOffsetX = { width -> width }) }
            ) {
                UserHistoryComicScreen()
            }
            composable(
                route = "userHistoryComment",
                enterTransition = { slideInHorizontally(initialOffsetX = { width -> width }) },
                exitTransition = { slideOutHorizontally(targetOffsetX = { width -> width }) }
            ) {
                UserHistoryCommentScreen()
            }
            composable(
                route = "appLocalSetting",
                enterTransition = { slideInHorizontally(initialOffsetX = { width -> width }) },
                exitTransition = { slideOutHorizontally(targetOffsetX = { width -> width }) }
            ) {
                LocalSettingScreen()
            }
        }
    }

}

val LocalMainNavController = staticCompositionLocalOf<NavHostController> {
    error("none")
}
