package com.par9uet.jm.ui.viewModel

import androidx.lifecycle.viewModelScope
import com.par9uet.jm.data.factory.LocalComicPicImageFactory
import com.par9uet.jm.data.models.DbResult
import com.par9uet.jm.database.model.LocalComicPic
import com.par9uet.jm.repository.LocalComicRepository
import com.par9uet.jm.store.LocalSettingManager
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class LocalComicReadViewModel @Inject constructor(
    private val localComicRepository: LocalComicRepository,
    private val localComicPicImageFactory: LocalComicPicImageFactory,
    private val localSettingManager: LocalSettingManager,
) : BaseComicReadViewModel(
    localSettingManager
) {
    override fun retry(comicId: Int) {
        load(comicId)
    }

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
            when (val data = localComicRepository.getComicPicList(comicId)) {
                is DbResult.Error -> {
                    _comicPicState.update {
                        it.copy(
                            isError = true,
                            errorMsg = data.message
                        )
                    }
                }

                is DbResult.Success<List<LocalComicPic>> -> {
                    _comicPicState.update {
                        it.copy(
                            data = data.data.map { item ->
                                localComicPicImageFactory.create(
                                    comicId,
                                    item.path,
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
}