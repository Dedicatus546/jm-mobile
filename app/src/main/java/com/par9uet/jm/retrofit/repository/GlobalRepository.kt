package com.par9uet.jm.retrofit.repository

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.par9uet.jm.data.models.AutoLogin
import com.par9uet.jm.data.models.RemoteSetting
import com.par9uet.jm.retrofit.model.LoginResponse
import com.par9uet.jm.retrofit.model.NetWorkResult
import com.par9uet.jm.storage.SecureStorage
import com.par9uet.jm.utils.createUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GlobalRepository(
    private val secureStorage: SecureStorage,
    userRepository: UserRepository
) : BaseRepository() {
    var remoteSetting by mutableStateOf(RemoteSetting(""))
    var settingLoading by mutableStateOf(false)

    var user by mutableStateOf(secureStorage.getUser() ?: createUser())
    var userLoading by mutableStateOf(false)

    var autoLogin by mutableStateOf(secureStorage.getAutoLogin())
}