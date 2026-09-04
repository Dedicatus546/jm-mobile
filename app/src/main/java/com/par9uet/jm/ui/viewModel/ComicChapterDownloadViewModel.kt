package com.par9uet.jm.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.par9uet.jm.database.dao.DownloadComicDao
import com.par9uet.jm.repository.ComicRepository
import com.par9uet.jm.retrofit.model.ComicDetailResponse
import com.par9uet.jm.retrofit.model.NetworkResult
import com.par9uet.jm.store.DownloadManager
import com.par9uet.jm.store.ToastManager
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ComicChapterDownloadViewModel @Inject constructor(
    private val downloadComicDao: DownloadComicDao,
    private val downloadManager: DownloadManager,
    private val comicRepository: ComicRepository,
    private val toastManager: ToastManager
) : ViewModel() {
    private val _comicIdListFilter = MutableStateFlow(listOf<Int>())

    @OptIn(ExperimentalCoroutinesApi::class)
    val downloadComicMapFlow = _comicIdListFilter.flatMapLatest {
        downloadComicDao.getListByIdList(it)
            .map { list ->
                list.associateBy { item ->
                    item.id
                }
            }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = mapOf()
    )

    fun updateComicIdListFilter(idList: List<Int>) {
        _comicIdListFilter.update {
            idList
        }
    }

    fun downloadComic(comicId: Int) {
        viewModelScope.launch {
            when (val data = comicRepository.getComicDetail(comicId)) {
                is NetworkResult.Error -> {
                    toastManager.show("获取本子详情失败，请重试")
                }

                is NetworkResult.Success<ComicDetailResponse> -> {
                    val comic = data.data.toComic()
                    downloadManager.downloadComic(comic)
                }
            }
        }
    }

    val waitDownloadComicIdFlow = MutableStateFlow(0)

    fun updateWaitDownloadComicId(id: Int) {
        waitDownloadComicIdFlow.update {
            id
        }
    }
}