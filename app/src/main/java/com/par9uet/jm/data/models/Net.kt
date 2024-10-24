package com.par9uet.jm.data.models

data class ApiResponse<T>(
    val code: Int,
    val errorMsg: String?,
    var data: T,
)

data class SettingResponseData(
    val logo_path: String,
    val main_web_host: String,
    val img_host: String,
    val base_url: String,
    val is_cn: Int,
    val cn_base_url: String,
    val version: String,
    val test_version: String,
    val store_link: String,
    val ios_version: String,
    val ios_test_version: String,
    val ios_store_link: String,
    val ad_cache_version: Int,
    val bundle_url: String,
    val is_hot_update: String,
    val api_banner_path: String,
    val version_info: String,
    val app_shunts: List<Shunt>,
    val download_url: String,
    val app_landing_page: String,
    val float_ad: Boolean,
    val newYearEvent: Boolean,
    val foolsDayEvent: Boolean,
) {
    inner class Shunt(val title: String, val key: String)
}

data class LoginResponseData(
    val uid: String,
    val username: String,
    val email: String,
    val emailverified: Boolean,
    val photo: String,
    val fname: String,
    val gender: String,
    val message: String,
    val coin: Int,
    val album_favorites: Int,
    val s: String,
    val level_name: String,
    val level: Int,
    val nextLevelExp: Int,
    val exp: String,
    val expPercent: Double,
    val badges: List<Any>,
    val album_favorites_max: Int,
    val ad_free: Boolean,
    val ad_free_before: String,
    val charge: String,
    val jar: String,
    val invitation_qrcode: String,
    val invitation_url: String,
    val invited_cnt: String,
)

data class ComicListItemCategoryResponseData(
    val id: String,
    val title: String,
)

data class ComicListItemResponseData(
    val id: String,
    val author: String,
    val description: String,
    val name: String,
    val image: String,
    val category: ComicListItemCategoryResponseData,
    val category_sub: ComicListItemCategoryResponseData,
    val liked: Boolean,
    val is_favorite: Boolean,
    val update_at: Int,
)

data class ComicListResponseData(
    val search_query: String,
    val total: String,
    val content: List<ComicListItemResponseData>
)

data class CollectComicListResponseData(
    val count: Int,
    val folder_list: List<Any>,
    val list: List<ComicListItemResponseData>,
    val total: String,
)

data class HistoryComicListResponseData(
    val list: List<ComicListResponseData>,
    val total: String,
)

data class PromoteComicListItemResponseData(
    val id: String,
    val title: String,
    val slug: String,
    val type: String,
    val filter_val: String,
    val content: List<ComicListItemResponseData>
)

data class ComicDetailSeriesListItem(
    val id: String,
    val name: String,
    val sort: String,
)

data class ComicDetailRelateListItem(
    val id: String,
    val author: String,
    val name: String,
    val image: String,
)

data class ComicDetailResponseData(
    val id: Int,
    val name: String,
    val images: List<Any>,
    val addtime: String,
    val description: String,
    val total_views: String,
    val likes: String,
    val series: List<ComicDetailSeriesListItem>,
    val series_id: String,
    val comment_total: String,
    val author: List<String>,
    val tags: List<String>,
    val works: List<String>,
    val actors: List<String>,
    val related_list: List<ComicDetailRelateListItem>,
    val liked: Boolean,
    val is_favorite: Boolean,
    val is_aids: Any,
    val price: Any,
    val purchased: Any,
)