package com.par9uet.jm.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.par9uet.jm.database.dao.LocalComicDao
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

@HiltViewModel
class ComicChapterDownloadViewModel @Inject constructor(
    private val localComicDao: LocalComicDao
) : ViewModel() {
    private val _comicIdListFilter = MutableStateFlow(listOf<Int>())

    @OptIn(ExperimentalCoroutinesApi::class)
    val localComicMap = _comicIdListFilter.flatMapLatest {
        localComicDao.getListByIdList(it)
            .map { list ->
                list.associateBy { item ->
                    item.comicId
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
}