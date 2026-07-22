package com.par9uet.jm.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.par9uet.jm.repository.UserRepository
import com.par9uet.jm.store.UserManager
import com.par9uet.jm.ui.pagingSource.HistoryCommentPagingSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class UserHistoryCommentViewModel(
    private val userManager: UserManager,
    private val userRepository: UserRepository,
) : ViewModel() {
    val historyCommentPager = Pager(
        config = PagingConfig(pageSize = 20, prefetchDistance = 6, initialLoadSize = 20),
        pagingSourceFactory = {
            HistoryCommentPagingSource(
                userRepository,
                userManager.userState.value.data!!.id
            )
        }
    ).flow.cachedIn(viewModelScope)
    private val _isHistoryCommentFirstLoading = MutableStateFlow(true)
    val isHistoryCommentFirstLoading = _isHistoryCommentFirstLoading.asStateFlow()

    fun updateIsHistoryCommentFirstLoading(ifl: Boolean) {
        _isHistoryCommentFirstLoading.update {
            ifl
        }
    }
}