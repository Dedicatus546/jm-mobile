package com.par9uet.jm.ui.screens

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.par9uet.jm.router.AboutRoute
import com.par9uet.jm.router.ApiSelectRoute
import com.par9uet.jm.router.ComicCategoryRoute
import com.par9uet.jm.router.ComicChapterDownloadRoute
import com.par9uet.jm.router.ComicChapterRoute
import com.par9uet.jm.router.ComicCommentRoute
import com.par9uet.jm.router.ComicDetailRoute
import com.par9uet.jm.router.ComicReadRoute
import com.par9uet.jm.router.ComicRecommendRoute
import com.par9uet.jm.router.ComicRelateRoute
import com.par9uet.jm.router.ComicSearchResultRoute
import com.par9uet.jm.router.ComicSearchRoute
import com.par9uet.jm.router.DownloadRoute
import com.par9uet.jm.router.LocalComicDetailRoute
import com.par9uet.jm.router.LocalSettingRoute
import com.par9uet.jm.router.LoginRoute
import com.par9uet.jm.router.SignInRoute
import com.par9uet.jm.router.TabRoute
import com.par9uet.jm.router.UserCollectComicRoute
import com.par9uet.jm.router.UserHistoryComicRoute
import com.par9uet.jm.router.UserHistoryCommentRoute
import com.par9uet.jm.ui.provider.LocalMainNavController
import com.par9uet.jm.ui.screens.downloadScreen.DownloadScreen
import com.par9uet.jm.ui.screens.localSettingScreen.LocalSettingScreen
import com.par9uet.jm.ui.screens.readScreen.ComicReadScreen
import com.par9uet.jm.ui.screens.tabScreen.TabScreen

@Composable
fun AppScreen() {
    val mainNavController = rememberNavController()
    CompositionLocalProvider(
        LocalMainNavController provides mainNavController,
    ) {
        NavHost(
            modifier = Modifier.fillMaxSize(),
            navController = mainNavController,
//            startDestination = "comicQuickSearch/百合",
//             startDestination = "appLocalSetting",
            startDestination = TabRoute(tabName = "home"),
//            startDestination = "comicRead/1044155",
//            startDestination = "comicDetail/1044155",
//            startDestination = "comicDetail/1454181",
//            startDestination = "comicSearch",
//            startDestination = "sign",
//            startDestination = "download",
//            startDestination = "category",
//             startDestination = "about",
//             startDestination = "apiSelect",
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
            composable<TabRoute> { backStackEntry ->
                val route = backStackEntry.toRoute<TabRoute>()
                TabScreen(tabName = route.tabName)
            }
            composable<LoginRoute> { LoginScreen() }
            composable<UserCollectComicRoute> { UserCollectComicScreen() }
            composable<UserHistoryComicRoute> { UserHistoryComicScreen() }
            composable<UserHistoryCommentRoute> { UserHistoryCommentScreen() }
            composable<LocalSettingRoute> { LocalSettingScreen() }
            composable<ComicDetailRoute> { backStackEntry ->
                val route = backStackEntry.toRoute<ComicDetailRoute>()
                ComicDetailScreen(comicId = route.comicId)
            }
            composable<ComicChapterRoute> { backStackEntry ->
                val route = backStackEntry.toRoute<ComicChapterRoute>()
                ComicChapterReadScreen(
                    comicChapterList = route.comicChapterList
                )
            }
            composable<ComicRelateRoute> { backStackEntry ->
                val route = backStackEntry.toRoute<ComicRelateRoute>()
                ComicRelateListScreen(relateComicList = route.relateComicList)
            }
            composable<ComicReadRoute> { backStackEntry ->
                val route = backStackEntry.toRoute<ComicReadRoute>()
                ComicReadScreen(comicId = route.comicId)
            }
            composable<ComicSearchRoute> { ComicSearchScreen() }
            composable<ComicSearchResultRoute> { backStackEntry ->
                val route = backStackEntry.toRoute<ComicSearchResultRoute>()
                ComicSearchResultScreen(
                    searchContent = route.searchContent
                )
            }
            composable<ComicRecommendRoute> { ComicWeekRecommendScreen() }
            composable<ComicCommentRoute> { backStackEntry ->
                val route = backStackEntry.toRoute<ComicCommentRoute>()
                ComicCommentScreen(comicId = route.comicId)
            }
            composable<SignInRoute> { SignInScreen() }
            composable<ComicCategoryRoute> { ComicCategoryScreen() }
            composable<DownloadRoute> { DownloadScreen() }
            composable<ComicChapterDownloadRoute> { backStackEntry ->
                val route = backStackEntry.toRoute<ComicChapterDownloadRoute>()
                ComicChapterDownloadScreen(
                    comicChapterList = route.comicChapterList
                )
            }
            composable<AboutRoute> { AboutScreen() }
            composable<ApiSelectRoute> { ApiSelectScreen() }
            composable<LocalComicDetailRoute> { backStackEntry ->
                val route = backStackEntry.toRoute<LocalComicDetailRoute>()
                LocalComicDetailScreen(comicId = route.comicId)
            }
        }
    }
}
