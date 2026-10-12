package com.par9uet.jm.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.par9uet.jm.repository.ProxyApiRepository
import com.par9uet.jm.retrofit.model.CommentComicResponse
import com.par9uet.jm.retrofit.model.NetworkResult
import com.par9uet.jm.store.LocalSettingManager
import com.par9uet.jm.store.ToastManager
import com.par9uet.jm.ui.models.CommonUIState
import com.par9uet.jm.utils.log
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ApiSelectViewModel @Inject constructor(
    private val proxyApiRepository: ProxyApiRepository,
    private val toastManager: ToastManager,
    private val localSettingManager: LocalSettingManager
) : ViewModel() {

    private val _pullApiListState = MutableStateFlow(CommonUIState(data = Unit))
    val pullApiListState = _pullApiListState.asStateFlow()

    fun pullApiList() {
        viewModelScope.launch {
            _pullApiListState.update {
                it.copy(
                    isLoading = true,
                    isError = false,
                    errorMsg = ""
                )
            }

            when (val data = proxyApiRepository.getApiList()) {
                is NetworkResult.Error -> {
                    _pullApiListState.update {
                        it.copy(
                            isError = true,
                            errorMsg = data.message
                        )
                    }
                }

                is NetworkResult.Success<List<String>> -> {
                    data.data.forEach {
                        localSettingManager.addApi(it)
                    }
                    toastManager.show("拉取成功")
                }
            }

            _pullApiListState.update {
                it.copy(
                    isLoading = false
                )
            }
        }
    }
}