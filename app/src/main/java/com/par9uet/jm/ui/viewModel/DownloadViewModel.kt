package com.par9uet.jm.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.par9uet.jm.database.dao.LocalComicDao
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update

data class DownloadFilter(
    val status: String,
)

@HiltViewModel
class DownloadViewModel @Inject constructor(
    private val localComicDao: LocalComicDao
) : ViewModel() {
    private val _downloadFilterState = MutableStateFlow(DownloadFilter("downloading"))
    val downloadFilterState = _downloadFilterState.asStateFlow()

    fun updateDownloadStatusFilter(status: String) {
        _downloadFilterState.update {
            it.copy(
                status = status
            )
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val localComicPager = _downloadFilterState.flatMapLatest { filter ->
        Pager(
            config = PagingConfig(
                pageSize = 20,
                prefetchDistance = 6,
                initialLoadSize = 20
            ),
        ) {
            if (filter.status == "downloading") {
                localComicDao.getUnCompleteList()
            } else {
                localComicDao.getCompleteList()
            }
        }.flow
    }.cachedIn(viewModelScope)
}