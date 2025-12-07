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
import com.par9uet.jm.retrofit.model.ComicListResponse
import com.par9uet.jm.retrofit.model.NetWorkResult
import com.par9uet.jm.retrofit.model.UserCollectComicListResponse
import com.par9uet.jm.retrofit.repository.ComicRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ComicQuickSearchViewModel(
    private val comicRepository: ComicRepository
) : ViewModel() {
    var loading by mutableStateOf(false)
    var list by mutableStateOf(mutableListOf<Comic>())
    var page by mutableIntStateOf(0)
    var order by mutableStateOf(ComicFilterOrder.MR)
    var total by mutableIntStateOf(0)

    fun getComicList(
        nPage: Int = 0,
        searchContent: String,
        clearList: Boolean = false,
    ) {
        page = nPage
        viewModelScope.launch {
            loading = true
            when (val data = withContext(Dispatchers.IO) {
                comicRepository.getComicList(page, order.value, searchContent)
            }) {
                is NetWorkResult.Error<*> -> {
                    Log.v("api", data.message)
                }

                is NetWorkResult.Success<ComicListResponse> -> {
                    Log.v("api", data.data.toString())
                    if (clearList) {
                        list = mutableListOf()
                    }
                    list.addAll(data.data.toComicList())
                    list = list.toMutableList()
                    total = data.data.total.toInt()
                }
            }
            loading = false
        }
    }
}