package com.par9uet.jm.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.ImageLoader
import com.par9uet.jm.coil.createAsyncImageLoader
import com.par9uet.jm.coil.createPicImageLoader

@Composable
fun AppScreen() {
    val context = LocalContext.current
    val mainNavController = rememberNavController()
    val asyncImageLoader = createAsyncImageLoader(context)
    val picImageLoader = createPicImageLoader(context)
    CompositionLocalProvider(
        LocalMainNavController provides mainNavController,
        LocalAsyncImageLoader provides asyncImageLoader,
        LocalPicImageLoader provides picImageLoader
    ) {
        NavHost(
            navController = mainNavController,
//            startDestination = "comicQuickSearch/百合"
//            startDestination = "appLocalSetting"
            startDestination = "tab/home",
//            startDestination = "comicRead/467243",
//             startDestination = "comicDetail/1230228"
        ) {
            composable(
                route = "tab/{tabName}?",
                arguments = listOf(
                    navArgument(name = "tabName") {
                        type = NavType.StringType; defaultValue = null; nullable = true
                    }
                ),
//                enterTransition = { slideInHorizontally(initialOffsetX = { width -> -width }) },
//                exitTransition = { slideOutHorizontally(targetOffsetX = { width -> -width }) }
            ) { backStackEntry ->
                val tabName = backStackEntry.arguments?.getString("tabName") ?: "home"
                TabScreen(tabName = tabName)
            }
            composable(
                route = "comicDetail/{id}",
                arguments = listOf(
                    navArgument(name = "id") { type = NavType.IntType; defaultValue = -1 }
                ),
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("id") ?: -1
                ComicDetailScreen(id = id)
            }
            composable(
                "login",
            ) {
                LoginScreen()
            }
            composable(
                route = "userCollectComic",
            ) {
                UserCollectComicScreen()
            }
            composable(
                route = "userHistoryComic",
            ) {
                UserHistoryComicScreen()
            }
            composable(
                route = "userHistoryComment",
            ) {
                UserHistoryCommentScreen()
            }
            composable(
                route = "appLocalSetting",
            ) {
                LocalSettingScreen()
            }
            composable(
                route = "comicRead/{id}",
                arguments = listOf(
                    navArgument(name = "id") { type = NavType.IntType; defaultValue = -1 }
                ),
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("id") ?: -1
                ComicReadScreen(comicId = id)
            }
            composable(
                route = "comicQuickSearch/{searchContent}",
                arguments = listOf(
                    navArgument(name = "searchContent") { type = NavType.StringType }
                ),
            ) { backStackEntry ->
                val searchContent = backStackEntry.arguments!!.getString("searchContent")!!
                ComicQuickSearchScreen(searchContent = searchContent)
            }
            composable(
                route = "test"
            ) {
                TestScreen()
            }
        }
    }
}

val LocalMainNavController = staticCompositionLocalOf<NavHostController> {
    error("none")
}

val LocalAsyncImageLoader = staticCompositionLocalOf<ImageLoader> {
    error("none")
}

val LocalPicImageLoader = staticCompositionLocalOf<ImageLoader> {
    error("none")
}
