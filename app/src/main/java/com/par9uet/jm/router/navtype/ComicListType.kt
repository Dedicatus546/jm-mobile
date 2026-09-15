package com.par9uet.jm.router.navtype

import android.net.Uri
import android.os.Bundle
import androidx.navigation.NavType
import com.par9uet.jm.data.models.Comic
import com.par9uet.jm.utils.json

object ComicListType : NavType<List<Comic>>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): List<Comic>? {
        return bundle.getString(key)?.let { json.decodeFromString(it) }
    }

    override fun parseValue(value: String): List<Comic> {
        return json.decodeFromString(Uri.decode(value))
    }

    override fun serializeAsValue(value: List<Comic>): String {
        return Uri.encode(json.encodeToString(value))
    }

    override fun put(bundle: Bundle, key: String, value: List<Comic>) {
        bundle.putString(key, json.encodeToString(value))
    }
}