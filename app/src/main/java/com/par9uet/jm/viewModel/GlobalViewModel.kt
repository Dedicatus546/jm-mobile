package com.par9uet.jm.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.par9uet.jm.data.models.Setting
import com.par9uet.jm.data.models.User
import com.par9uet.jm.retrofit.model.NetWorkResult
import com.par9uet.jm.retrofit.model.SettingResponse
import com.par9uet.jm.retrofit.repository.GlobalRepository
import com.par9uet.jm.retrofit.repository.SettingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GlobalViewModel(
    private val globalRepository: GlobalRepository
) : ViewModel() {
    data class UserState(val loading: Boolean, val user: User, val isLogin: Boolean)

    val userState
        get() = UserState(
            loading = globalRepository.userLoading,
            user = globalRepository.user,
            isLogin = globalRepository.user.id > 0
        )

    data class SettingState(val loading: Boolean, val setting: Setting)

    val settingState
        get() = SettingState(
            loading = globalRepository.settingLoading,
            setting = globalRepository.setting,
        )
}