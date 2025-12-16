package com.par9uet.jm.ui.viewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.par9uet.jm.data.models.Comic
import com.par9uet.jm.data.models.ComicFilterOrder
import com.par9uet.jm.repository.ComicRepository
import com.par9uet.jm.retrofit.model.ComicListResponse
import com.par9uet.jm.retrofit.model.NetWorkResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ComicQuickSearchViewModel(
    private val comicRepository: ComicRepository
) : ViewModel() {
    var isRefreshing by mutableStateOf(true)
    var isLoadingMore by mutableStateOf(false)
    var list by mutableStateOf(listOf<Comic>())
    var page by mutableIntStateOf(0)
    var order by mutableStateOf(ComicFilterOrder.MR)
    var total by mutableIntStateOf(0)
    val hasMore get() = list.size < total

    fun refresh(searchContent: String) {
        viewModelScope.launch(Dispatchers.IO) {
            isRefreshing = true
            page = 1
            when (val data = comicRepository.getComicList(page, order.value, searchContent)) {
                is NetWorkResult.Error -> {
                    Log.v("api", data.message)
                }

                is NetWorkResult.Success<ComicListResponse> -> {
                    Log.v("api", data.data.toString())
                    list = data.data.toComicList()
                    total = data.data.total.toInt()
                }
            }
            isRefreshing = false
        }
    }

    fun loadMore(searchContent: String) {
        viewModelScope.launch(Dispatchers.IO) {
            isLoadingMore = true
            page++
            when (val data = comicRepository.getComicList(page, order.value, searchContent)) {
                is NetWorkResult.Error -> {
                    Log.v("api", data.message)
                }

                is NetWorkResult.Success<ComicListResponse> -> {
                    Log.v("api", data.data.toString())
                    list = list + data.data.toComicList()
                    total = data.data.total.toInt()
                }
            }
            isLoadingMore = false
        }
    }
}