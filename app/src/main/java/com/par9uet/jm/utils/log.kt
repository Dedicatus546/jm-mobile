package com.par9uet.jm.utils

import android.util.Log

inline fun <reified T> T.log(msg: String) {
    Log.d("[JM-MOBILE] ${T::class.java.simpleName}", msg)
}

fun log(tag: String, msg: String) {
    Log.d("[JM-MOBILE] $tag", msg)
}