package com.par9uet.jm.viewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.par9uet.jm.data.models.Setting
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
    var setting
        get() = globalRepository.setting
        set(value) {
            globalRepository.setting = value
        }
    var loading
        get() = globalRepository.loading
        set(value) {
            globalRepository.loading = value
        }

    fun getSetting() {
        viewModelScope.launch {
            loading = true
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
                    setting = setting.copy(imgHost = data.data.img_host)
                    Log.d("api setting", setting.toString())
                }
            }
            loading = false
        }
    }
}