package com.par9uet.jm.ui.views

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.par9uet.jm.viewModel.NavigateViewModel
import com.par9uet.jm.viewModel.UserViewModel

@Composable
fun Login() {
    val userViewModel: UserViewModel = viewModel(LocalContext.current as ComponentActivity)
    val navigateViewModel: NavigateViewModel = viewModel(LocalContext.current as ComponentActivity)
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val loginLoading = userViewModel.loading
    val userInfo = userViewModel.userInfo
    val isLogin = userViewModel.isLogin

    LaunchedEffect(isLogin) {
        if (isLogin) {
            navigateViewModel.navigate("home")
        }
    }

    fun toLogin() {
        if (username.isBlank() || password.isBlank()) {
            return
        }
        userViewModel.login(username, password)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {
        TextField(value = username, onValueChange = { v -> username = v }, label = {
            Text("用户名")
        }, modifier = Modifier.fillMaxWidth())
        TextField(value = password, onValueChange = { v -> password = v }, label = {
            Text("密码")
        }, modifier = Modifier.fillMaxWidth())
        FilledTonalButton(
            enabled = !loginLoading,
            onClick = { toLogin() },
            modifier = Modifier.fillMaxWidth()
        ) {
            if (!loginLoading) {
                Text("登录")
            } else {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CircularProgressIndicator(
                        color = ButtonDefaults.buttonColors().disabledContainerColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Text("登录中")
                }
            }
        }
        Text("登录结果。username=${userInfo.username} id=${userInfo.id}")
    }
}