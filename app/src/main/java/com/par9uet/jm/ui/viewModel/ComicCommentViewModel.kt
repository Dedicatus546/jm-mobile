package com.par9uet.jm.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.par9uet.jm.repository.ComicRepository
import com.par9uet.jm.retrofit.model.CommentComicResponse
import com.par9uet.jm.retrofit.model.NetWorkResult
import com.par9uet.jm.store.ToastManager
import com.par9uet.jm.ui.models.CommonUIState
import com.par9uet.jm.ui.pagingSource.ComicCommentPagingSource
import com.par9uet.jm.utils.log
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ComicCommentViewModel(
    private val comicRepository: ComicRepository,
    private val toastManager: ToastManager,
) : ViewModel() {
    private val _comicIdState = MutableStateFlow(0)
    private val _isFirstLoading = MutableStateFlow(true)
    val isFirstLoading = _isFirstLoading.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val commentPager = _comicIdState.flatMapLatest { comicId ->
        Pager(
            config = PagingConfig(pageSize = 20, prefetchDistance = 6, initialLoadSize = 20),
            pagingSourceFactory = {
                ComicCommentPagingSource(
                    comicRepository,
                    comicId
                )
            }
        ).flow
    }.cachedIn(viewModelScope)

    fun updateComicId(comicId: Int) {
        _comicIdState.update {
            comicId
        }
    }

    private val _commentComicState = MutableStateFlow(CommonUIState(data = null))
    val commentComicState = _commentComicState.asStateFlow()
    fun comment(
        content: String,
        comicId: Int,
        commentId: Int? = null,
        onSuccess: (() -> Unit)? = null
    ) {
        viewModelScope.launch {
            _commentComicState.update {
                it.copy(
                    isLoading = true,
                    isError = false,
                    errorMsg = ""
                )
            }
            when (val data = comicRepository.comment(content, comicId, commentId)) {
                is NetWorkResult.Error -> {
                    _commentComicState.update {
                        it.copy(
                            isError = true,
                            errorMsg = data.message
                        )
                    }
                }

                is NetWorkResult.Success<CommentComicResponse> -> {
                    log("commentArg $content, $comicId, $commentId")
                    toastManager.showAsync(data.data.msg)
                    if (data.data.status == "ok") {
                        onSuccess?.invoke()
                    }
                }
            }
            _commentComicState.update {
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