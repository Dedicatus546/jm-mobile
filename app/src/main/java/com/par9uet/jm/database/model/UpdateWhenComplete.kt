package com.par9uet.jm.database.model

import android.net.Uri

data class UpdateWhenComplete(
    val id: Int,
    val zipPath: Uri,
    val zipMd5: String,
    val status: String = "complete"
)