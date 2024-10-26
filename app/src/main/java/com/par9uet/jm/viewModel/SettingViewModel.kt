package com.par9uet.jm.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.par9uet.jm.http.getSettingApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class SettingInfo(
//    val logoPath: String,
//    val webHost: String,
    val imgHost: String,
//    val baseUrl: String,
//    val cnBaseUrl: String,
//    val version: String,
)

class SettingViewModel : ViewModel() {
    var settingInfo by mutableStateOf(SettingInfo(imgHost = ""))
    var loading by mutableStateOf(false)

    fun getSetting() {
        viewModelScope.launch {
            loading = true
            val data = withContext(Dispatchers.IO) {
                getSettingApi()
            }
            settingInfo = SettingInfo(
                data.data.img_host
            )
            loading = false
        }
    }
}