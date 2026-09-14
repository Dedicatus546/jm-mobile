package com.par9uet.jm.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.par9uet.jm.database.dao.LocalComicDao
import com.par9uet.jm.database.model.LocalComic
import com.par9uet.jm.ui.models.CommonUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class LocalComicDetailViewModel @Inject constructor(
    private val localComicDao: LocalComicDao
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
            try {
                val data = withContext(Dispatchers.IO) {
                    localComicDao.getOne(comicId)
                }
                _comicDetailState.update {
                    it.copy(
                        data = data
                    )
                }
            } catch (e: Exception) {
                _comicDetailState.update {
                    it.copy(
                        isError = true,
                        errorMsg = e.message
                    )
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