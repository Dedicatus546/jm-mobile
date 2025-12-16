package com.par9uet.jm.ui.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.par9uet.jm.repository.UserRepository
import com.par9uet.jm.retrofit.model.LoginResponse
import com.par9uet.jm.retrofit.model.NetWorkResult
import com.par9uet.jm.storage.SecureStorage
import com.par9uet.jm.ui.models.CommonUIState
import com.par9uet.jm.utils.createUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserViewModel(
    private val secureStorage: SecureStorage,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(
        CommonUIState(
            data = createUser()
        )
    )
    val state = _state.asStateFlow()
    val isLogin = _state.map { it.data.id > 0 }
    private var username = ""
    private var password = ""

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true)
            }
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
                    }
                    secureStorage.set("user", _state.value.data)
                }
            }
            _state.update {
                it.copy(isLoading = false)
            }
        }
    }

    fun enableAutoLogin(username: String, password: String) {
        secureStorage.set("autoLogin", true)
        secureStorage.set("username", username)
        secureStorage.set("password", password)
        this.username = username
        this.password = password
    }
}