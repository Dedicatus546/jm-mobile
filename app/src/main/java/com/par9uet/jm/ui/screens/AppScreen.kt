package com.par9uet.jm.ui.screens

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.par9uet.jm.data.models.Comic
import com.par9uet.jm.data.models.ComicChapter
import com.par9uet.jm.ui.screens.localSettingScreen.LocalSettingScreen
import com.par9uet.jm.ui.screens.readScreen.ComicReadScreen
import com.par9uet.jm.ui.screens.tabScreen.TabScreen
import org.koin.compose.getKoin

@Composable
fun AppScreen() {
    val gson: Gson = getKoin().get()
    val mainNavController = rememberNavController()
    CompositionLocalProvider(
        LocalMainNavController provides mainNavController,
    ) {
        NavHost(
            modifier = Modifier.fillMaxSize(),
            navController = mainNavController,
//            startDestination = "comicQuickSearch/百合",
//            startDestination = "appLocalSetting",
            startDestination = "tab/home",
//            startDestination = "comicRead/1044155",
//            startDestination = "comicDetail/1044155",
//            startDestination = "comicSearch",
//            startDestination = "sign",
//            startDestination = "download",
//            startDestination = "category",
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> fullWidth },
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> -fullWidth },
                    animationSpec = tween(300)
                )
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> -fullWidth },
                    animationSpec = tween(300)
                )
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> fullWidth },
                    animationSpec = tween(300)
                )
            }
        ) {
            composable(
                route = "tab/{tabName}?",
                arguments = listOf(
                    navArgument(name = "tabName") {
                        type = NavType.StringType; defaultValue = null; nullable = true
                    }
                ),
            ) { backStackEntry ->
                val tabName = backStackEntry.arguments?.getString("tabName") ?: "home"
                TabScreen(tabName = tabName)
            }
            composable("login") { LoginScreen() }
            composable(route = "userCollectComic") { UserCollectComicScreen() }
            composable(route = "userHistoryComic") { UserHistoryComicScreen() }
            composable(route = "userHistoryComment") { UserHistoryCommentScreen() }
            composable(route = "appLocalSetting") { LocalSettingScreen() }
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
                route = "comicChapter/{comicChapterList}",
                arguments = listOf(
                    navArgument(name = "comicChapterList") { type = NavType.StringType }
                ),
            ) { backStackEntry ->
                val comicChapterListJson =
                    backStackEntry.arguments?.getString("comicChapterList") ?: "[]"
                val comicChapterList = gson.fromJson<List<ComicChapter>>(
                    comicChapterListJson,
                    object : TypeToken<List<ComicChapter>>() {}.type
                )
                ComicChapterScreen(
                    comicChapterList = comicChapterList
                )
            }
            composable(
                route = "comicRelate/{relateComicList}",
                arguments = listOf(
                    navArgument(name = "relateComicList") { type = NavType.StringType; }
                ),
            ) { backStackEntry ->
                val comicJson = backStackEntry.arguments?.getString("relateComicList") ?: "[]"
                val relateComicList =
                    gson.fromJson<List<Comic>>(comicJson, object : TypeToken<List<Comic>>() {}.type)
                ComicRelateListScreen(relateComicList = relateComicList)
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
            composable(route = "comicSearch") { ComicSearchScreen() }
            composable(
                route = "comicSearchResult/{searchContent}",
                arguments = listOf(
                    navArgument(name = "searchContent") { type = NavType.StringType }
                ),
            ) { backStackEntry ->
                val searchContent = backStackEntry.arguments!!.getString("searchContent")!!
                ComicSearchResultScreen(
                    searchContent = searchContent
                )
            }
            composable(route = "comicSearch") { ComicSearchScreen() }
            composable(route = "comicRecommend") { ComicWeekRecommendScreen() }
            composable(
                route = "comment/{comicId}",
                arguments = listOf(
                    navArgument(name = "comicId") { type = NavType.IntType }
                ),
            ) { backStackEntry ->
                val comicId = backStackEntry.arguments?.getInt("comicId") ?: -1
                ComicCommentScreen(comicId = comicId)
            }
            composable(route = "sign") { SignInScreen() }
            composable(route = "category") { ComicCategoryScreen() }
//            composable(route = "download") { DownloadScreen() }
        }
    }
}

val LocalMainNavController = staticCompositionLocalOf<NavHostController> {
    error("none")
}
