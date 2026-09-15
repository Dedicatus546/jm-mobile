package com.par9uet.jm.router

import kotlinx.serialization.Serializable

@Serializable
data class ComicSearchResultRoute(
    val searchContent: String,
)