package com.par9uet.jm.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.par9uet.jm.data.models.Comic
import com.par9uet.jm.data.models.Comment
import com.par9uet.jm.repository.UserRepository
import com.par9uet.jm.retrofit.model.LoginResponse
import com.par9uet.jm.retrofit.model.NetWorkResult
import com.par9uet.jm.retrofit.model.UserCollectComicListResponse
import com.par9uet.jm.retrofit.model.UserHistoryComicListResponse
import com.par9uet.jm.retrofit.model.UserHistoryCommentListResponse
import com.par9uet.jm.store.UserManager
import com.par9uet.jm.ui.models.AppendListUIState
import com.par9uet.jm.ui.models.CommonUIState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserViewModel(
    private val userManager: UserManager,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _loginState = MutableStateFlow(CommonUIState(data = null))
    val loginState = _loginState.asStateFlow()
    fun login(username: String, password: String, isAutoLogin: Boolean = false) {
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
                    userManager.updateUser(data.data.toUser())
                }
            }
            if (isAutoLogin) {
                userManager.enableAutoLogin(username, password)
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

    private val _collectComicState = MutableStateFlow(AppendListUIState<Comic>())
    val collectComicState = _collectComicState.asStateFlow()
    fun getCollectComicList(type: String, order: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _collectComicState.update {
                (if (type == "refresh") it.copy(
                    isRefreshing = true,
                    page = 1
                ) else it.copy(
                    isMoreLoading = true,
                    page = it.page + 1,
                )).copy(
                    isError = false,
                    errorMsg = ""
                )
            }
            when (val data = withContext(Dispatchers.IO) {
                userRepository.getCollectComicList(_collectComicState.value.page, order)
            }) {
                is NetWorkResult.Error -> {
                    _collectComicState.update {
                        it.copy(
                            isError = true,
                            errorMsg = data.message
                        )
                    }
                }

                is NetWorkResult.Success<UserCollectComicListResponse> -> {
                    _collectComicState.update {
                        it.copy(
                            list = data.data.toComicList(),
                            total = data.data.total
                        )
                    }
                }
            }
            _collectComicState.update {
                if (type == "refresh") it.copy(
                    isRefreshing = false,
                    page = 1
                ) else it.copy(
                    isMoreLoading = false,
                    page = it.page - 1,
                )
            }
        }
    }

    private val _historyComicState = MutableStateFlow(AppendListUIState<Comic>())
    val historyComicState = _historyComicState.asStateFlow()
    fun getHistoryComicList(type: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _historyComicState.update {
                (if (type == "refresh") it.copy(
                    isRefreshing = true,
                    page = 1
                ) else it.copy(
                    isMoreLoading = true,
                    page = it.page + 1,
                )).copy(
                    isError = false,
                    errorMsg = ""
                )
            }
            when (val data = withContext(Dispatchers.IO) {
                userRepository.getHistoryComicList(_collectComicState.value.page)
            }) {
                is NetWorkResult.Error -> {
                    _historyComicState.update {
                        it.copy(
                            isError = true,
                            errorMsg = data.message
                        )
                    }
                }

                is NetWorkResult.Success<UserHistoryComicListResponse> -> {
                    _historyComicState.update {
                        it.copy(
                            list = data.data.toComicList(),
                            total = data.data.total
                        )
                    }
                }
            }
            _historyComicState.update {
                if (type == "refresh") it.copy(
                    isRefreshing = false,
                    page = 1
                ) else it.copy(
                    isMoreLoading = false,
                    page = it.page - 1,
                )
            }
        }
    }

    private val _historyCommentState = MutableStateFlow(AppendListUIState<Comment>())
    val historyCommentState = _historyCommentState.asStateFlow()
    fun getHistoryCommentList(
        type: String,
    ) {
        viewModelScope.launch {
            _historyCommentState.update {
                (if (type == "refresh") it.copy(
                    isRefreshing = true,
                    page = 1
                ) else it.copy(
                    isMoreLoading = true,
                    page = it.page + 1,
                )).copy(
                    isError = false,
                    errorMsg = ""
                )
            }
            when (val data = withContext(Dispatchers.IO) {
                userRepository.getCommentList(
                    _historyCommentState.value.page,
                    userManager.userState.value!!.id
                )
            }) {
                is NetWorkResult.Error -> {
                    _historyCommentState.update {
                        it.copy(
                            isError = true,
                            errorMsg = data.message
                        )
                    }
                }

                is NetWorkResult.Success<UserHistoryCommentListResponse> -> {
                    _historyCommentState.update {
                        it.copy(
                            list = data.data.toCommentList(),
                            total = data.data.total
                        )
                    }
                }
            }
            _historyCommentState.update {
                if (type == "refresh") it.copy(
                    isRefreshing = false,
                    page = 1
                ) else it.copy(
                    isMoreLoading = false,
                    page = it.page - 1,
                )
            }
        }
    }
}