package com.par9uet.jm.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.par9uet.jm.R
import com.par9uet.jm.viewModel.GlobalViewModel
import com.par9uet.jm.viewModel.UserViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    userViewModel: UserViewModel = koinViewModel(),
    globalViewModel: GlobalViewModel = koinViewModel()
) {
    val mainNavController = LocalMainNavController.current
    // test user and password
    var username by remember { mutableStateOf("par9uet") }
    var password by remember { mutableStateOf("eP2jAKYW") }
    var isAutoLogin by remember { mutableStateOf(false) }
    val userState = globalViewModel.userState

    LaunchedEffect(userState.isLogin) {
        if (userState.isLogin) {
            mainNavController.navigate("tab/person") {
                popUpTo("login") {
                    inclusive = true
                }
            }
        }
    }

    fun toLogin() {
        if (username.isBlank() || password.isBlank()) {
            return
        }
        userViewModel.login(username, password)
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("登录")
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            mainNavController.popBackStack()
                        }
                    ) {
                        Icon(
                            painterResource(R.drawable.chevron_left_icon),
                            "返回",
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_with_name),
                contentDescription = "",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(837f / 263),
                contentScale = ContentScale.FillBounds
            )
            TextField(
                value = username,
                onValueChange = { v ->
                    username = v
                },
                label = {
                    Text("用户名")
                },
                modifier = Modifier
                    .fillMaxWidth()
            )
            TextField(
                value = password,
                onValueChange = { v ->
                    password = v
                },
                label = {
                    Text("密码")
                },
                modifier = Modifier
                    .fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically // 垂直居中
            ) {
                Checkbox(
                    checked = isAutoLogin,
                    onCheckedChange = { v -> isAutoLogin = v }
                )
                Text("自动登录")
            }
            FilledTonalButton(
                enabled = !userState.loading,
                onClick = { toLogin() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (!userState.loading) {
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
        }
    }
}