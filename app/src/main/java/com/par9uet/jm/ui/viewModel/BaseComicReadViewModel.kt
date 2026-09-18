package com.par9uet.jm.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.par9uet.jm.store.LocalSettingManager
import com.par9uet.jm.ui.models.CommonUIState
import com.par9uet.jm.data.models.BaseComicPicImage
import com.par9uet.jm.utils.log
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min
import kotlin.time.Duration.Companion.milliseconds

abstract class BaseComicReadViewModel(
    private val localSettingManager: LocalSettingManager
) : ViewModel() {
    private val _isShowToolBar = MutableStateFlow(false)
    val isShowToolBar = _isShowToolBar.asStateFlow()

    private val _currentIndex = MutableStateFlow(0)
    val currentIndex = _currentIndex.asStateFlow()

    protected val _comicPicState = MutableStateFlow(
        CommonUIState<List<BaseComicPicImage>>(
            isLoading = true
        )
    )
    val comicPicState = _comicPicState.asStateFlow()
    val sizeState = comicPicState.map { it.data?.size ?: 0 }.stateIn(
        scope = viewModelScope, // 绑定的协程作用域
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )
    private var hideToolBarJob: Job? = null

    protected val prefetchDeferredMap = mutableMapOf<Int, Deferred<Unit>>()

    fun prev(needHideToolBar: Boolean = true) {
        if (needHideToolBar) {
            hideToolBar()
        } else {
            startAutoHideToolBar()
        }
        val index = max(0, _currentIndex.value - 1)
        _currentIndex.update {
            index
        }
    }

    fun next(needHideToolBar: Boolean = true) {
        if (needHideToolBar) {
            hideToolBar()
        } else {
            startAutoHideToolBar()
        }
        val index = min(sizeState.value - 1, _currentIndex.value + 1)
        _currentIndex.update {
            index
        }
    }

    fun triggerToolBar() {
        _isShowToolBar.update {
            !isShowToolBar.value
        }
        if (isShowToolBar.value) {
            startAutoHideToolBar()
        }
    }

    fun hideToolBar() {
        _isShowToolBar.update {
            false
        }
    }

    fun showToolBar() {
        _isShowToolBar.update {
            true
        }
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
            _isShowToolBar.update {
                false
            }
            hideToolBarJob = null
        }
    }

    fun updateCurrentIndex(index: Int) {
        _currentIndex.update {
            index
        }
        decodeIndex(index)
    }

    fun decodeIndex(index: Int) {
        log("decode index $index")
        val count = localSettingManager.localSettingState.value.prefetchCount
        val start = max(0, index - count)
        val end = min(sizeState.value - 1, index + count)
        viewModelScope.launch {
            decode(index) {
                for (i in index + 1..end) {
                    viewModelScope.launch {
                        log("pre decode index $i")
                        decode(i)
                    }
                }
                for (i in index - 1 downTo start) {
                    viewModelScope.launch {
                        log("pre decode index $i")
                        decode(i)
                    }
                }
            }
        }
    }

    private suspend fun decode(index: Int, onComplete: (() -> Unit)? = null) {
        val comicPicImageState = comicPicState.value.data?.getOrNull(index) ?: return
        if (prefetchDeferredMap[index] != null) {
            prefetchDeferredMap[index]!!.await()
            onComplete?.invoke()
            return
        }
        val task = viewModelScope.async {
            comicPicImageState.load()
        }
        prefetchDeferredMap[index] = task
        task.await()
        onComplete?.invoke()
    }

    abstract fun load(comicId: Int)

    abstract fun retry(comicId: Int)
}