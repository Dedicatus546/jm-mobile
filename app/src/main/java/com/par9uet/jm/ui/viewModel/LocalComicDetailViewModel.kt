package com.par9uet.jm.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.par9uet.jm.data.models.DbResult
import com.par9uet.jm.database.model.LocalComic
import com.par9uet.jm.repository.LocalComicRepository
import com.par9uet.jm.ui.models.CommonUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class LocalComicDetailViewModel @Inject constructor(
    private val localComicRepository: LocalComicRepository
) : ViewModel() {
    private val _comicDetailState = MutableStateFlow<CommonUIState<LocalComic>>(
        CommonUIState(
            isLoading = true,
        )
    )
    val comicDetailState = _comicDetailState.asStateFlow()

    private val _isFirstLoading = MutableStateFlow(true)
    val isFirstLoading = _isFirstLoading.asStateFlow()

    fun getComicDetail(comicId: Int) {
        viewModelScope.launch {
            _comicDetailState.update {
                it.copy(
                    isLoading = true,
                    isError = false,
                    errorMsg = "",
                )
            }
            when (val data = localComicRepository.getLocalComic(comicId)) {
                is DbResult.Error -> {
                    _comicDetailState.update {
                        it.copy(
                            isError = true,
                            errorMsg = data.message
                        )
                    }
                }

                is DbResult.Success<LocalComic> -> {
                    _comicDetailState.update {
                        it.copy(
                            data = data.data
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

    fun updateIsFirstLoading(ifl: Boolean) {
        _isFirstLoading.update {
            ifl
        }
    }
}