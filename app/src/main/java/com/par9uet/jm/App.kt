package com.par9uet.jm

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.par9uet.jm.ui.screens.IndexScreen
import com.par9uet.jm.ui.theme.AppTheme
import com.par9uet.jm.viewModel.IndexNavigateViewModel
import com.par9uet.jm.viewModel.SettingViewModel

@Composable
fun App() {
    val settingViewModel: SettingViewModel = viewModel(LocalContext.current as ComponentActivity)
    LaunchedEffect(Unit) {
        settingViewModel.getSetting()
    }

    val indexNavigateViewModel: IndexNavigateViewModel =
        viewModel(LocalContext.current as ComponentActivity)
    val navController = rememberNavController()
    LaunchedEffect(navController) {
        indexNavigateViewModel.setNavController(navController)
    }

    AppTheme(content = {
        IndexScreen()
    })
}