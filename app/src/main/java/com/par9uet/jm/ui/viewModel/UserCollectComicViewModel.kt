package com.par9uet.jm.ui.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.par9uet.jm.data.models.Comic
import com.par9uet.jm.data.models.ComicFilterOrder
import com.par9uet.jm.retrofit.model.NetWorkResult
import com.par9uet.jm.retrofit.model.UserCollectComicListResponse
import com.par9uet.jm.retrofit.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserCollectComicViewModel(
    private val userRepository: UserRepository
) : ViewModel() {
    var isRefreshing by mutableStateOf(true)
    var isLoadingMore by mutableStateOf(false)
    var list by mutableStateOf(listOf<Comic>())
    var page by mutableIntStateOf(0)
    var order by mutableStateOf(ComicFilterOrder.MR)
    var total by mutableIntStateOf(0)
    val hasMore get() = list.size < total

    fun refresh() {
        viewModelScope.launch(Dispatchers.IO) {
            isRefreshing = true
            page = 1
            when (val data = withContext(Dispatchers.IO) {
                userRepository.getCollectComicList(page, order.value)
            }) {
                is NetWorkResult.Error<*> -> {
                }


                is NetWorkResult.Success<UserCollectComicListResponse> -> {
                    list = data.data.toComicList()
                    total = data.data.total
                }
            }
            isRefreshing = false
        }
    }

    fun loadMore() {
        viewModelScope.launch(Dispatchers.IO) {
            isLoadingMore = true
            page++
            when (val data = withContext(Dispatchers.IO) {
                userRepository.getCollectComicList(page, order.value)
            }) {
                is NetWorkResult.Error<*> -> {
                }


                is NetWorkResult.Success<UserCollectComicListResponse> -> {
                    list = list + data.data.toComicList()
                    total = data.data.total.toInt()
                }
            }
            isLoadingMore = false
        }
    }
}