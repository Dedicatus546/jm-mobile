package com.par9uet.jm.store

import com.par9uet.jm.data.models.User
import com.par9uet.jm.storage.CookieStorage
import com.par9uet.jm.storage.UserStorage
import com.par9uet.jm.task.AppInitTask
import com.par9uet.jm.task.AppTaskInfo
import com.par9uet.jm.utils.createUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

class UserManager(
    private val userStorage: UserStorage,
    private val cookieStorage: CookieStorage
) : AppInitTask {
    private val _userState = MutableStateFlow<User?>(null)
    val userState = _userState.asStateFlow()
    private val _cookieListState = MutableStateFlow<List<Cookie>?>(null)
    val cookieListState = _cookieListState.asStateFlow()

    private val appTaskInfo = AppTaskInfo(
        taskName = "加载上次退出前保存的用户信息",
        sort = 2,
    )

    val cookieJar = object : CookieJar {
        override fun saveFromResponse(
            url: HttpUrl,
            cookies: List<Cookie>
        ) {
            val currentCookieList = _cookieListState.value ?: listOf()
            _cookieListState.update {
                (currentCookieList + cookies).associateBy { "${it.domain}:${it.path}:${it.name}" }.values.toList()
            }
            cookieStorage.set(_cookieListState.value!!)
        }

        override fun loadForRequest(url: HttpUrl): List<Cookie> {
            return _cookieListState.value ?: cookieStorage.get()
        }

    }

    fun updateUser(user: User) {
        _userState.update {
            user
        }
        userStorage.set(user)
    }

    fun logout() {
        _userState.update {
            createUser()
        }
        _cookieListState.update {
            listOf()
        }
        userStorage.remove()
        cookieStorage.remove()
    }

    override suspend fun init() {
        _userState.update {
            userStorage.get()
        }
        _cookieListState.update {
            cookieStorage.get()
        }
    }

    override fun getAppTaskInfo(): AppTaskInfo = appTaskInfo
}