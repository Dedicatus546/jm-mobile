package com.par9uet.jm.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.par9uet.jm.data.models.LocalSetting
import com.par9uet.jm.storage.SecureStorage

class LocalSettingViewModel(
    private val secureStorage: SecureStorage,
) : ViewModel() {
    var localSetting by mutableStateOf(secureStorage.getLocalSetting() ?: LocalSetting())

    fun changeLocalSetting(nLocalSetting: LocalSetting) {
        localSetting = nLocalSetting
        secureStorage.saveLocalSetting(nLocalSetting)
    }
}