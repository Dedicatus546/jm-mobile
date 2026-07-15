package com.par9uet.jm.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.par9uet.jm.data.models.ComicSearchOrderFilter
import com.par9uet.jm.repository.ComicRepository
import com.par9uet.jm.ui.pagingSource.SearchComicFilter
import com.par9uet.jm.ui.pagingSource.SearchComicPagingSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update

class ComicSearchResultViewModel(
    private val comicRepository: ComicRepository
): ViewModel() {
    private val _searchComicFilterState = MutableStateFlow(SearchComicFilter())
    val searchComicFilterState = _searchComicFilterState.asStateFlow()
    private val _isSearchComicFirstLoading = MutableStateFlow(true)
    val isSearchComicFirstLoading = _isSearchComicFirstLoading.asStateFlow()

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
                )
            }
        ).flow
    }.cachedIn(viewModelScope)

    fun changeSearchComicOrderFilter(order: ComicSearchOrderFilter) {
        _searchComicFilterState.update {
            it.copy(
                order = order
            )
        }
    }

    fun changeSearchComicContentFilter(searchContent: String) {
        _searchComicFilterState.update {
            it.copy(
                searchContent = searchContent
            )
        }
    }

    fun updateIsSearchComicFirstLoading(ifl: Boolean) {
        _isSearchComicFirstLoading.update {
            ifl
        }
    }
}