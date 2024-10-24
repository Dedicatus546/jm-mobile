package com.par9uet.jm

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.par9uet.jm.ui.components.BottomNavigationBar
import com.par9uet.jm.ui.components.TopBar
import com.par9uet.jm.ui.theme.JMMobileTheme
import com.par9uet.jm.ui.views.Home
import com.par9uet.jm.ui.views.Login
import com.par9uet.jm.ui.views.Person

@Composable
fun App() {
    val navController = rememberNavController();
    JMMobileTheme(content = {
        Scaffold(
            bottomBar = {
                if (navController.currentDestination?.route != "login") {
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
                startDestination = "login",
                modifier = Modifier.padding(innerPadding)
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