package com.par9uet.jm.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.par9uet.jm.retrofit.model.NetWorkResult
import com.par9uet.jm.retrofit.model.RemoteSettingResponse
import com.par9uet.jm.retrofit.repository.GlobalRepository
import com.par9uet.jm.retrofit.repository.RemoteSettingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingViewModel(
    private val remoteSettingRepository: RemoteSettingRepository,
    private val globalRepository: GlobalRepository
) : ViewModel() {

    fun getSetting() {
        viewModelScope.launch {
            globalRepository.settingLoading = true
            when (val data = withContext(Dispatchers.IO) {
                remoteSettingRepository.getRemoteSetting()
            }) {
                is NetWorkResult.Error<*> -> {
                    Log.v("api", data.message)
                }

                is NetWorkResult.Loading<*> -> {
                    Log.v("api", "loading")
                }

                is NetWorkResult.Success<RemoteSettingResponse> -> {
                    globalRepository.remoteSetting = globalRepository.remoteSetting.copy(imgHost = data.data.img_host)
                }
            }
            globalRepository.settingLoading = false
        }
    }
}