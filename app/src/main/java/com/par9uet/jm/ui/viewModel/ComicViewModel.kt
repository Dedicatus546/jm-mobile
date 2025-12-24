package com.par9uet.jm.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.cachedIn
import com.par9uet.jm.data.models.ComicSearchOrderFilter
import com.par9uet.jm.data.models.HomeComicSwiperItem
import com.par9uet.jm.repository.ComicRepository
import com.par9uet.jm.retrofit.model.HomeSwiperComicListItemResponse
import com.par9uet.jm.retrofit.model.NetWorkResult
import com.par9uet.jm.ui.pagingSource.SearchComicFilter
import com.par9uet.jm.ui.pagingSource.SearchComicPagingSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PromoteComicPagingSource(
    private val comicRepository: ComicRepository
) : PagingSource<Int, HomeComicSwiperItem>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, HomeComicSwiperItem> {
        val currentPage = params.key ?: 1
        return when (val data = comicRepository.getHomeSwiperComicList()) {
            is NetWorkResult.Error -> {
                LoadResult.Error(Exception(data.message))
            }

            is NetWorkResult.Success<List<HomeSwiperComicListItemResponse>> -> {
                LoadResult.Page(
                    data = data.data.map { it.toHomeComicSwiperItem() },
                    prevKey = if (currentPage == 1) null else currentPage - 1,
                    nextKey = if (data.data.isEmpty()) null else currentPage + 1
                )
            }
        }
    }

    override fun getRefreshKey(state: PagingState<Int, HomeComicSwiperItem>): Int? =
        state.anchorPosition
}

class ComicViewModel(
    private val comicRepository: ComicRepository
) : ViewModel() {
    data class HomeComicUIState(
        val isLoading: Boolean = true,
        val isError: Boolean = false,
        val list: List<HomeComicSwiperItem> = listOf(),
        val errorMsg: String? = null
    )
    private val _homeComicState = MutableStateFlow(HomeComicUIState())
    val homeComicState = _homeComicState.asStateFlow()
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
                        it.copy(list = data.data.map { item -> item.toHomeComicSwiperItem() })
                    }
                }
            }
            _homeComicState.update {
                it.copy(isLoading = false)
            }
        }
    }

    private val _searchComicFilterState = MutableStateFlow(SearchComicFilter())
    val searchComicFilterState = _searchComicFilterState.asStateFlow()
    private val _searchComicIdState = MutableStateFlow<Int?>(null)
    val searchComicIdState = _searchComicIdState.asStateFlow()
    @OptIn(ExperimentalCoroutinesApi::class)
    val searchComicPager = _searchComicFilterState.flatMapLatest { filter ->
        Pager(
            config = PagingConfig(
                pageSize = 20,
                prefetchDistance = 6,
                initialLoadSize = 20
            ),
            pagingSourceFactory = {
                SearchComicPagingSource(
                    comicRepository,
                    filter
                ) { id ->
                    _searchComicIdState.update {
                        id
                    }
                }
            }
        ).flow
    }.cachedIn(viewModelScope)

    fun changeSearchComicOrderFilter(order: ComicSearchOrderFilter) {
        _searchComicIdState.update { null }
        _searchComicFilterState.update {
            it.copy(
                order = order
            )
        }
    }

    fun changeSearchComicContent(searchContent: String) {
        _searchComicIdState.update { null }
        _searchComicFilterState.update {
            it.copy(
                searchContent = searchContent
            )
        }
    }
}