package com.par9uet.jm

import androidx.compose.runtime.Composable
import com.par9uet.jm.ui.screens.AppScreen
import com.par9uet.jm.ui.theme.AppTheme

@Composable
fun App() {
    AppTheme(content = {
        AppScreen()
    })
}