package com.par9uet.jm

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.par9uet.jm.ui.screens.AppScreen
import com.par9uet.jm.ui.theme.AppTheme
import com.par9uet.jm.ui.viewModel.GlobalViewModel
import org.koin.compose.viewmodel.koinActivityViewModel

@Composable
fun App(
    globalViewModel: GlobalViewModel = koinActivityViewModel()
) {
    val state by globalViewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        globalViewModel.init()
    }
    if (state.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else if (state.isError) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            Column() {
                Text("初始化错误，原因${state.errorMsg}")
                Button(
                    onClick = {
                        globalViewModel.init()
                    }
                ) {
                    Text("重试")
                }
            }
        }
    } else {
        AppTheme(content = {
            AppScreen()
        })
    }
}