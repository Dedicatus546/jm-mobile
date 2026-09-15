package com.par9uet.jm.router.navtype

import android.net.Uri
import android.os.Bundle
import androidx.navigation.NavType
import com.par9uet.jm.data.models.ComicChapter
import com.par9uet.jm.utils.json

object ComicChapterListType : NavType<List<ComicChapter>>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): List<ComicChapter>? {
        return bundle.getString(key)?.let { json.decodeFromString(it) }
    }

    override fun parseValue(value: String): List<ComicChapter> {
        return json.decodeFromString(Uri.decode(value))
    }

    override fun serializeAsValue(value: List<ComicChapter>): String {
        return Uri.encode(json.encodeToString(value))
    }

    override fun put(bundle: Bundle, key: String, value: List<ComicChapter>) {
        bundle.putString(key, json.encodeToString(value))
    }
}