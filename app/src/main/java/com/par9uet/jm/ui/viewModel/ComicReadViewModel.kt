package com.par9uet.jm.ui.viewModel

import android.content.Context
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import com.par9uet.jm.data.models.ComicPicImageState
import com.par9uet.jm.repository.ComicRepository
import com.par9uet.jm.retrofit.model.ComicPicListResponse
import com.par9uet.jm.retrofit.model.NetWorkResult
import com.par9uet.jm.store.LocalSettingManager
import com.par9uet.jm.ui.models.CommonUIState
import com.par9uet.jm.utils.log
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min
import kotlin.time.Duration.Companion.milliseconds

class ComicReadViewModel(
    private val comicRepository: ComicRepository,
    private val picImageLoader: ImageLoader,
    private val localSettingManager: LocalSettingManager,
) : ViewModel() {
    var isShowToolBar = mutableStateOf(false)
    private var hideToolBarJob: Job? = null
    var currentIndexState = mutableIntStateOf(0)
    private val _comicPicState = MutableStateFlow(
        CommonUIState<List<ComicPicImageState>>(
            isLoading = true
        )
    )
    val comicPicState = _comicPicState.asStateFlow()
    val sizeState = comicPicState.map { it.data?.size ?: 0 }.stateIn(
        scope = viewModelScope, // 绑定的协程作用域
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )
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
                is NetWorkResult.Error -> {
                    _comicPicState.update {
                        it.copy(
                            isError = true,
                            errorMsg = data.message
                        )
                    }
                }

                is NetWorkResult.Success<ComicPicListResponse> -> {
                    _comicPicState.update {
                        it.copy(
                            data = data.data.list.mapIndexed { index, item ->
                                ComicPicImageState(
                                    index,
                                    comicId,
                                    item,
                                    data.data.__scrambleId,
                                    data.data.__speed,
                                    picImageLoader,
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

    fun prev(context: Context, needHideToolBar: Boolean = true) {
        if (needHideToolBar) {
            hideToolBar()
        } else {
            startAutoHideToolBar()
        }
        val index = max(0, currentIndexState.intValue - 1)
        currentIndexState.intValue = index
        decodeIndex(index, context)
    }

    fun next(context: Context, needHideToolBar: Boolean = true) {
        if (needHideToolBar) {
            hideToolBar()
        } else {
            startAutoHideToolBar()
        }
        val index = min(sizeState.value - 1, currentIndexState.intValue + 1)
        currentIndexState.intValue = index
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

    fun triggerToolBar() {
        isShowToolBar.value = !isShowToolBar.value
        if (isShowToolBar.value) {
            startAutoHideToolBar()
        }
    }

    fun hideToolBar() {
        isShowToolBar.value = false
    }

    fun showToolBar() {
        isShowToolBar.value = true
        startAutoHideToolBar()
    }

    fun clearAutoHideToolBarJob() {
        if (hideToolBarJob != null) {
            hideToolBarJob!!.cancel()
            hideToolBarJob = null
        }
    }

    // 等待一段时间后自动隐藏底部进度条
    fun startAutoHideToolBar() {
        clearAutoHideToolBarJob()
        hideToolBarJob = viewModelScope.launch {
            delay(3000.milliseconds)
            isShowToolBar.value = false
            hideToolBarJob = null
        }
    }
}