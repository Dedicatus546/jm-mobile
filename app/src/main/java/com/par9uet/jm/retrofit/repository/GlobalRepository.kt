package com.par9uet.jm.retrofit.repository

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.par9uet.jm.data.models.Setting
import com.par9uet.jm.retrofit.Retrofit
import com.par9uet.jm.storage.SecureStorage
import com.par9uet.jm.utils.createUser

class GlobalRepository(private val secureStorage: SecureStorage) : BaseRepository() {
    var setting by mutableStateOf(Setting(""))
    var settingLoading by mutableStateOf(false)

    var user by mutableStateOf(secureStorage.getUser() ?: createUser())
    var userLoading by mutableStateOf(false)
}