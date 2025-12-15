package com.par9uet.jm.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.par9uet.jm.data.models.RemoteSetting
import com.par9uet.jm.retrofit.model.NetWorkResult
import com.par9uet.jm.retrofit.model.RemoteSettingResponse
import com.par9uet.jm.retrofit.repository.RemoteSettingRepository
import com.par9uet.jm.ui.models.CommonUIState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RemoteSettingViewModel(
    private val remoteSettingRepository: RemoteSettingRepository
) : ViewModel() {
    private val _state = MutableStateFlow(
        CommonUIState(
            data = RemoteSetting(
                imgHost = ""
            ),
        )
    )
    val state = _state.asStateFlow()

    fun getRemoteSetting() {
        viewModelScope.launch(Dispatchers.IO) {
            _state.update {
                it.copy(isLoading = true)
            }
            when (val data = remoteSettingRepository.getRemoteSetting()) {
                is NetWorkResult.Error -> {
                    _state.update {
                        it.copy(
                            isError = true,
                            errorMsg = data.message
                        )
                    }
                }

                is NetWorkResult.Success<RemoteSettingResponse> -> {
                    _state.update {
                        it.copy(
                            data = RemoteSetting(
                                imgHost = data.data.img_host
                            )
                        )
                    }
                }
            }
            _state.update {
                it.copy(isLoading = false)
            }
        }
    }
}