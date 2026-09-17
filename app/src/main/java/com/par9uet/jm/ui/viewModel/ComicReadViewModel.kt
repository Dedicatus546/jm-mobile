package com.par9uet.jm.ui.viewModel

import android.content.Context
import androidx.lifecycle.viewModelScope
import coil3.ImageLoader
import com.par9uet.jm.ui.screens.readScreen.ComicPicImage
import com.par9uet.jm.repository.ComicRepository
import com.par9uet.jm.retrofit.model.ComicPicListResponse
import com.par9uet.jm.retrofit.model.NetworkResult
import com.par9uet.jm.store.LocalSettingManager
import com.par9uet.jm.ui.screens.readScreen.ComicPicImageFactory
import com.par9uet.jm.utils.log
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min

@HiltViewModel
class ComicReadViewModel @Inject constructor(
    private val comicRepository: ComicRepository,
    private val localSettingManager: LocalSettingManager,
    private val comicPicImageFactory: ComicPicImageFactory,
) : BaseComicReadViewModel() {
    private val _refreshComicPicTrigger = MutableSharedFlow<Unit>()
    val refreshComicPicTrigger: SharedFlow<Unit> = _refreshComicPicTrigger.asSharedFlow()
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
                is NetworkResult.Error -> {
                    _comicPicState.update {
                        it.copy(
                            isError = true,
                            errorMsg = data.message
                        )
                    }
                }

                is NetworkResult.Success<ComicPicListResponse> -> {
                    _comicPicState.update {
                        it.copy(
                            data = data.data.list.map { item ->
                                comicPicImageFactory.create(
                                    comicId,
                                    item,
                                    data.data.__scrambleId,
                                    data.data.__speed,
                                )
                            }
                        )
                    }
                    _refreshComicPicTrigger.emit(Unit)
                }
            }
            _comicPicState.update {
                it.copy(
                    isLoading = false
                )
            }
        }
    }

    fun resetDecodePrefetchCache() {
        prefetchSet.clear()
    }

    fun decodeIndex(index: Int, context: Context) {
        log("decode index $index")
        val count = localSettingManager.localSettingState.value.prefetchCount
        val start = max(0, index - count)
        val end = min(sizeState.value - 1, index + count)
        decode(index, context) {
            for (i in index + 1..end) {
                log("pre decode index $i")
                decode(i, context)
            }
            for (i in index - 1 downTo start) {
                log("pre decode index $i")
                decode(i, context)
            }
        }
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