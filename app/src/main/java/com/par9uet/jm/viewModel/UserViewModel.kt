package com.par9uet.jm.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.par9uet.jm.http.loginApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class UserInfo(
    val id: Int,
    val username: String,
    val avatar: String,
)

class UserViewModel : ViewModel() {
    var userInfo by mutableStateOf(UserInfo(4403997, "Par9uet", "4043997.jpg?v=1699018068?v="))
    var loading by mutableStateOf(false)
    val isLogin: Boolean
        get() {
            return userInfo.id > 0
        }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            loading = true
            val data = withContext(Dispatchers.IO) {
                loginApi(username, password).data
            }
            userInfo = UserInfo(
                data.uid.toInt(),
                data.username,
                data.photo
            )
            loading = false
        }
    }
}