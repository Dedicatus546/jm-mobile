package com.par9uet.jm

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.par9uet.jm.ui.screens.AppScreen
import com.par9uet.jm.ui.theme.AppTheme
import com.par9uet.jm.ui.viewModel.GlobalViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun App(
    globalViewModel: GlobalViewModel = koinViewModel()
) {
    LaunchedEffect(Unit) {
        globalViewModel.init()
    }
    AppTheme(content = {
        AppScreen()
    })
}