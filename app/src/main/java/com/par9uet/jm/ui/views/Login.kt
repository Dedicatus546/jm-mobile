package com.par9uet.jm.ui.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.par9uet.jm.http.getSettingApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@Composable
fun Login() {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    fun toLogin() {
        if (username.isBlank() || password.isBlank()) {
            return
        }
        runBlocking {
            launch(Dispatchers.IO) {
                delay(3000)
                val res = getSettingApi()
                println("res = $res")
            }
        }

    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TextField(value = username, onValueChange = { v -> username = v }, label = {
            Text("用户名")
        }, modifier = Modifier.fillMaxWidth())
        TextField(value = password, onValueChange = { v -> password = v }, label = {
            Text("密码")
        }, modifier = Modifier.fillMaxWidth())
        Button(onClick = { toLogin() }, modifier = Modifier.fillMaxWidth()) {
            Text("登录")
        }
    }
}