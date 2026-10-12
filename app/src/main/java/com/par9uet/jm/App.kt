package com.par9uet.jm

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.par9uet.jm.ui.components.Calculator
import com.par9uet.jm.ui.compsable.FakeAppIconEffect
import com.par9uet.jm.ui.provider.LocalLocalSettingManager
import com.par9uet.jm.ui.provider.LocalToastManager
import com.par9uet.jm.ui.screens.AppScreen
import com.par9uet.jm.ui.viewModel.GlobalViewModel
import com.par9uet.jm.utils.hiltActivityViewModel

@Composable
fun App() {
    val globalViewModel: GlobalViewModel = hiltActivityViewModel()
    val localSettingManager = LocalLocalSettingManager.current
    val toastManager = LocalToastManager.current
    val activity = LocalActivity.current

    val isLockFakeApp by localSettingManager.isLockFakeApp.collectAsState()
    val currentAliasName = activity?.componentName?.className ?: ""

    FakeAppIconEffect()
    if (currentAliasName.contains("Calculator") && isLockFakeApp) {
        Calculator()
    } else {
        LaunchedEffect(Unit) {
            globalViewModel.init()
        }
        val snackbarHostState = remember { SnackbarHostState() }
        LaunchedEffect(Unit) {
            toastManager.message.collect { text ->
                snackbarHostState.showSnackbar(
                    message = text,
                    actionLabel = null,
                    withDismissAction = true,
                    duration = SnackbarDuration.Short
                )
            }
        }
        Box(modifier = Modifier.fillMaxSize()) {
            AppScreen()
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding() // 自动避开系统导航栏
                    .padding(bottom = 80.dp)
                    .imePadding()
            )
        }
    }
}