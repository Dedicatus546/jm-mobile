package com.par9uet.jm

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.par9uet.jm.ui.screens.AppScreen
import com.par9uet.jm.ui.theme.AppTheme
import com.par9uet.jm.ui.viewModel.GlobalViewModel
import com.par9uet.jm.ui.viewModel.RemoteSettingViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.viewmodel.koinActivityViewModel

@Composable
fun App(
    globalViewModel: GlobalViewModel = koinViewModel(),
    remoteSettingViewModel: RemoteSettingViewModel = koinActivityViewModel()
) {
    LaunchedEffect(Unit) {
        remoteSettingViewModel.getRemoteSetting()
    }
    AppTheme(content = {
        AppScreen()
    })
}