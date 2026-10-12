package com.par9uet.jm.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.par9uet.jm.store.InitManager
import com.par9uet.jm.task.AppInitTask
import com.par9uet.jm.utils.log
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class GlobalViewModel @Inject constructor(
    private val appInitTaskList: List<@JvmSuppressWildcards AppInitTask>,
    private val initManager: InitManager,
) : ViewModel() {

    fun init() {
        viewModelScope.launch {
            appInitTaskList.sortedBy { it.getAppTaskInfo().sort }.forEach { it.init() }
            if (appInitTaskList.any { it.getAppTaskInfo().isError }) {
                // TODO fix
            }
            initManager.deferred.complete("")
            log("完成全局初始化")
        }
    }
}