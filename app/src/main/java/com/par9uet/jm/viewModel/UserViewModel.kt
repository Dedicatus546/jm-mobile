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
                loginApi(username, password).data
            }
            userInfo = UserInfo(
                data.uid.toInt(),
                data.username,
                data.photo,
                data.level,
                data.level_name,
                data.exp.toInt(),
                data.nextLevelExp,
                data.album_favorites.toInt(),
                data.album_favorites_max,
                data.coin
            )
            loading = false
        }
    }

    fun logout() {
        userInfo = UserInfo()
    }
}