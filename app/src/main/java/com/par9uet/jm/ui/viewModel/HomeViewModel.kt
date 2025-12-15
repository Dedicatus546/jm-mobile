package com.par9uet.jm.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.par9uet.jm.data.models.HomeComicSwiperItem
import com.par9uet.jm.retrofit.model.HomeSwiperComicListItemResponse
import com.par9uet.jm.retrofit.model.NetWorkResult
import com.par9uet.jm.retrofit.repository.ComicRepository
import com.par9uet.jm.ui.models.CommonUIState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(
    private val comicRepository: ComicRepository
) : ViewModel() {

    private val _state = MutableStateFlow(
        CommonUIState<List<HomeComicSwiperItem>>(
            data = listOf(),
        )
    )
    val state = _state.asStateFlow()

    fun getPromoteComicList() {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true)
            }
            when (val data = withContext(Dispatchers.IO) {
                comicRepository.getHomeSwiperComicList()
            }) {
                is NetWorkResult.Error -> {
                    _state.update {
                        it.copy(isError = true, errorMsg = data.message)
                    }
                }

                is NetWorkResult.Success<List<HomeSwiperComicListItemResponse>> -> {
                    val list = data.data.map { it.toHomeComicSwiperItem() }
                    _state.update {
                        it.copy(data = list)
                    }
                }
            }
            _state.update {
                it.copy(isLoading = false)
            }
        }
    }
}