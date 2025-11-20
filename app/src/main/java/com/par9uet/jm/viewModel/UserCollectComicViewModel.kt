package com.par9uet.jm.viewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.par9uet.jm.data.models.Comic
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
    var list by mutableStateOf(listOf<Comic>())
    var page by mutableStateOf(0)
    var total by mutableStateOf(0)

    fun getCollectComicList(_page: Int = 0) {
        page = _page
        viewModelScope.launch {
            loading = true
            when (val data = withContext(Dispatchers.IO) {
                userRepository.getCollectComic(page)
            }) {
                is NetWorkResult.Error<*> -> {
                    Log.v("api", data.message)
                }

                is NetWorkResult.Loading<*> -> {
                    Log.v("api", "loading")
                }

                is NetWorkResult.Success<UserCollectComicListResponse> -> {
                    Log.v("api", data.data.toString())

                    list = data.data.toComicList()
                    total = data.data.total
                }
            }
            loading = false
        }
    }
}