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
import com.par9uet.jm.retrofit.model.NetWorkResult
import com.par9uet.jm.retrofit.model.UserCollectComicListResponse
import com.par9uet.jm.retrofit.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserCollectComicViewModel(
    private val userRepository: UserRepository
) : ViewModel() {
    var loading by mutableStateOf(false)
    var list by mutableStateOf(mutableListOf<Comic>())
    var page by mutableIntStateOf(0)
    var order by mutableStateOf(ComicFilterOrder.MR)
    var total by mutableIntStateOf(0)

    fun getCollectComicList(
        nPage: Int = 0,
        clearList: Boolean = false
    ) {
        page = nPage
        viewModelScope.launch {
            loading = true
            when (val data = withContext(Dispatchers.IO) {
                userRepository.getCollectComicList(page, order.value)
            }) {
                is NetWorkResult.Error<*> -> {
                    Log.v("api", data.message)
                }


                is NetWorkResult.Success<UserCollectComicListResponse> -> {
                    Log.v("api", data.data.toString())
                    if (clearList) {
                        list = mutableListOf()
                    }
                    list.addAll(data.data.toComicList())
                    list = list.toMutableList()
                    total = data.data.total
                }
            }
            loading = false
        }
    }
}