package com.par9uet.jm.repository.impl

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.par9uet.jm.data.models.LocalSetting
import com.par9uet.jm.repository.BaseRepository
import com.par9uet.jm.repository.LocalSettingRepository

class LocalSettingRepositoryImpl : BaseRepository(), LocalSettingRepository {
    var localSetting by mutableStateOf(LocalSetting())
}