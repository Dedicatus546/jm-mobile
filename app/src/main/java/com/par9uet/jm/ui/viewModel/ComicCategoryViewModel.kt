package com.par9uet.jm.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.par9uet.jm.data.models.Category
import com.par9uet.jm.data.models.ComicCategoryOrderFilter
import com.par9uet.jm.repository.ComicRepository
import com.par9uet.jm.retrofit.model.ComicCategoryListResponse
import com.par9uet.jm.retrofit.model.NetWorkResult
import com.par9uet.jm.ui.models.CommonUIState
import com.par9uet.jm.ui.pagingSource.ComicCategoryFilter
import com.par9uet.jm.ui.pagingSource.ComicCategoryPagingSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class Filter(
    val categoryList: List<Category>,
    val tagList: List<String>,
)

class ComicCategoryViewModel(
    private val comicRepository: ComicRepository
) : ViewModel() {
    private val _comicCategoryFilterState = MutableStateFlow(ComicCategoryFilter())
    val comicCategoryFilterState = _comicCategoryFilterState.asStateFlow()
    private val _isComicCategoryFirstLoading = MutableStateFlow(true)
    val isComicCategoryFirstLoading = _isComicCategoryFirstLoading.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val comicCategoryPager = _comicCategoryFilterState.flatMapLatest { filter ->
        Pager(
            config = PagingConfig(
                pageSize = 20,
                prefetchDistance = 6,
                initialLoadSize = 20
            ),
            pagingSourceFactory = {
                ComicCategoryPagingSource(
                    comicRepository,
                    filter
                )
            }
        ).flow
    }.cachedIn(viewModelScope)

    private val _filterListState = MutableStateFlow(CommonUIState<Filter>())
    val filterListState = _filterListState.asStateFlow()

    fun getCategory() {
        viewModelScope.launch {
            _filterListState.update {
                it.copy(
                    isLoading = true,
                    isError = false,
                    errorMsg = ""
                )
            }
            when (val data = comicRepository.getCategoryList()) {
                is NetWorkResult.Error -> {
                    _filterListState.update {
                        it.copy(isError = true, errorMsg = data.message)
                    }
                }

                is NetWorkResult.Success<ComicCategoryListResponse> -> {
                    _filterListState.update {
                        it.copy(
                            data = Filter(
                                categoryList = data.data.toCategoryList(),
                                tagList = data.data.toTagList()
                            )
                        )
                    }
                }
            }
            _filterListState.update {
                it.copy(isLoading = false)
            }
        }
    }

    fun changeComicCategoryOrderFilter(order: ComicCategoryOrderFilter) {
        _comicCategoryFilterState.update {
            it.copy(
                order = order
            )
        }
    }

    fun changeComicCategoryFilter(category: String) {
        _comicCategoryFilterState.update {
            it.copy(
                category = category
            )
        }
    }

    fun changeComicSubCategoryFilter(subCategory: String) {
        _comicCategoryFilterState.update {
            it.copy(
                subCategory = subCategory
            )
        }
    }

    fun updateIsComicCategoryFirstLoading(ifl: Boolean) {
        _isComicCategoryFirstLoading.update {
            ifl
        }
    }
}