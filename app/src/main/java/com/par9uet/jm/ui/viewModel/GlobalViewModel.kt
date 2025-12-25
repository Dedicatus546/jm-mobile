package com.par9uet.jm.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.par9uet.jm.task.AppInitTask
import com.par9uet.jm.ui.models.CommonUIState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GlobalViewModel(
    private val appInitTaskList: List<AppInitTask>
) : ViewModel() {

    private val _state = MutableStateFlow(CommonUIState(
        isLoading = true,
        data = null
    ))
    val state = _state.asStateFlow()

    fun init() {
        viewModelScope.launch(Dispatchers.IO) {
            _state.update {
                it.copy(
                    isLoading = true,
                    isError = false,
                    errorMsg = "",
                )
            }
            appInitTaskList.sortedBy { it.getAppTaskInfo().sort }.forEach { it.init() }
            if (appInitTaskList.any { it.getAppTaskInfo().isError }) {
                _state.update {
                    it.copy(
                        isError = true,
                        errorMsg = appInitTaskList.first { item -> item.getAppTaskInfo().isError }
                            .getAppTaskInfo().errorMsg
                    )
                }
            }
            _state.update {
                it.copy(
                    isLoading = false
                )
            }
        }
    }
}