package com.par9uet.jm.store

import android.content.Context
import android.graphics.Bitmap
import androidx.core.graphics.drawable.toBitmap
import coil.ImageLoader
import coil.request.ErrorResult
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.par9uet.jm.cache.getDownloadDir
import com.par9uet.jm.data.models.Comic
import com.par9uet.jm.data.models.DownloadComic
import com.par9uet.jm.database.dao.DownloadComicDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class DownloadManager(
    private val scope: CoroutineScope,
    private val downloadComicDao: DownloadComicDao,
    private val context: Context,
    private val remoteSettingManager: RemoteSettingManager,
) {

    fun downloadComic(comic: Comic) {
        scope.launch {
            val coverPath = downloadCover(comic)
            downloadComicDao.insert(
                DownloadComic(
                    id = comic.id,
                    name = comic.name,
                    authorList = comic.authorList,
                    coverPath = coverPath,
                    picPathList = listOf(),
                    zipPath = "",
                    progress = 0f,
                    status = "pending"
                )
            )
            // TODO 提示
        }
    }

    private suspend fun downloadCover(comic: Comic): String {
        return withContext(Dispatchers.IO) {
            val coverUrl =
                "${remoteSettingManager.remoteSettingState.value.imgHost}/media/albums/${comic.id}_3x4.jpg"
            val loader = ImageLoader(context)
            val request = ImageRequest.Builder(context)
                .data(coverUrl)
                .allowHardware(false)
                .build()

            when (val result = loader.execute(request)) {
                is ErrorResult -> {
                    // TODO 处理错误
                    ""
                }

                is SuccessResult -> {
                    val bitmap = result.drawable.toBitmap()
                    val dir = getDownloadDir(context)
                    if (!dir.exists()) {
                        dir.mkdirs()
                    }
                    val file = File("$dir/${comic.id}.jpg")
                    FileOutputStream(file).use { out ->
                        bitmap.compress(Bitmap.CompressFormat.WEBP_LOSSY, 50, out)
                    }
                    file.absolutePath
                }
            }
        }
    }
}