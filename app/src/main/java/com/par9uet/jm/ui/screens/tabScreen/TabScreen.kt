package com.par9uet.jm.ui.screens.tabScreen

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

private fun <T : BottomNav> getTransitionDirection(
    from: NavDestination?,
    to: NavDestination?,
    screens: List<T>
): Int {
    if (from == null || to == null) {
        return 1
    }

    val fromIndex = screens.indexOfFirst { it.route == from.route }
    val toIndex = screens.indexOfFirst { it.route == to.route }

    if (fromIndex == -1 || toIndex == -1) {
        return 1
    }

    return when {
        toIndex > fromIndex -> 1
        toIndex < fromIndex -> -1
        else -> 1
    }
}

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
                startDestination = tabName,
                enterTransition = {
                    val direction = getTransitionDirection(
                        from = initialState.destination,
                        to = targetState.destination,
                        screens = bottomNavList
                    )
                    slideInHorizontally(
                        initialOffsetX = { fullWidth -> fullWidth * direction },
                        animationSpec = tween(300)
                    )
                },
                exitTransition = {
                    val direction = getTransitionDirection(
                        from = initialState.destination,
                        to = targetState.destination,
                        screens = bottomNavList
                    )
                    slideOutHorizontally(
                        targetOffsetX = { fullWidth -> -fullWidth * direction },
                        animationSpec = tween(300)
                    )
                },
                popEnterTransition = {
                    val direction = getTransitionDirection(
                        from = initialState.destination,
                        to = targetState.destination,
                        screens = bottomNavList
                    )
                    slideInHorizontally(
                        initialOffsetX = { fullWidth -> -fullWidth * direction },
                        animationSpec = tween(300)
                    )
                },
                popExitTransition = {
                    val direction = getTransitionDirection(
                        from = initialState.destination,
                        to = targetState.destination,
                        screens = bottomNavList
                    )
                    slideOutHorizontally(
                        targetOffsetX = { fullWidth -> fullWidth * direction },
                        animationSpec = tween(300)
                    )
                }
            ) {
                bottomNavList.forEach { nav ->
                    composable(nav.route) {
                        nav.Content()
                    }
                }
            }
        }
    }
}

val LocalTabNavController = staticCompositionLocalOf<NavHostController> {
    error("none")
}