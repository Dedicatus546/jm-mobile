package com.par9uet.jm.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.par9uet.jm.data.models.Comic
import com.par9uet.jm.repository.ComicRepository
import com.par9uet.jm.repository.LocalComicRepository
import com.par9uet.jm.retrofit.model.CollectComicResponse
import com.par9uet.jm.retrofit.model.ComicDetailResponse
import com.par9uet.jm.retrofit.model.LikeComicResponse
import com.par9uet.jm.retrofit.model.NetworkResult
import com.par9uet.jm.store.ToastManager
import com.par9uet.jm.ui.models.CommonUIState
import com.par9uet.jm.utils.getOrThrow
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ComicChapterViewModel @Inject constructor(
    private val localComicRepository: LocalComicRepository,
    private val comicRepository: ComicRepository,
    private val toastManager: ToastManager,
) : ViewModel() {
    val downloadComicId = MutableStateFlow(0)

    @OptIn(ExperimentalCoroutinesApi::class)
    val localComic = downloadComicId.flatMapLatest {
        localComicRepository.getNullableLocalComicFlow(it).getOrThrow()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    fun loadDownloadComic(id: Int) {
        downloadComicId.update {
            id
        }
    }

    private val _comicDetailState = MutableStateFlow<CommonUIState<Comic>>(
        CommonUIState(
            isLoading = true,
        )
    )
    val comicDetailState = _comicDetailState.asStateFlow()

    fun getComicDetail(id: Int) {
        viewModelScope.launch {
            _comicDetailState.update {
                it.copy(
                    isLoading = true,
                    isError = false,
                    errorMsg = "",
                    data = null
                )
            }
            when (val data = comicRepository.getComicDetail(id)) {
                is NetworkResult.Error -> {
                    _comicDetailState.update {
                        it.copy(
                            isError = true,
                            errorMsg = data.message
                        )
                    }
                }

                is NetworkResult.Success<ComicDetailResponse> -> {
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
                is NetworkResult.Error -> {
                    _likeComicState.update {
                        it.copy(
                            isError = true,
                            errorMsg = data.message
                        )
                    }
                }

                is NetworkResult.Success<LikeComicResponse> -> {
                    toastManager.show("喜欢成功")
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
                is NetworkResult.Error -> {
                    _collectComicState.update {
                        it.copy(
                            isError = true,
                            errorMsg = data.message
                        )
                    }
                }

                is NetworkResult.Success<CollectComicResponse> -> {
                    toastManager.show("收藏成功")
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
                is NetworkResult.Error -> {
                    _collectComicState.update {
                        it.copy(
                            isError = true,
                            errorMsg = data.message
                        )
                    }
                }

                is NetworkResult.Success<CollectComicResponse> -> {
                    toastManager.show("取消收藏成功")
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

    fun resetLikeComicState() {
        _likeComicState.update {
            CommonUIState(data = null)
        }
    }

    fun resetCollectComicState() {
        _collectComicState.update {
            CommonUIState(data = null)
        }
    }
}