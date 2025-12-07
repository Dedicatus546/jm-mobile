package com.par9uet.jm.ui.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.par9uet.jm.data.models.AutoLogin
import com.par9uet.jm.data.models.RemoteSetting
import com.par9uet.jm.data.models.User
import com.par9uet.jm.retrofit.model.LoginResponse
import com.par9uet.jm.retrofit.model.NetWorkResult
import com.par9uet.jm.retrofit.model.RemoteSettingResponse
import com.par9uet.jm.retrofit.repository.GlobalRepository
import com.par9uet.jm.retrofit.repository.RemoteSettingRepository
import com.par9uet.jm.retrofit.repository.UserRepository
import com.par9uet.jm.storage.SecureStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GlobalViewModel(
    private val globalRepository: GlobalRepository,
    private val remoteSettingRepository: RemoteSettingRepository,
    private val userRepository: UserRepository,
    private val secureStorage: SecureStorage
) : ViewModel() {
    data class UserState(val loading: Boolean, val user: User, val isLogin: Boolean)

    val userState
        get() = UserState(
            loading = globalRepository.userLoading,
            user = globalRepository.user,
            isLogin = globalRepository.user.id > 0
        )

    data class SettingState(val loading: Boolean, val remoteSetting: RemoteSetting)

    val settingState
        get() = SettingState(
            loading = globalRepository.settingLoading,
            remoteSetting = globalRepository.remoteSetting,
        )

    val isAutoLogin get() = globalRepository.autoLogin != null
    val username get() = globalRepository.autoLogin?.username ?: ""
    val password get() = globalRepository.autoLogin?.password ?: ""

    fun getSetting() {
        viewModelScope.launch {
            globalRepository.settingLoading = true
            when (val data = withContext(Dispatchers.IO) {
                remoteSettingRepository.getRemoteSetting()
            }) {
                is NetWorkResult.Error<*> -> {
                    Log.v("api", data.message)
                }

                is NetWorkResult.Success<RemoteSettingResponse> -> {
                    globalRepository.remoteSetting =
                        globalRepository.remoteSetting.copy(imgHost = data.data.img_host)
                }
            }
            globalRepository.settingLoading = false
        }
    }

    fun tryAutoLogin() {
        val autoLogin = globalRepository.autoLogin
        if (autoLogin != null) {
            val username = autoLogin.username
            val password = autoLogin.password
            login(username, password)
        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            globalRepository.userLoading = true
            when (val data = withContext(Dispatchers.IO) {
                userRepository.login(username, password)
            }) {
                is NetWorkResult.Error<*> -> {
                    Log.v("api", data.message)
                }

                is NetWorkResult.Success<LoginResponse> -> {
                    globalRepository.user = globalRepository.user.copy(
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
                    secureStorage.saveUser(globalRepository.user)
                }
            }

            globalRepository.userLoading = false
        }
    }

    fun saveLoginInfo(username: String, password: String) {
        secureStorage.saveAutoLogin(AutoLogin(username, password))
    }
}