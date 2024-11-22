package com.par9uet.jm.viewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.par9uet.jm.http.loginApi
import com.par9uet.jm.retrofit.Client
import com.par9uet.jm.retrofit.UserService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.await

data class UserInfo(
    val id: Int = 0,
    val username: String = "",
    val avatar: String = "",
    val level: Int = 0,
    val levelName: String = "",
    val currentLevelExp: Int = 0,
    val nextLevelExp: Int = 0,
    val currentCollectCount: Int = 0,
    val maxCollectCount: Int = 0,
    val jCoin: Int = 0,
)

class UserViewModel : ViewModel() {
    var userInfo by mutableStateOf(UserInfo())
    var loading by mutableStateOf(false)
    val isLogin: Boolean
        get() {
            return userInfo.id > 0
        }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            loading = true
            val data = withContext(Dispatchers.IO) {
                Client.create(UserService::class.java).login(username, password).await()
            }
            Log.v("test1", data.toString())
            loading = false
        }
    }

    fun logout() {
        userInfo = UserInfo()
    }
}