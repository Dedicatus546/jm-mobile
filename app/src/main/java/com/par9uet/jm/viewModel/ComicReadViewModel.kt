package com.par9uet.jm.viewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.par9uet.jm.retrofit.model.NetWorkResult
import com.par9uet.jm.retrofit.repository.ComicRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ComicReadViewModel(
    private val comicRepository: ComicRepository
) : ViewModel() {
    var loading by mutableStateOf(false)
    var list by mutableStateOf(listOf<String>())

    fun getComicPicList(id: Int) {
        viewModelScope.launch {
            loading = true
            when (val data = withContext(Dispatchers.IO) {
                comicRepository.getComicPicList(id)
            }) {
                is NetWorkResult.Error<*> -> {
                    Log.v("api", data.message)
                }

                is NetWorkResult.Loading<*> -> {
                    Log.v("api", "loading")
                }

                is NetWorkResult.Success<List<String>> -> {
                    list = data.data
                }
            }
            loading = false
        }
    }
}