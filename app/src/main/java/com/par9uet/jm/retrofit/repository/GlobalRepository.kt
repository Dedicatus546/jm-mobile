package com.par9uet.jm.retrofit.repository

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.par9uet.jm.data.models.Setting

class GlobalRepository(
    private val settingRepository: SettingRepository
): BaseRepository() {
    var setting by mutableStateOf(Setting(""))
    var loading by mutableStateOf(false)
}