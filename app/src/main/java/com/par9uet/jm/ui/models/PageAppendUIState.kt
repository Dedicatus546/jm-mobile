package com.par9uet.jm.ui.models

data class PageAppendUIState<T>(
    private var isLoading: Boolean = false,
    var page: Int = 1,
    var pageSize: Int = 20,
    var total: Int = 0,
    val list: MutableList<T> = mutableListOf(),
    var isError: Boolean = false,
    var errMsg: String = ""
) {

    // 第一次获取数据
    val isInitializing: Boolean get() = list.isEmpty() && isLoading

    // 已有数据，刷新页面
    val isRefreshing: Boolean get() = list.isNotEmpty() && isLoading && page == 1
    val hasData: Boolean get() = list.isNotEmpty()
    val hasMore: Boolean get() = list.size < total

    fun append(appendList: List<T>): PageAppendUIState<T> {
        list.addAll(appendList)
        return copy(
            list = list,
            isLoading = false,
            isError = false,
            total = total + appendList.size,
        )
    }

    fun startLoading(): PageAppendUIState<T> = copy(
        isLoading = true,
        isError = false,
        errMsg = ""
    )

    fun setError(msg: String): PageAppendUIState<T> = copy(
        isError = true,
        errMsg = msg
    )

    fun nextPage(): PageAppendUIState<T> = setPage(page + 1)

    fun setPage(page: Int): PageAppendUIState<T> = copy(
        page = page
    )
}