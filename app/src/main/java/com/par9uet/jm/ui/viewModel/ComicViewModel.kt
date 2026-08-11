package com.par9uet.jm.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.par9uet.jm.data.models.HomeComicSwiperItem
import com.par9uet.jm.data.models.WeekData
import com.par9uet.jm.repository.ComicRepository
import com.par9uet.jm.retrofit.model.HomeSwiperComicListItemResponse
import com.par9uet.jm.retrofit.model.NetWorkResult
import com.par9uet.jm.retrofit.model.WeekResponse
import com.par9uet.jm.ui.models.CommonUIState
import com.par9uet.jm.ui.pagingSource.WeekComicPagingSource
import com.par9uet.jm.ui.pagingSource.WeekFilter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ComicViewModel(
    private val comicRepository: ComicRepository
) : ViewModel() {
    data class HomeComicUIState(
        val isLoading: Boolean = true,
        val isError: Boolean = false,
        val list: List<HomeComicSwiperItem>? = null,
        val errorMsg: String? = null
    )

    private val _homeComicState = MutableStateFlow(HomeComicUIState())
    val homeComicState = _homeComicState.asStateFlow()
    private val _isHomeComicFirstLoading = MutableStateFlow(true)
    val isHomeComicFirstLoading = _isHomeComicFirstLoading.asStateFlow()
    fun getHomeComic() {
        viewModelScope.launch {
            _homeComicState.update {
                it.copy(
                    isLoading = true,
                    isError = false,
                    errorMsg = ""
                )
            }
            when (val data = comicRepository.getHomeSwiperComicList()) {
                is NetWorkResult.Error -> {
                    _homeComicState.update {
                        it.copy(isError = true, errorMsg = data.message)
                    }
                }

                is NetWorkResult.Success<List<HomeSwiperComicListItemResponse>> -> {
                    _homeComicState.update {
                        it.copy(
                            list = data.data
                                // 去除文禁漫书库和禁漫小说
                                .filter { item -> item.type != "library" && item.type != "novels" }
                                .map { item -> item.toHomeComicSwiperItem() }
                        )
                    }
                }
            }
            _homeComicState.update {
                it.copy(isLoading = false)
            }
        }
    }

    fun updateIsHomeComicFirstLoading(ifl: Boolean) {
        _isHomeComicFirstLoading.update {
            ifl
        }
    }

    private val _weekDataState = MutableStateFlow(
        CommonUIState<WeekData>(
            isLoading = true
        )
    )
    val weekDataState = _weekDataState.asStateFlow()
    private val _refreshWeekDataTrigger = MutableSharedFlow<Unit>()
    val refreshWeekDataTrigger: SharedFlow<Unit> = _refreshWeekDataTrigger.asSharedFlow()
    fun getWeekData() {
        viewModelScope.launch {
            _weekDataState.update {
                it.copy(
                    isLoading = true,
                    isError = false,
                    errorMsg = ""
                )
            }
            when (val data = comicRepository.getWeekData()) {
                is NetWorkResult.Error -> {
                    _weekDataState.update {
                        it.copy(isError = true, errorMsg = data.message)
                    }
                }

                is NetWorkResult.Success<WeekResponse> -> {
                    val d = data.data.toWeekData()
                    _weekDataState.update {
                        it.copy(data = d)
                    }
                    if (d.categoryList.isNotEmpty()) {
                        _weekFilterState.update {
                            it.copy(categoryId = d.categoryList[0].first)
                        }
                    }
                    if (d.typeList.isNotEmpty()) {
                        _weekFilterState.update {
                            it.copy(typeId = d.typeList[0].first)
                        }
                    }
                    _refreshWeekDataTrigger.emit(Unit)
                }
            }
            _weekDataState.update {
                it.copy(isLoading = false)
            }
        }
    }

    private val _weekFilterState = MutableStateFlow(WeekFilter())
    val weekFilterState = _weekFilterState.asStateFlow()
    private val _isWeekComicFirstLoading = MutableStateFlow(true)
    val isWeekComicFirstLoading = _isWeekComicFirstLoading.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val weekComicPager = _weekFilterState.flatMapLatest { filter ->
        Pager(
            config = PagingConfig(
                pageSize = 20,
                prefetchDistance = 6,
                initialLoadSize = 20
            ),
            pagingSourceFactory = {
                WeekComicPagingSource(
                    comicRepository,
                    filter
                )
            }
        ).flow
    }.cachedIn(viewModelScope)

    fun updateIsWeekComicFirstLoading(ifl: Boolean) {
        _isWeekComicFirstLoading.update {
            ifl
        }
    }

    fun changeWeekCategoryFilter(categoryId: String?) {
        _weekFilterState.update {
            it.copy(
                categoryId = categoryId
            )
        }
    }

    fun changeWeekTypeFilter(typeId: String?) {
        _weekFilterState.update {
            it.copy(
                typeId = typeId
            )
        }
    }
}