package com.par9uet.jm

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.par9uet.jm.ui.screens.AppScreen
import com.par9uet.jm.ui.theme.AppTheme
import com.par9uet.jm.viewModel.SettingViewModel
import com.par9uet.jm.viewModel.rememberAppNavigateViewModel

@Composable
fun App() {
    val settingViewModel: SettingViewModel = viewModel(LocalContext.current as ComponentActivity)
    LaunchedEffect(Unit) {
        settingViewModel.getSetting()
    }

    val appNavigateViewModel = rememberAppNavigateViewModel()
    val navController = rememberNavController()
    LaunchedEffect(navController) {
        appNavigateViewModel.setNavController(navController)
    }

    AppTheme(content = {
        AppScreen()
    })
}