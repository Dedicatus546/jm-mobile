package com.par9uet.jm.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.par9uet.jm.data.models.HomeComicSwiperItem
import com.par9uet.jm.repository.ComicRepository
import com.par9uet.jm.retrofit.model.HomeSwiperComicListItemResponse
import com.par9uet.jm.retrofit.model.NetWorkResult
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


    fun getComicQuickSearchList() {

    }
}