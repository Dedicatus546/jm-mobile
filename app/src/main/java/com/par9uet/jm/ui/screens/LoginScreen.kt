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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecureTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.par9uet.jm.R
import com.par9uet.jm.store.UserManager
import com.par9uet.jm.ui.viewModel.UserViewModel
import org.koin.compose.getKoin
import org.koin.compose.viewmodel.koinActivityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    userManager: UserManager = getKoin().get(),
    userViewModel: UserViewModel = koinActivityViewModel(),
) {
    val focusManager = LocalFocusManager.current
    val mainNavController = LocalMainNavController.current
    val usernameTextFieldState = rememberTextFieldState()
    val usernameFocusRequester = remember { FocusRequester() }
    val passwordTextFieldState = rememberTextFieldState()
    val passwordFocusRequester = remember { FocusRequester() }
    val isLogin by userManager.isLoginState.collectAsState(false)
    val loginState by userViewModel.loginState.collectAsState()

    LaunchedEffect(isLogin) {
        if (isLogin) {
            mainNavController.navigate("tab/person") {
                popUpTo("login") {
                    inclusive = true
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        usernameFocusRequester.requestFocus()
    }

    fun toLogin() {
        val username = usernameTextFieldState.text.toString()
        val password = passwordTextFieldState.text.toString()
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
                lineLimits = TextFieldLineLimits.SingleLine,
                state = usernameTextFieldState,
                label = {
                    Text("用户名")
                },
                modifier = Modifier
                    .focusRequester(usernameFocusRequester)
                    .fillMaxWidth(),
                inputTransformation = InputTransformation {
                    if (!asCharSequence().matches(Regex("""\A\p{ASCII}*\z"""))) {
                        revertAllChanges()
                    }
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                onKeyboardAction = {
                    passwordFocusRequester.requestFocus()
                }
            )
            SecureTextField(
                state = passwordTextFieldState,
                label = {
                    Text("密码")
                },
                modifier = Modifier
                    .focusRequester(passwordFocusRequester)
                    .fillMaxWidth(),
                inputTransformation = InputTransformation {
                    if (!asCharSequence().matches(Regex("""\A\p{ASCII}*\z"""))) {
                        revertAllChanges()
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                onKeyboardAction = {
                    focusManager.clearFocus()
                    toLogin()
                }
            )
            Button(
                enabled = !loginState.isLoading,
                onClick = { toLogin() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (!loginState.isLoading) {
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