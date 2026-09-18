package com.par9uet.jm.ui.viewModel

import androidx.lifecycle.viewModelScope
import com.par9uet.jm.data.factory.OnlineComicPicImageFactory
import com.par9uet.jm.repository.ComicRepository
import com.par9uet.jm.retrofit.model.ComicPicListResponse
import com.par9uet.jm.retrofit.model.NetworkResult
import com.par9uet.jm.store.LocalSettingManager
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class OnlineComicReadViewModel @Inject constructor(
    private val comicRepository: ComicRepository,
    private val localSettingManager: LocalSettingManager,
    private val onlineComicPicImageFactory: OnlineComicPicImageFactory,
) : BaseComicReadViewModel(
    localSettingManager
) {
    override fun load(comicId: Int) {
        viewModelScope.launch {
            _comicPicState.update {
                it.copy(
                    isLoading = true,
                    isError = false,
                    errorMsg = ""
                )
            }
            prefetchDeferredMap.clear()
            val shunt = localSettingManager.localSettingState.value.shunt
            when (val data = comicRepository.getComicPicList(comicId, shunt)) {
                is NetworkResult.Error -> {
                    _comicPicState.update {
                        it.copy(
                            isError = true,
                            errorMsg = data.message
                        )
                    }
                }

                is NetworkResult.Success<ComicPicListResponse> -> {
                    _comicPicState.update {
                        it.copy(
                            data = data.data.list.map { item ->
                                onlineComicPicImageFactory.create(
                                    comicId,
                                    item,
                                    data.data.__scrambleId,
                                    data.data.__speed,
                                )
                            }
                        )
                    }
                    updateCurrentIndex(0)
                }
            }
            _comicPicState.update {
                it.copy(
                    isLoading = false
                )
            }
        }
    }

    override fun retry(comicId: Int) {
        load(comicId)
    }
}