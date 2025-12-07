package com.par9uet.jm.retrofit.repository

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.par9uet.jm.data.models.LocalSetting

class LocalSettingRepository : BaseRepository() {
    var localSetting by mutableStateOf(LocalSetting())
}