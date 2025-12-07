package com.par9uet.jm.ui.models

data class BaseUIState<T>(
    private var isLoading: Boolean = false,
    var data: T? = null,
    var isError: Boolean = false,
    var errMsg: String = ""
) {
    // 第一次获取数据
    val isInitializing: Boolean
        get() = data == null && isLoading

    // 已有数据，刷新页面
    val isRefreshing: Boolean
        get() = data != null && isLoading
    val hasData: Boolean
        get() = data != null

    fun setData(data: T?) = copy(
        isLoading = false,
        isError = false,
        errMsg = "",
        data = data
    )

    fun startLoading(): BaseUIState<T> = copy(
        isLoading = true,
        isError = false,
        errMsg = ""
    )

    fun setError(errMsg: String): BaseUIState<T> = copy(
        isError = true,
        errMsg = errMsg
    )
}