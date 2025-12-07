package com.par9uet.jm.ui.viewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.reflect.TypeToken
import com.par9uet.jm.data.models.LocalSetting
import com.par9uet.jm.data.models.User
import com.par9uet.jm.retrofit.LoginCookieJar
import com.par9uet.jm.retrofit.model.LoginResponse
import com.par9uet.jm.retrofit.model.NetWorkResult
import com.par9uet.jm.retrofit.model.RemoteSettingResponse
import com.par9uet.jm.retrofit.repository.LocalSettingRepository
import com.par9uet.jm.retrofit.repository.RemoteSettingRepository
import com.par9uet.jm.retrofit.repository.UserRepository
import com.par9uet.jm.storage.SecureStorage
import com.par9uet.jm.utils.createUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GlobalViewModel(
    private val remoteSettingRepository: RemoteSettingRepository,
    private val userRepository: UserRepository,
    private val localSettingRepository: LocalSettingRepository,
    private val secureStorage: SecureStorage,
    private val cookieJar: LoginCookieJar,
) : ViewModel() {
    var loading by mutableStateOf(false)

    private suspend fun getRemoteSetting() {
        when (val data = remoteSettingRepository.getRemoteSetting()) {
            is NetWorkResult.Error<*> -> {
                Log.v("api", data.message)
            }

            is NetWorkResult.Success<RemoteSettingResponse> -> {
                remoteSettingRepository.remoteSetting =
                    remoteSettingRepository.remoteSetting.copy(imgHost = data.data.img_host)
            }
        }
    }

    private suspend fun tryAutoLogin() {
        if (userRepository.isAutoLogin) {
            Log.d("GlobalViewModel", "已开启自动登录功能")
            val username = userRepository.username
            val password = userRepository.password
            if (username.isNotBlank() && password.isNotBlank()) {
                login(username, password)
                Log.d("GlobalViewModel", "已执行登录")
            } else {
                Log.d("GlobalViewModel", "虽开启自动登录功能，但用户名或密码为空")
            }
        } else {
            Log.d("GlobalViewModel", "没有开启自动登录功能")
        }
    }

    suspend fun login(username: String, password: String) {
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
    }

    fun logout() {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.user = createUser()
            secureStorage.save("user", userRepository.user)
            clearAutoLogin()
            cookieJar.clearCookie()
        }
    }

    fun loadFromStorage() {
        secureStorage.get<User>("user", object : TypeToken<User>() {}.type)?.let {
            userRepository.user = it
        }
        secureStorage.get<Boolean>("autoLogin", object : TypeToken<Boolean>() {}.type)?.let {
            userRepository.isAutoLogin = it
        }
        secureStorage.get<LocalSetting>("localSetting", object : TypeToken<LocalSetting>() {}.type)
            ?.let {
                localSettingRepository.localSetting = it
            }
    }

    fun clearAutoLogin() {
        secureStorage.remove("autoLogin")
        secureStorage.remove("username")
        secureStorage.remove("password")
        userRepository.isAutoLogin = false
        userRepository.username = ""
        userRepository.password = ""
    }

    fun init() {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("GlobalViewModel", "开始全局初始化")
            loading = true
            loadFromStorage()
            Log.d("GlobalViewModel", "已加载本地数据")
            tryAutoLogin()
            Log.d("GlobalViewModel", "已尝试自动登录")
            getRemoteSetting()
            Log.d("GlobalViewModel", "已获取应用远程配置数据")
            loading = false
        }
    }
}