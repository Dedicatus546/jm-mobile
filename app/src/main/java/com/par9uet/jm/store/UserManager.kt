package com.par9uet.jm.store

import com.par9uet.jm.data.models.User
import com.par9uet.jm.repository.UserRepository
import com.par9uet.jm.retrofit.Retrofit
import com.par9uet.jm.retrofit.model.LoginResponse
import com.par9uet.jm.retrofit.model.NetWorkResult
import com.par9uet.jm.storage.CookieStorage
import com.par9uet.jm.storage.UserStorage
import com.par9uet.jm.task.AppInitTask
import com.par9uet.jm.task.AppTaskInfo
import com.par9uet.jm.utils.log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class UserManager(
    private val userStorage: UserStorage,
    private val cookieStorage: CookieStorage,
    private val userRepository: UserRepository,
    private val retrofit: Retrofit
) : AppInitTask {
    private val _userState = MutableStateFlow(User.create())
    val userState = _userState.asStateFlow()
    val cookieListState get() = cookieStorage.state

    val isLoginState = _userState.map { it.id > 0 }

    private val appTaskInfo = AppTaskInfo(
        taskName = "加载上次退出前保存的用户信息",
        sort = 4,
    )

    fun updateUser(user: User) {
        _userState.update {
            user
        }
        userStorage.set(user)
    }

    fun clearUser() {
        _userState.update {
            User.create()
        }
        retrofit.clearCookie()
        userStorage.remove()
        cookieStorage.remove()
    }

    suspend fun autoLogin(username: String, password: String) {
        when (val data = userRepository.login(username, password)) {
            is NetWorkResult.Error -> {
                clearUser()
                log("登录失败，原因：${data.message}")
            }

            is NetWorkResult.Success<LoginResponse> -> {
                updateUser(
                    data.data.toUser(
                        password = password
                    )
                )
                log("登录成功")
            }
        }
    }

    override suspend fun init() {
        log("用户信息开始初始化")
        log("加载本地用户、cookie、登录信息")
        _userState.update {
            userStorage.get()
        }
        log("已加载本地用户、cookie、登录信息")
        if (_userState.value.username.isNotEmpty() && _userState.value.password.isNotEmpty()) {
            val username = _userState.value.username
            val password = _userState.value.password
            log("检测到已保存了用户登录信息，开始执行一次用户登录")
            autoLogin(username, password)
        }
        log("用户信息初始化结束")
    }

    override fun getAppTaskInfo(): AppTaskInfo = appTaskInfo
}