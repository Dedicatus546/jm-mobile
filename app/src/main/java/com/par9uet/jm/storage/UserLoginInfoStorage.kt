package com.par9uet.jm.storage

import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class UserLoginInfoStorage(
    private val secureStorage: SecureStorage
) {
    data class UserLoginInfo(
        val isAutoLogin: Boolean = false,
        val username: String = "",
        val password: String = ""
    )

    companion object {
        private const val STORAGE_KEY = "userLoginInfo"
    }

    private var _state = MutableStateFlow<UserLoginInfo?>(null)
    val state = _state.asStateFlow()

    fun set(isAutoLogin: Boolean, username: String, password: String) {
        _state.update {
            UserLoginInfo(
                isAutoLogin = isAutoLogin,
                username = username,
                password = password
            )
        }
        secureStorage.set(STORAGE_KEY, this.state.value)
    }

    fun get(): UserLoginInfo {
        if (_state.value == null) {
            _state.update {
                secureStorage.get(STORAGE_KEY, object : TypeToken<UserLoginInfo>() {}.type)
                    ?: UserLoginInfo()
            }
        }
        return _state.value!!
    }
}