package com.par9uet.jm.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.par9uet.jm.retrofit.model.NetWorkResult
import com.par9uet.jm.retrofit.model.SettingResponse
import com.par9uet.jm.retrofit.repository.GlobalRepository
import com.par9uet.jm.retrofit.repository.SettingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingViewModel(
    private val settingRepository: SettingRepository,
    private val globalRepository: GlobalRepository
) : ViewModel() {

    fun getSetting() {
        viewModelScope.launch {
            globalRepository.settingLoading = true
            when (val data = withContext(Dispatchers.IO) {
                settingRepository.getSetting()
            }) {
                is NetWorkResult.Error<*> -> {
                    Log.v("api", data.message)
                }

                is NetWorkResult.Loading<*> -> {
                    Log.v("api", "loading")
                }

                is NetWorkResult.Success<SettingResponse> -> {
                    globalRepository.setting = globalRepository.setting.copy(imgHost = data.data.img_host)
                }
            }
            globalRepository.settingLoading = false
        }
    }
}