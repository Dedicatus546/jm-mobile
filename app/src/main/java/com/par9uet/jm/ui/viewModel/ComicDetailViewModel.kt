package com.par9uet.jm.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.par9uet.jm.data.models.Comic
import com.par9uet.jm.database.dao.DownloadComicDao
import com.par9uet.jm.repository.ComicRepository
import com.par9uet.jm.retrofit.model.CollectComicResponse
import com.par9uet.jm.retrofit.model.ComicDetailResponse
import com.par9uet.jm.retrofit.model.LikeComicResponse
import com.par9uet.jm.retrofit.model.NetWorkResult
import com.par9uet.jm.store.RemoteSettingManager
import com.par9uet.jm.store.ToastManager
import com.par9uet.jm.ui.models.CommonUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ComicDetailViewModel(
    private val comicRepository: ComicRepository,
    private val toastManager: ToastManager,
    private val downloadComicDao: DownloadComicDao,
    private val remoteSettingManager: RemoteSettingManager,
) : ViewModel() {
    private val _comicDetailState = MutableStateFlow<CommonUIState<Comic>>(
        CommonUIState(
            isLoading = true,
        )
    )
    val comicDetailState = _comicDetailState.asStateFlow()
    private val _isFirstLoading = MutableStateFlow(true)
    val isFirstLoading = _isFirstLoading.asStateFlow()

    fun getComicDetail(id: Int) {
        viewModelScope.launch {
            _comicDetailState.update {
                it.copy(
                    isLoading = true,
                    isError = false,
                    errorMsg = "",
                )
            }
            when (val data = comicRepository.getComicDetail(id)) {
                is NetWorkResult.Error -> {
                    _comicDetailState.update {
                        it.copy(
                            isError = true,
                            errorMsg = data.message
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

    private val _likeComicState = MutableStateFlow(CommonUIState(data = null))
    val likeComicState = _likeComicState.asStateFlow()
    fun likeComic(id: Int) {
        viewModelScope.launch {
            _likeComicState.update {
                it.copy(
                    isLoading = true,
                    isError = false,
                    errorMsg = ""
                )
            }
            when (val data = comicRepository.likeComic(id)) {
                is NetWorkResult.Error -> {
                    _likeComicState.update {
                        it.copy(
                            isError = true,
                            errorMsg = data.message
                        )
                    }
                }

                is NetWorkResult.Success<LikeComicResponse> -> {
                    toastManager.showAsync("喜欢成功")
                    if (_comicDetailState.value.data != null) {
                        _comicDetailState.update {
                            it.copy(
                                data = it.data!!.copy(
                                    isLike = true,
                                    likeCount = it.data.likeCount + 1
                                )
                            )
                        }
                    }
                }
            }
            _likeComicState.update {
                it.copy(
                    isLoading = false,
                )
            }
        }
    }

    private val _collectComicState = MutableStateFlow(CommonUIState(data = null))
    val collectComicState = _collectComicState.asStateFlow()
    fun collect(id: Int) {
        viewModelScope.launch {
            _collectComicState.update {
                it.copy(
                    isLoading = true,
                    isError = false,
                    errorMsg = ""
                )
            }
            when (val data = comicRepository.collectComic(id)) {
                is NetWorkResult.Error -> {
                    _collectComicState.update {
                        it.copy(
                            isError = true,
                            errorMsg = data.message
                        )
                    }
                }

                is NetWorkResult.Success<CollectComicResponse> -> {
                    toastManager.showAsync("收藏成功")
                    if (_comicDetailState.value.data != null) {
                        _comicDetailState.update {
                            it.copy(
                                data = it.data!!.copy(
                                    isCollect = true,
                                )
                            )
                        }
                    }
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
                    errorMsg = ""
                )
            }
            when (val data = comicRepository.unCollectComic(id)) {
                is NetWorkResult.Error -> {
                    _collectComicState.update {
                        it.copy(
                            isError = true,
                            errorMsg = data.message
                        )
                    }
                }

                is NetWorkResult.Success<CollectComicResponse> -> {
                    toastManager.showAsync("取消收藏成功")
                    if (_comicDetailState.value.data != null) {
                        _comicDetailState.update {
                            it.copy(
                                data = it.data!!.copy(
                                    isCollect = false,
                                )
                            )
                        }
                    }
                }
            }
            _collectComicState.update {
                it.copy(
                    isLoading = false,
                )
            }
        }
    }

    fun updateIsFirstLoading(ifl: Boolean) {
        _isFirstLoading.update {
            ifl
        }
    }
}