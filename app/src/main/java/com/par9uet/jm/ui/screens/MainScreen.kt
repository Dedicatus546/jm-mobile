package com.par9uet.jm.ui.screens

import androidx.activity.ComponentActivity
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.par9uet.jm.data.models.BottomNavigationRoute
import com.par9uet.jm.ui.components.BottomNavigationBarComponent
import com.par9uet.jm.ui.components.TopBarComponent
import com.par9uet.jm.viewModel.MainNavigateViewModel

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val mainNavigateViewModel: MainNavigateViewModel =
        viewModel(LocalContext.current as ComponentActivity)
    LaunchedEffect(navController) {
        mainNavigateViewModel.setNavController(navController)
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
            startDestination = BottomNavigationRoute.HOME.value
        ) {
            composable(
                BottomNavigationRoute.HOME.value,
                enterTransition = { slideInHorizontally(initialOffsetX = { width -> -width }) },
                exitTransition = { slideOutHorizontally(targetOffsetX = { width -> -width }) }) {
                HomeScreen()
            }
            composable(
                BottomNavigationRoute.PERSON.value,
                enterTransition = { slideInHorizontally(initialOffsetX = { width -> width }) },
                exitTransition = { slideOutHorizontally(targetOffsetX = { width -> width }) }) {
                PersonScreen()
            }
            composable(
                BottomNavigationRoute.LOGIN.value,
                enterTransition = { slideInHorizontally(initialOffsetX = { width -> width }) },
                exitTransition = { slideOutHorizontally(targetOffsetX = { width -> width }) }) {
                LoginScreen()
            }
        }
    }
}