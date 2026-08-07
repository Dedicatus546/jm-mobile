package com.par9uet.jm.ui.pagingSource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.par9uet.jm.data.models.Comic
import com.par9uet.jm.data.models.ComicCategoryOrderFilter
import com.par9uet.jm.repository.ComicRepository
import com.par9uet.jm.retrofit.model.ComicFilterListResponse
import com.par9uet.jm.retrofit.model.NetWorkResult

data class ComicCategoryFilter(
    val category: String = "",
    val subCategory: String = "",
    val order: ComicCategoryOrderFilter = ComicCategoryOrderFilter.NEWEST,
)

class ComicCategoryPagingSource(
    private val comicRepository: ComicRepository,
    private val filter: ComicCategoryFilter,
) : PagingSource<Int, Comic>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Comic> {
        val currentPage = params.key ?: 1
        return when (val data =
            comicRepository.getComicFilterList(
                page = currentPage,
                category = listOf(filter.category, filter.subCategory).filter { it.isNotEmpty() }
                    .joinToString("_"),
                order = filter.order.value
            )) {
            is NetWorkResult.Error -> {
                LoadResult.Error(Exception(data.message))
            }

            is NetWorkResult.Success<ComicFilterListResponse> -> {
                val list = data.data.toComicList()
                list.forEach { it.comicKey = "${it.id}-${filter.category}-${filter.order}" }
                val total = data.data.total.toInt()
                val isLastPage = currentPage >= (total + params.loadSize - 1) / params.loadSize
                LoadResult.Page(
                    data = list,
                    prevKey = if (currentPage == 1) null else currentPage - 1,
                    nextKey = if (isLastPage) null else currentPage + 1
                )
            }
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Comic>): Int? = null
}