package com.par9uet.jm.viewModel

import androidx.lifecycle.ViewModel
import com.par9uet.jm.data.models.RemoteSetting
import com.par9uet.jm.data.models.User
import com.par9uet.jm.retrofit.repository.GlobalRepository

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

    data class SettingState(val loading: Boolean, val remoteSetting: RemoteSetting)

    val settingState
        get() = SettingState(
            loading = globalRepository.settingLoading,
            remoteSetting = globalRepository.remoteSetting,
        )
}