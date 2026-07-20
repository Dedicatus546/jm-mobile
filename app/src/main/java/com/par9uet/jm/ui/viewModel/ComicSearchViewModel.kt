package com.par9uet.jm.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.par9uet.jm.data.models.ComicSearchOrderFilter
import com.par9uet.jm.repository.ComicRepository
import com.par9uet.jm.retrofit.model.ComicListResponse
import com.par9uet.jm.retrofit.model.NetWorkResult
import com.par9uet.jm.store.HistorySearchManager
import com.par9uet.jm.ui.models.CommonUIState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class ComicSearchViewModel(
    private val comicRepository: ComicRepository,
    private val historySearchManager: HistorySearchManager
) : ViewModel() {

    // 先提前走一次搜索接口来获取数据，做判断
    // 不用在搜索结果页那边判断是否需要重定向到特定详情页了
    // 用一次重复请求来减少逻辑耦合，感觉还行？
    data class ComicSearchResult(
        val type: String, // "redirect" | "page" 重定向或者跳转到列表页
        val redirect: Int?,
        val content: String
    )

    private val _comicSearchResultState = MutableStateFlow<CommonUIState<ComicSearchResult>>(
        CommonUIState()
    )
    val comicSearchResultState = _comicSearchResultState.asStateFlow()

    fun search(
        content: String
    ) {
        viewModelScope.launch {
            _comicSearchResultState.update {
                it.copy(
                    isLoading = true,
                    isError = false,
                    errorMsg = "",
                    data = null
                )
            }
            when (val data =
                comicRepository.getComicList(1, ComicSearchOrderFilter.NEWEST, content)) {
                is NetWorkResult.Error -> {
                    _comicSearchResultState.update {
                        it.copy(isError = true, errorMsg = data.message)
                    }
                }

                is NetWorkResult.Success<ComicListResponse> -> {
                    val r = data.data.redirect_aid?.toInt()
                    _comicSearchResultState.update {
                        it.copy(data = ComicSearchResult(
                            type = if (r != null) "redirect" else "page",
                            redirect = r,
                            content = content,
                        ))
                    }
                }
            }
            _comicSearchResultState.update {
                it.copy(isLoading = false)
            }
        }
    }

    fun addHistoryItem(text: String) {
        viewModelScope.launch {
            delay(1000.milliseconds)
            historySearchManager.addItem(text)
        }
    }
}