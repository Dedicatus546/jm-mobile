package com.par9uet.jm.data.models

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.core.net.toFile
import coil3.ImageLoader
import coil3.request.ErrorResult
import coil3.request.SuccessResult
import coil3.toBitmap
import com.par9uet.jm.database.dao.LocalComicDao
import com.par9uet.jm.database.dao.LocalComicPicDao
import com.par9uet.jm.database.model.del.DeleteLocalComicPic
import com.par9uet.jm.database.model.ret.LocalComicWithPic
import com.par9uet.jm.database.model.update.UpdateLocalComicDownloadingStatus
import com.par9uet.jm.database.model.update.UpdateLocalComicErrorStatus
import com.par9uet.jm.database.model.update.UpdateLocalComicPicWhenComplete
import com.par9uet.jm.database.model.update.UpdateLocalComicProgress
import com.par9uet.jm.database.model.update.UpdateLocalComicWhenComplete
import com.par9uet.jm.store.LocalSettingManager
import com.par9uet.jm.store.ToastManager
import com.par9uet.jm.utils.compressComicPic
import com.par9uet.jm.utils.createComicOriginalPicImageRequest
import com.par9uet.jm.utils.decodeComicPicBitmap
import com.par9uet.jm.utils.getDownloadCacheDir
import com.par9uet.jm.utils.log
import com.par9uet.jm.utils.md5
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.concurrent.atomics.AtomicInt
import kotlin.concurrent.atomics.ExperimentalAtomicApi

@AssistedFactory
interface DownloaderFactory {
    fun create(comic: Comic, comicChapter: ComicChapter): Downloader
}

data class Downloader @AssistedInject constructor(
    @Assisted val comic: Comic,
    @Assisted val comicChapter: ComicChapter,
    @ApplicationContext private val context: Context,
    private val localSettingManager: LocalSettingManager,
    private val localComicDao: LocalComicDao,
    private val localComicPicDao: LocalComicPicDao,
    private val imageLoader: ImageLoader,
    private val toastManager: ToastManager
) {

    private lateinit var localComicWithPic: LocalComicWithPic
    private val belongComicId get() = comic.id
    private val comicId get() = comicChapter.id

    suspend fun download() {
        localComicWithPic = localComicDao.getWithLocalComicPic(comicChapter.id)
        try {
            localComicDao.updateDownloadingStatus(
                UpdateLocalComicDownloadingStatus(
                    comicId,
                )
            )
            val localComicPic = localComicWithPic.localComic
            val localComicPicList = localComicWithPic.localComicPicList
            val picList =
                downloadPicList()
            val zipFile = zipPicPathList(
                localComicPic.comicId,
                localComicPic.name + if (localComicPic.chapterName.isNotEmpty()) " [${localComicPic.chapterName}]" else "",
                picList
            )
            var uri = createUri(zipFile.name)
            // TODO 这里好像没生效？
            uri?.let {
                // 删除原有文件，防止出现（1）文件名后缀
                context.contentResolver.query(uri, null, null, null, null)
                    ?.use { cursor ->
                        if (cursor.moveToFirst()) {
                            context.contentResolver.delete(uri, null, null)
                        }
                    }
            }
            // 重新获取 uri ，不可对同一个 uri 执行多个操作，否则报错
            uri = createUri(zipFile.name)
            uri?.let {
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    zipFile.inputStream().use { inputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                val zipMd5 = md5(zipFile)
                // 删除临时文件
                zipFile.delete()
                picList.forEach { it.delete() }
                localComicPicDao.delete(
                    localComicPicList.map {
                        DeleteLocalComicPic(
                            it.comicId,
                            it.url
                        )
                    }
                )
                localComicDao.updateWhenComplete(
                    UpdateLocalComicWhenComplete(
                        comicId,
                        uri,
                        zipMd5
                    )
                )
                toastManager.show("下载成功")
            }
            // uri 不存在
        } catch (e: Exception) {
            log("下载过程出错，${e.stackTraceToString()}")
            localComicDao.updateErrorStatus(
                UpdateLocalComicErrorStatus(
                    comicId,
                    e.stackTraceToString()
                )
            )
        }
    }

    @OptIn(ExperimentalAtomicApi::class)
    private suspend fun downloadPicList(): List<File> {
        val scrambleId = localComicWithPic.localComic.scrambleId
        val speed = localComicWithPic.localComic.speed
        val size = localComicWithPic.localComicPicList.size
        val completeCount = AtomicInt(0)
        return localComicWithPic.localComicPicList.mapIndexed { _, item ->
            withContext(Dispatchers.IO) {
                val file = item.path.toFile()
                if (item.isComplete) {
                    return@withContext file
                }
                val request = createComicOriginalPicImageRequest(
                    context = context,
                    url = item.url,
                    comicId = item.comicId
                )

                when (val result = imageLoader.execute(request)) {
                    is ErrorResult -> {
                        throw Error("下载 ${item.page} 图片失败")
                    }

                    is SuccessResult -> {
                        // TODO 这里或许可以利用缓存？
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
                                item.comicId,
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
                        file
                    }
                }
            }
        }
    }

    private fun zipPicPathList(
        comicId: Int,
        name: String,
        picFileList: List<File>
    ): File {
        val filename =
            "[${comicId}]${name}".filter { it.isLetterOrDigit() || it == '[' || it == ']' || it == ' ' }
        val zipFile = File(getDownloadCacheDir(context), "$filename.zip")
        ZipOutputStream(FileOutputStream(zipFile)).use { zipOut ->
            picFileList.forEach { file ->
                if (file.exists()) {
                    val entryName = "${file.name}"
                    val zipEntry = ZipEntry(entryName)
                    zipOut.putNextEntry(zipEntry)
                    FileInputStream(file).use { fis ->
                        fis.copyTo(zipOut)
                    }
                    zipOut.closeEntry()
                }
            }
        }
        return zipFile
    }

    private fun createUri(filename: String): Uri? {
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

        return uri
    }
}