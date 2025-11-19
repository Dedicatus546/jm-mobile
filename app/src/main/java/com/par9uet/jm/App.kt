package com.par9uet.jm

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.par9uet.jm.ui.screens.AppScreen
import com.par9uet.jm.ui.theme.AppTheme
import com.par9uet.jm.viewModel.SettingViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun App(
    settingViewModel: SettingViewModel = koinViewModel()
) {
    LaunchedEffect(Unit) {
        settingViewModel.getSetting()
    }
    AppTheme(content = {
        AppScreen()
    })
}