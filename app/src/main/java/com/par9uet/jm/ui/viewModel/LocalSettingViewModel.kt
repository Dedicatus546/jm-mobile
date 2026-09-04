package com.par9uet.jm.ui.viewModel

import androidx.lifecycle.ViewModel
import com.par9uet.jm.data.models.LocalSetting
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

@HiltViewModel
class LocalSettingViewModel @Inject constructor(
) : ViewModel() {

    fun changeLocalSetting(nLocalSetting: LocalSetting) {
//        secureStorage.saveLocalSetting(nLocalSetting)
    }
}