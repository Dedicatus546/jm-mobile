package com.par9uet.jm.ui.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.reflect.TypeToken
import com.par9uet.jm.data.models.LocalSetting
import com.par9uet.jm.data.models.RemoteSetting
import com.par9uet.jm.data.models.User
import com.par9uet.jm.retrofit.LoginCookieJar
import com.par9uet.jm.retrofit.model.LoginResponse
import com.par9uet.jm.retrofit.model.NetWorkResult
import com.par9uet.jm.retrofit.repository.LocalSettingRepository
import com.par9uet.jm.retrofit.repository.RemoteSettingRepository
import com.par9uet.jm.retrofit.repository.UserRepository
import com.par9uet.jm.storage.SecureStorage
import com.par9uet.jm.task.startTask.RemoteSettingTask
import com.par9uet.jm.task.startTask.TryAutoLoginTask
import com.par9uet.jm.ui.models.CommonUIState
import com.par9uet.jm.utils.createUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GlobalViewModel(
    private val remoteSettingRepository: RemoteSettingRepository,
    private val userRepository: UserRepository,
    private val localSettingRepository: LocalSettingRepository,
    private val secureStorage: SecureStorage,
    private val cookieJar: LoginCookieJar,
    private val remoteSettingTask: RemoteSettingTask,
    private val tryAutoLoginTask: TryAutoLoginTask
) : ViewModel() {

    data class GlobalData(
        val remoteSetting: RemoteSetting = RemoteSetting(
            imgHost = ""
        ),
        val localSetting: LocalSetting = LocalSetting(),
        val user: User = createUser(),
        val isAutoLogin: Boolean = false,
        val username: String = "",
        val password: String = "",
    )

    private val _state = MutableStateFlow(
        CommonUIState(
            data = GlobalData()
        )
    )
    val remoteSettingState = _state.map { it.data.remoteSetting }
    val localSettingState = _state.map { it.data.localSetting }
    val userState = _state.map { it.data.user }
    val state = _state.asStateFlow()

    private suspend fun tryAutoLogin() {
        if (state.value.data.isAutoLogin) {
            Log.d("GlobalViewModel", "已开启自动登录功能")
            val username = state.value.data.username
            val password = state.value.data.password
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
            is NetWorkResult.Error -> {
                Log.v("api", data.message)
            }

            is NetWorkResult.Success<LoginResponse> -> {
                _state.update {
                    it.copy(
                        data = it.data.copy(
                            user = it.data.user.copy(
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
                        )
                    )
                }
                secureStorage.save("user", state.value.data.user)
            }
        }
    }

    fun logout() {
        viewModelScope.launch(Dispatchers.IO) {
            clearUser()
        }
    }

    private fun clearUser() {
        _state.update {
            it.copy(
                data = it.data.copy(
                    user = createUser(),
                    username = "",
                    password = ""
                )
            )
        }
        secureStorage.save("user", state.value.data.user)
        secureStorage.remove("autoLogin")
        secureStorage.remove("username")
        secureStorage.remove("password")
        cookieJar.clearCookie()
    }

    private fun loadFromStorage() {
        val user =
            secureStorage.get<User>("user", object : TypeToken<User>() {}.type) ?: createUser()
        val isAutoLogin =
            secureStorage.get<Boolean>("autoLogin", object : TypeToken<Boolean>() {}.type) ?: false
        val localSetting = secureStorage.get<LocalSetting>(
            "localSetting",
            object : TypeToken<LocalSetting>() {}.type
        ) ?: LocalSetting()
        val username =
            secureStorage.get<String>("username", object : TypeToken<String>() {}.type) ?: ""
        val password =
            secureStorage.get<String>("password", object : TypeToken<String>() {}.type) ?: ""
        _state.update {
            it.copy(
                data = it.data.copy(
                    user = user,
                    isAutoLogin = isAutoLogin,
                    username = username,
                    password = password,
                    localSetting = localSetting
                )
            )
        }
    }

    fun init() {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("GlobalViewModel", "开始全局初始化")
            _state.update {
                it.copy(
                    isLoading = true
                )
            }
            loadFromStorage()
            val remoteSettingTaskResult = remoteSettingTask.run()
            if (remoteSettingTaskResult.isFailure) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        isError = true,
                        errorMsg = "加载远程 APP 初始化数据失败"
                    )
                }
                return@launch
            }
            val tryAutoLoginTaskResult = tryAutoLoginTask.run()
            if (tryAutoLoginTaskResult.isFailure) {
                clearUser()
                // 可以加个提示啥的
            }
            _state.update {
                it.copy(
                    isLoading = false,
                    data = it.data.copy(
                        remoteSetting = remoteSettingTaskResult.data!!,
                        user = tryAutoLoginTaskResult.data!!
                    )
                )
            }
        }
    }
}