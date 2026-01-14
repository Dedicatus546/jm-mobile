package com.par9uet.jm.ui.viewModel

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import com.par9uet.jm.data.models.ComicPicImageState
import com.par9uet.jm.repository.ComicRepository
import com.par9uet.jm.retrofit.model.NetWorkResult
import com.par9uet.jm.store.LocalSettingManager
import com.par9uet.jm.ui.models.CommonUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min

class ComicReadViewModel(
    private val comicRepository: ComicRepository,
    private val picImageLoader: ImageLoader,
    private val localSettingManager: LocalSettingManager,
) : ViewModel() {
    var currentIndexState = mutableStateOf(0)
    private val _comicPicState = MutableStateFlow(
        CommonUIState<List<ComicPicImageState>>(
            isLoading = true
        )
    )
    val comicPicState = _comicPicState.asStateFlow()

    val size: Int get() = _comicPicState.value.data?.size ?: 0

    private val prefetchSet = mutableSetOf<Int>()

    fun getComicPicList(comicId: Int, shunt: String) {
        viewModelScope.launch {
            _comicPicState.update {
                it.copy(
                    isLoading = true,
                    isError = false,
                    errorMsg = ""
                )
            }
            when (val data = comicRepository.getComicPicList(comicId, shunt)) {
                is NetWorkResult.Error -> {
                    _comicPicState.update {
                        it.copy(
                            isError = true,
                            errorMsg = data.message
                        )
                    }
                }

                is NetWorkResult.Success<List<String>> -> {
                    _comicPicState.update {
                        it.copy(
                            data = data.data.mapIndexed { index, item ->
                                ComicPicImageState(
                                    index,
                                    comicId,
                                    item,
                                    picImageLoader,
                                )
                            }
                        )
                    }
                }
            }
            _comicPicState.update {
                it.copy(
                    isLoading = false
                )
            }
        }
    }

    fun changeIndex(context: Context) {
        if (currentIndexState.value >= size) {
            return
        }
        decodeIndex(currentIndexState.value, context)
    }

    private fun decodeIndex(index: Int, context: Context) {
        val count = localSettingManager.localSettingState.value.prefetchCount
        val start = max(0, index - count)
        val end = min(size - 1, index + count)
        decode(index, context) {
            for (i in index + 1..end) {
                decode(i, context)
            }
            for (i in index - 1 downTo start) {
                decode(i, context)
            }
        }
    }

    fun prevIndex(context: Context) {
        val index = max(0, currentIndexState.value - 1)
        currentIndexState.value = index
        decodeIndex(index, context)
    }

    fun nextIndex(context: Context) {
        val index = min(size - 1, currentIndexState.value + 1)
        currentIndexState.value = index
        decodeIndex(index, context)
    }

    private fun decode(index: Int, context: Context, onComplete: (() -> Unit)? = null) {
        val comicPicImageState = comicPicState.value.data?.getOrNull(index) ?: return
        if (prefetchSet.contains(index)) {
            onComplete?.invoke()
            return
        }
        viewModelScope.launch {
            comicPicImageState.decode(context)
            onComplete?.invoke()
        }
        prefetchSet.add(index)
    }
}