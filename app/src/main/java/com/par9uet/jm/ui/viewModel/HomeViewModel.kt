package com.par9uet.jm.ui.viewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.par9uet.jm.data.models.HomeComicSwiperItem
import com.par9uet.jm.retrofit.model.HomeSwiperComicListItemResponse
import com.par9uet.jm.retrofit.model.NetWorkResult
import com.par9uet.jm.retrofit.repository.ComicRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(
    private val comicRepository: ComicRepository
) : ViewModel() {
    var list by mutableStateOf(listOf<HomeComicSwiperItem>())
    var loading by mutableStateOf(false)

    fun getPromoteComicList() {
        viewModelScope.launch {
            loading = true
            when (val data = withContext(Dispatchers.IO) {
                comicRepository.getHomeSwiperComicList()
            }) {
                is NetWorkResult.Error<*> -> {
                    Log.v("api", data.message)
                }

                is NetWorkResult.Success<List<HomeSwiperComicListItemResponse>> -> {
                    Log.v("api", data.data.toString())
                    list = data.data.map { it.toHomeComicSwiperItem() }
                }
            }
            loading = false
        }
    }
}