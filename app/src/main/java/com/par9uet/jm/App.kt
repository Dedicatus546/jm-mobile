package com.par9uet.jm

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.par9uet.jm.ui.components.BottomNavigationBar
import com.par9uet.jm.ui.components.TopBar
import com.par9uet.jm.ui.theme.AppTheme
import com.par9uet.jm.ui.views.Home
import com.par9uet.jm.ui.views.Login
import com.par9uet.jm.ui.views.Person
import com.par9uet.jm.viewModel.NavigateViewModel
import com.par9uet.jm.viewModel.SettingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    val settingViewModel: SettingViewModel = viewModel(LocalContext.current as ComponentActivity)
    LaunchedEffect(Unit) {
        settingViewModel.getSetting()
    }
    val navigateViewModel: NavigateViewModel = viewModel(LocalContext.current as ComponentActivity)
    val navController = rememberNavController()
    LaunchedEffect(navController) {
        navigateViewModel.setNavController(navController)
    }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    AppTheme(content = {
        Scaffold(
            bottomBar = {
                if (currentRoute != "login") {
                    BottomNavigationBar(
                        navController = navController
                    )
                }
            },
            topBar = {
                TopBar()
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "person",
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxHeight()
                    .fillMaxWidth()
            ) {
                composable("home") {
                    Home()
                }
                composable("person") {
                    Person()
                }
                composable("login") {
                    Login()
                }
            }
        }
    })
}