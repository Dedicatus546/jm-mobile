package com.par9uet.jm.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.par9uet.jm.data.models.Comic
import com.par9uet.jm.repository.ComicRepository
import com.par9uet.jm.retrofit.model.CollectComicResponse
import com.par9uet.jm.retrofit.model.ComicDetailResponse
import com.par9uet.jm.retrofit.model.LikeComicResponse
import com.par9uet.jm.retrofit.model.NetWorkResult
import com.par9uet.jm.ui.models.BaseUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ComicDetailViewModel(
    private val comicRepository: ComicRepository
) : ViewModel() {
    private val _comicDetailState = MutableStateFlow(BaseUIState<Comic>())
    val comicDetailState = _comicDetailState.asStateFlow()

    fun getComicDetail(id: Int) {
        viewModelScope.launch {
            _comicDetailState.update {
                it.copy(
                    isLoading = true,
                    isError = false,
                    errMsg = ""
                )
            }
            when (val data = comicRepository.getComicDetail(id)) {
                is NetWorkResult.Error -> {
                    _comicDetailState.update {
                        it.copy(
                            isError = true,
                            errMsg = data.message
                        )
                    }
                }

                is NetWorkResult.Success<ComicDetailResponse> -> {
                    _comicDetailState.update {
                        it.copy(
                            data = data.data.toComic()
                        )
                    }
                }
            }
            _comicDetailState.update {
                it.copy(
                    isLoading = false
                )
            }
        }
    }

    private val _likeComicState = MutableStateFlow(BaseUIState(data = null))
    val likeComicState = _likeComicState.asStateFlow()
    fun likeComic(id: Int) {
        viewModelScope.launch {
            _likeComicState.update {
                it.copy(
                    isLoading = true,
                    isError = false,
                    errMsg = ""
                )
            }
            when (val data = comicRepository.likeComic(id)) {
                is NetWorkResult.Error -> {
                    _likeComicState.update {
                        it.copy(
                            isError = true,
                            errMsg = data.message
                        )
                    }
                }

                is NetWorkResult.Success<LikeComicResponse> -> {
                }
            }
            _likeComicState.update {
                it.copy(
                    isLoading = false,
                )
            }
        }
    }

    private val _collectComicState = MutableStateFlow(BaseUIState(data = null))
    val collectComicState = _collectComicState.asStateFlow()
    fun collect(id: Int) {
        viewModelScope.launch {
            _collectComicState.update {
                it.copy(
                    isLoading = true,
                    isError = false,
                    errMsg = ""
                )
            }
            when (val data = comicRepository.collectComic(id)) {
                is NetWorkResult.Error -> {
                    _collectComicState.update {
                        it.copy(
                            isError = true,
                            errMsg = data.message
                        )
                    }
                }

                is NetWorkResult.Success<CollectComicResponse> -> {
                }
            }
            _collectComicState.update {
                it.copy(
                    isLoading = false,
                )
            }
        }
    }

    fun unCollect(id: Int) {
        viewModelScope.launch {
            _collectComicState.update {
                it.copy(
                    isLoading = true,
                    isError = false,
                    errMsg = ""
                )
            }
            when (val data = comicRepository.unCollectComic(id)) {
                is NetWorkResult.Error -> {
                    _collectComicState.update {
                        it.copy(
                            isError = true,
                            errMsg = data.message
                        )
                    }
                }

                is NetWorkResult.Success<CollectComicResponse> -> {
                }
            }
            _collectComicState.update {
                it.copy(
                    isLoading = false,
                )
            }
        }
    }
}