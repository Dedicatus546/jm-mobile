package com.par9uet.jm.data.models

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.core.net.toFile
import coil3.ImageLoader
import coil3.request.CachePolicy
import coil3.request.ErrorResult
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.request.allowHardware
import coil3.size.Size
import coil3.toBitmap
import com.par9uet.jm.database.dao.LocalComicDao
import com.par9uet.jm.database.dao.LocalComicPicDao
import com.par9uet.jm.database.model.ret.LocalComicWithPic
import com.par9uet.jm.database.model.update.UpdateLocalComicDownloadingStatus
import com.par9uet.jm.database.model.update.UpdateLocalComicErrorStatus
import com.par9uet.jm.database.model.update.UpdateLocalComicPicWhenComplete
import com.par9uet.jm.database.model.update.UpdateLocalComicProgress
import com.par9uet.jm.database.model.update.UpdateLocalComicWhenComplete
import com.par9uet.jm.store.LocalSettingManager
import com.par9uet.jm.store.ToastManager
import com.par9uet.jm.utils.compressComicPic
import com.par9uet.jm.utils.decodeComicPicBitmap
import com.par9uet.jm.utils.log
import com.par9uet.jm.utils.md5
import com.par9uet.jm.utils.sanitizeFileName
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.concurrent.atomics.AtomicInt
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.update

data class Downloader @AssistedInject constructor(
    @Assisted("comicId") val comicId: Int,
    @Assisted("comicChapterId") val comicChapterId: Int,
    @ApplicationContext private val context: Context,
    private val localSettingManager: LocalSettingManager,
    private val localComicDao: LocalComicDao,
    private val localComicPicDao: LocalComicPicDao,
    private val imageLoader: ImageLoader,
    private val toastManager: ToastManager
) {
    private lateinit var localComicWithPic: LocalComicWithPic
    suspend fun download() {
        localComicWithPic = localComicDao.getWithLocalComicPic(comicChapterId)
        try {
            localComicDao.updateDownloadingStatus(
                UpdateLocalComicDownloadingStatus(
                    comicChapterId,
                )
            )
            downloadPicList()
            exportZipFile()
            localComicDao.updateWhenComplete(
                UpdateLocalComicWhenComplete(
                    comicChapterId,
                )
            )
            toastManager.show("下载成功")
        } catch (e: Exception) {
            log("下载过程出错，${e.stackTraceToString()}")
            localComicDao.updateErrorStatus(
                UpdateLocalComicErrorStatus(
                    comicChapterId,
                    e.stackTraceToString()
                )
            )
        }
    }

    @OptIn(ExperimentalAtomicApi::class)
    private suspend fun downloadPicList() {
        val scrambleId = localComicWithPic.localComic.scrambleId
        val speed = localComicWithPic.localComic.speed
        val size = localComicWithPic.localComicPicList.size
        val completeCount = AtomicInt(0)
        localComicWithPic.localComicPicList.forEach { item ->
            when (item.isComplete) {
                true -> completeCount.update { it + 1 }
                false -> {
                    val request = ImageRequest.Builder(context)
                        .data(item.url)
                        // 下载的时候禁用缓存，强制走请求（不过 okhttp 是否会影响？）
                        .memoryCachePolicy(CachePolicy.DISABLED)
                        .diskCachePolicy(CachePolicy.DISABLED)
                        // 这里必须使用原始 size ，不然解密会有问题，出现白线
                        .size { Size.ORIGINAL }
                        .allowHardware(false)
                        .build()

                    when (val result = imageLoader.execute(request)) {
                        is ErrorResult -> {
                            throw Error("下载 ${item.page} 图片失败", result.throwable)
                        }

                        is SuccessResult -> {
                            val originalBitmap = result.image.toBitmap()
                            val decodedBitmap = decodeComicPicBitmap(
                                item.url,
                                originalBitmap,
                                item.comicId,
                                scrambleId!!,
                                speed!!,
                                item.page
                            )
                            val file = item.path.toFile()
                            FileOutputStream(file).use { out ->
                                compressComicPic(
                                    decodedBitmap,
                                    localSettingManager.localSettingState.value.comicPicDecodeCompressLevel,
                                    out
                                )
                            }
                            val progress = completeCount.addAndFetch(1) * 1.0f / size
                            localComicDao.updateProgress(
                                UpdateLocalComicProgress(
                                    comicChapterId,
                                    progress
                                )
                            )
                            localComicPicDao.updateComplete(
                                UpdateLocalComicPicWhenComplete(
                                    comicId = item.comicId,
                                    url = item.url,
                                    md5 = md5(file),
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    private fun exportZipFile() {
        val localComic = localComicWithPic.localComic
        val localComicPicList = localComicWithPic.localComicPicList
        val zipFilename =
            sanitizeFileName("[${localComic.comicId}] ${localComic.name} ${localComic.chapterName}")
        val uri = createUri(zipFilename)
        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
            ZipOutputStream(outputStream).use { zipOut ->
                localComicPicList.forEach { item ->
                    val file = item.path.toFile()
                    if (file.exists()) {
                        val zipEntry = ZipEntry(file.name)
                        zipOut.putNextEntry(zipEntry)
                        FileInputStream(file).use { fis ->
                            fis.copyTo(zipOut)
                        }
                        zipOut.closeEntry()
                    }
                }
            }
        }
    }

    private fun createUri(filename: String): Uri {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/zip")
            // 保存到 Download/jm-mobile 目录下
            put(
                MediaStore.MediaColumns.RELATIVE_PATH,
                "${Environment.DIRECTORY_DOWNLOADS}/jm-mobile"
            )
        }

        val uri =
            context.contentResolver.insert(
                MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                contentValues
            )

        if (uri == null) {
            throw Error("创建 $filename uri 失败")
        }

        return uri
    }
}