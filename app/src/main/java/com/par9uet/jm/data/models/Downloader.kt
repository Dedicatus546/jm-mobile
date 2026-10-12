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
import com.par9uet.jm.database.model.LocalComic
import com.par9uet.jm.database.model.LocalComicPic
import com.par9uet.jm.repository.LocalComicPicRepository
import com.par9uet.jm.repository.LocalComicRepository
import com.par9uet.jm.store.LocalSettingManager
import com.par9uet.jm.store.ToastManager
import com.par9uet.jm.utils.compressComicPic
import com.par9uet.jm.utils.decodeComicPicBitmap
import com.par9uet.jm.utils.getOrThrow
import com.par9uet.jm.utils.log
import com.par9uet.jm.utils.md5
import com.par9uet.jm.utils.runOrThrow
import com.par9uet.jm.utils.sanitizeFileName
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.concurrent.atomics.AtomicInt
import kotlin.concurrent.atomics.ExperimentalAtomicApi

data class Downloader @AssistedInject constructor(
    @Assisted("comicId") val comicId: Int,
    @Assisted("comicChapterId") val comicChapterId: Int,
    @ApplicationContext private val context: Context,
    private val localSettingManager: LocalSettingManager,
    private val localComicRepository: LocalComicRepository,
    private val localComicPicRepository: LocalComicPicRepository,
    private val imageLoader: ImageLoader,
    private val toastManager: ToastManager
) {
    private lateinit var localComic: LocalComic
    private lateinit var localComicPicList: List<LocalComicPic>
    private val picDownloadConcurrentCount = 3

    suspend fun download() {
        try {
            localComic = localComicRepository.getLocalComic(comicChapterId).getOrThrow()
            localComicPicList =
                localComicPicRepository.getLocalComicPicList(comicChapterId).getOrThrow()
            localComicRepository.updateLocalComicDownloadingStatus(
                comicId = comicChapterId
            ).runOrThrow()
            downloadPicList()
            exportZipFile()
            localComicRepository.updateLocalComicCompleteStatus(
                comicId = comicChapterId
            ).runOrThrow()
            toastManager.show("下载成功")
        } catch (e: Throwable) {
            log("下载过程出错，${e.stackTraceToString()}")
            localComicRepository.updateLocalComicErrorStatus(
                comicId = comicChapterId,
                errorMessage = e.stackTraceToString()
            ).runOrThrow()
        }
    }

    @OptIn(ExperimentalAtomicApi::class, ExperimentalCoroutinesApi::class)
    private suspend fun downloadPicList() {
        val scrambleId = localComic.scrambleId
        val speed = localComic.speed
        val size = localComicPicList.size
        val completeCount = AtomicInt(0)
        val progressMutex = Mutex()
        localComicPicList
            .asFlow()
            .filter { !it.isComplete }
            .flatMapMerge(concurrency = picDownloadConcurrentCount) { item ->
                flow {
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
                            progressMutex.withLock {
                                localComicRepository.updateLocalComicProgress(
                                    comicChapterId,
                                    progress
                                ).runOrThrow()
                            }
                            localComicPicRepository.updateLocalComicPicCompleteStatus(
                                comicId = comicChapterId,
                                page = item.page,
                                md5 = md5(file)
                            ).runOrThrow()
                            log("$comicChapterId 下载 ${item.page} 图片成功")
                            emit(Unit)
                        }
                    }
                }.flowOn(Dispatchers.IO)
            }
            .collect()
    }

    private fun exportZipFile() {
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