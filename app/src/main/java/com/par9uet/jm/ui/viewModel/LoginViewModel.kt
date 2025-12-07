package com.par9uet.jm.ui.viewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.par9uet.jm.retrofit.model.LoginResponse
import com.par9uet.jm.retrofit.model.NetWorkResult
import com.par9uet.jm.retrofit.repository.UserRepository
import com.par9uet.jm.storage.SecureStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel(
    private val secureStorage: SecureStorage,
    private val userRepository: UserRepository
) : ViewModel() {

    var loginLoading by mutableStateOf(false)

    fun login(username: String, password: String) {
        viewModelScope.launch {
            loginLoading = true
            when (val data = withContext(Dispatchers.IO) {
                userRepository.login(username, password)
            }) {
                is NetWorkResult.Error<*> -> {
                    Log.v("api", data.message)
                }

                is NetWorkResult.Success<LoginResponse> -> {
                    userRepository.user = userRepository.user.copy(
                        id = data.data.uid,
                        username = data.data.username,
                        avatar = data.data.photo,
                        level = data.data.level,
                        levelName = data.data.level_name,
                        currentLevelExp = data.data.exp,
                        nextLevelExp = data.data.nextLevelExp,
                        currentCollectCount = data.data.album_favorites,
                        maxCollectCount = data.data.album_favorites_max,
                        jCoin = data.data.coin.toInt(),
                    )
                    secureStorage.save("user", userRepository.user)
                }
            }
            loginLoading = false
        }
    }

    fun enableAutoLogin(username: String, password: String) {
        secureStorage.save("autoLogin", true)
        secureStorage.save("username", username)
        secureStorage.save("password", password)
        userRepository.username = username
        userRepository.password = password
        userRepository.isAutoLogin = true
    }

}