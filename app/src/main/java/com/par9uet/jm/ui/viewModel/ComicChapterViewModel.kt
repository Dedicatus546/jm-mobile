package com.par9uet.jm.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.par9uet.jm.repository.LocalComicRepository
import com.par9uet.jm.utils.getOrThrow
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

@HiltViewModel
class ComicChapterViewModel @Inject constructor(
    private val localComicRepository: LocalComicRepository
) : ViewModel() {
    val downloadComicId = MutableStateFlow(0)

    @OptIn(ExperimentalCoroutinesApi::class)
    val localComic = downloadComicId.flatMapLatest {
        localComicRepository.getNullableLocalComicFlow(it).getOrThrow()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    fun loadDownloadComic(id: Int) {
        downloadComicId.update {
            id
        }
    }
}