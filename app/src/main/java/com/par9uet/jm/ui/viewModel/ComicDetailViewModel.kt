package com.par9uet.jm.ui.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.par9uet.jm.data.models.Comic
import com.par9uet.jm.repository.ComicRepository
import com.par9uet.jm.retrofit.model.CollectComicResponse
import com.par9uet.jm.retrofit.model.ComicDetailResponse
import com.par9uet.jm.retrofit.model.LikeComicResponse
import com.par9uet.jm.retrofit.model.NetWorkResult
import com.par9uet.jm.ui.models.BaseUIState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ComicDetailViewModel(
    private val comicRepository: ComicRepository
) : ViewModel() {
    var comicDetailBaseUIState by mutableStateOf<BaseUIState<Comic>>(BaseUIState())
    var likeComicBaseUIState by mutableStateOf<BaseUIState<Unit>>(
        BaseUIState(

        )
    )
    var collectComicBaseUIState by mutableStateOf<BaseUIState<Unit>>(BaseUIState())

    fun getComicDetail(id: Int) {
        viewModelScope.launch {
            comicDetailBaseUIState = comicDetailBaseUIState.startLoading()
            comicDetailBaseUIState = when (val data = withContext(Dispatchers.IO) {
                comicRepository.getComicDetail(id)
            }) {
                is NetWorkResult.Error -> {
                    comicDetailBaseUIState.setError(data.message)
                }

                is NetWorkResult.Success<ComicDetailResponse> -> {
                    comicDetailBaseUIState.setData(data.data.toComic())
                }
            }
        }
    }

    fun likeComic(id: Int) {
        viewModelScope.launch {
            likeComicBaseUIState = likeComicBaseUIState.startLoading()
            when (val data = withContext(Dispatchers.IO) {
                comicRepository.likeComic(id)
            }) {
                is NetWorkResult.Error -> {
                    likeComicBaseUIState = likeComicBaseUIState.setError(data.message)
                }

                is NetWorkResult.Success<LikeComicResponse> -> {
                    comicDetailBaseUIState = comicDetailBaseUIState.setData(
                        comicDetailBaseUIState.data?.copy(
                            isLike = true
                        )
                    )
                }
            }
        }
    }

    fun collect(id: Int) {
        viewModelScope.launch {
            collectComicBaseUIState = collectComicBaseUIState.startLoading()
            when (val data = withContext(Dispatchers.IO) {
                comicRepository.collectComic(id)
            }) {
                is NetWorkResult.Error -> {
                    collectComicBaseUIState = collectComicBaseUIState.setError(data.message)
                }

                is NetWorkResult.Success<CollectComicResponse> -> {
                    comicDetailBaseUIState = comicDetailBaseUIState.setData(
                        comicDetailBaseUIState.data?.copy(
                            isCollect = true
                        )
                    )
                }
            }
        }
    }

    fun unCollect(id: Int) {
        viewModelScope.launch {
            collectComicBaseUIState = collectComicBaseUIState.startLoading()
            when (val data = withContext(Dispatchers.IO) {
                comicRepository.collectComic(id)
            }) {
                is NetWorkResult.Error -> {
                    collectComicBaseUIState = collectComicBaseUIState.setError(data.message)
                }

                is NetWorkResult.Success<CollectComicResponse> -> {
                    collectComicBaseUIState = collectComicBaseUIState.startLoading()
                    when (val data = withContext(Dispatchers.IO) {
                        comicRepository.collectComic(id)
                    }) {
                        is NetWorkResult.Error -> {
                            collectComicBaseUIState = collectComicBaseUIState.setError(data.message)
                        }

                        is NetWorkResult.Success<CollectComicResponse> -> {
                            comicDetailBaseUIState = comicDetailBaseUIState.setData(
                                comicDetailBaseUIState.data?.copy(
                                    isCollect = false
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}