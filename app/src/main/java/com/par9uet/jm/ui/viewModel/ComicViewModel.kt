package com.par9uet.jm.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.par9uet.jm.data.models.Comic
import com.par9uet.jm.data.models.ComicFilterOrder
import com.par9uet.jm.data.models.HomeComicSwiperItem
import com.par9uet.jm.repository.ComicRepository
import com.par9uet.jm.retrofit.model.ComicListResponse
import com.par9uet.jm.retrofit.model.HomeSwiperComicListItemResponse
import com.par9uet.jm.retrofit.model.NetWorkResult
import com.par9uet.jm.ui.models.AppendListUIState
import com.par9uet.jm.ui.models.ListUIState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ComicViewModel(
    private val comicRepository: ComicRepository
) : ViewModel() {
    private val _promoteComicState = MutableStateFlow(ListUIState<HomeComicSwiperItem>())
    val promoteComicState = _promoteComicState.asStateFlow()
    fun getPromoteComicList() {
        viewModelScope.launch {
            _promoteComicState.update {
                it.copy(isLoading = true)
            }
            when (val data = withContext(Dispatchers.IO) {
                comicRepository.getHomeSwiperComicList()
            }) {
                is NetWorkResult.Error -> {
                    _promoteComicState.update {
                        it.copy(isError = true, errorMsg = data.message)
                    }
                }

                is NetWorkResult.Success<List<HomeSwiperComicListItemResponse>> -> {
                    _promoteComicState.update {
                        it.copy(list = data.data.map { item -> item.toHomeComicSwiperItem() })
                    }
                }
            }
            _promoteComicState.update {
                it.copy(isLoading = false)
            }
        }
    }

    private val _comicQuickSearchState = MutableStateFlow(AppendListUIState<Comic>())
    val comicQuickSearchState = _comicQuickSearchState.asStateFlow()
    fun getComicQuickSearchList(type: String, content: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _comicQuickSearchState.update {
                (if (type == "refresh") it.copy(
                    isRefreshing = true,
                    page = 1
                ) else it.copy(
                    isMoreLoading = true,
                    page = it.page + 1,
                )).copy(
                    isError = false,
                    errorMsg = ""
                )
            }
            when (val data = comicRepository.getComicList(
                _comicQuickSearchState.value.page,
                ComicFilterOrder.MR.value,
                content

            )) {
                is NetWorkResult.Error -> {
                    _comicQuickSearchState.update {
                        it.copy(
                            isError = true,
                            errorMsg = data.message
                        )
                    }
                }

                is NetWorkResult.Success<ComicListResponse> -> {
                    _comicQuickSearchState.update {
                        it.copy(
                            list = data.data.toComicList(),
                            total = data.data.total.toInt()
                        )
                    }
                }
            }
            _comicQuickSearchState.update {
                if (type == "refresh") it.copy(
                    isRefreshing = false,
                    page = 1
                ) else it.copy(
                    isMoreLoading = false,
                    page = it.page - 1,
                )
            }
        }
    }
}