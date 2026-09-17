package com.par9uet.jm.router.navtype

import android.net.Uri
import android.os.Bundle
import androidx.navigation.NavType
import com.par9uet.jm.data.models.Comic
import com.par9uet.jm.utils.json

object ComicType : NavType<Comic>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): Comic? {
        return bundle.getString(key)?.let { json.decodeFromString(it) }
    }

    override fun parseValue(value: String): Comic {
        return json.decodeFromString(Uri.decode(value))
    }

    override fun serializeAsValue(value: Comic): String {
        return Uri.encode(json.encodeToString(value))
    }

    override fun put(bundle: Bundle, key: String, value: Comic) {
        bundle.putString(key, json.encodeToString(value))
    }
}