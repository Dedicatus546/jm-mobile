package com.par9uet.jm.viewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.par9uet.jm.retrofit.model.ComicDetailResponse
import com.par9uet.jm.retrofit.model.NetWorkResult
import com.par9uet.jm.retrofit.repository.ComicRepository
import com.par9uet.jm.utils.createComic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ComicDetailViewModel(
    private val comicRepository: ComicRepository
) : ViewModel() {
    var comic by mutableStateOf(createComic(-1, "", listOf()))
    var loading by mutableStateOf(false)

    fun getComicDetail(id: Int) {
        viewModelScope.launch {
            loading = true
            when(val data = withContext(Dispatchers.IO) {
                comicRepository.getComicDetail(id)
            }) {
                is NetWorkResult.Error<*> -> {
                   Log.v("api", data.message)
                }
                is NetWorkResult.Loading<*> -> {
                    Log.v("api", "loading")
                }
                is NetWorkResult.Success<ComicDetailResponse> -> {
                    Log.v("api", data.data.toString())
                    comic = data.data.toComic()
                }
            }
            loading = false
        }
    }
}