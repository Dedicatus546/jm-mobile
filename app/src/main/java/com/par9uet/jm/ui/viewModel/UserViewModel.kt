package com.par9uet.jm.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.par9uet.jm.data.models.CollectComicOrderFilter
import com.par9uet.jm.repository.UserRepository
import com.par9uet.jm.retrofit.model.LoginResponse
import com.par9uet.jm.retrofit.model.NetWorkResult
import com.par9uet.jm.store.UserManager
import com.par9uet.jm.ui.models.CommonUIState
import com.par9uet.jm.ui.pagingSource.CollectComicPagingSource
import com.par9uet.jm.ui.pagingSource.HistoryComicPagingSource
import com.par9uet.jm.ui.pagingSource.HistoryCommentPagingSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserViewModel(
    private val userManager: UserManager,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _loginState = MutableStateFlow(CommonUIState(data = null))
    val loginState = _loginState.asStateFlow()
    fun login(username: String, password: String) {
        viewModelScope.launch {
            _loginState.update {
                it.copy(
                    isLoading = true
                )
            }
            when (val data = withContext(Dispatchers.IO) {
                userRepository.login(username, password)
            }) {
                is NetWorkResult.Error -> {
                    _loginState.update {
                        it.copy(
                            isError = true,
                            errorMsg = data.message
                        )
                    }
                }

                is NetWorkResult.Success<LoginResponse> -> {
                    userManager.updateUser(
                        data.data.toUser(
                            password = password
                        )
                    )
                }
            }
            _loginState.update {
                it.copy(
                    isLoading = false
                )
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            userManager.clearUser()
        }
    }

    private val _collectComicOrder = MutableStateFlow(CollectComicOrderFilter.COLLECT_TIME)
    val collectComicOrder = _collectComicOrder.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val collectComicPager = _collectComicOrder.flatMapLatest { order ->
        Pager(
            config = PagingConfig(pageSize = 20, prefetchDistance = 6, initialLoadSize = 20),
            pagingSourceFactory = {
                CollectComicPagingSource(
                    userRepository,
                    order
                )
            }
        ).flow
    }.cachedIn(viewModelScope)

    fun changeCollectComicOrder(order: CollectComicOrderFilter) {
        _collectComicOrder.update {
            order
        }
    }

    val historyComicPager = Pager(
        config = PagingConfig(pageSize = 20, prefetchDistance = 6, initialLoadSize = 20),
        pagingSourceFactory = {
            HistoryComicPagingSource(
                userRepository,
            )
        }
    ).flow.cachedIn(viewModelScope)

    val historyCommentPager = Pager(
        config = PagingConfig(pageSize = 20, prefetchDistance = 6, initialLoadSize = 20),
        pagingSourceFactory = {
            HistoryCommentPagingSource(
                userRepository,
                userManager.userState.value.id
            )
        }
    ).flow.cachedIn(viewModelScope)

    private val _signInDataState = MutableStateFlow(CommonUIState())
}